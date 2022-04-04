package invaded.cc.core.database.punishments.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.MongoDatabase;
import invaded.cc.core.database.punishments.PunishmentStorage;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.Punishment;
import invaded.cc.core.util.Task;
import org.bson.Document;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class MongoPunishmentStorage implements PunishmentStorage {

    private MongoCollection<Document> punishments, activePunishments;

    public MongoPunishmentStorage(MongoDatabase database) {
        this.punishments = database.getDatabase().getCollection("punishments");
        this.activePunishments = database.getDatabase().getCollection("activePunishments");
    }

    @Override
    public void punish(UUID id, String name, Punishment punishment) {
        Document punishmentBody = new Document();

        punishmentBody.put("cheaterName", name);
        punishmentBody.put("cheaterUuid", id.toString());
        punishmentBody.put("staffName", punishment.getStaffName());
        punishmentBody.put("reason", punishment.getReason());
        punishmentBody.put("silent", punishment.isSilent());
        punishmentBody.put("type", punishment.getType().name());
        punishmentBody.put("punishedAt", punishment.getPunishedAt());
        punishmentBody.put("expire", punishment.getExpire());
        punishmentBody.put("address", punishment.getAddress());

        activePunishments.insertOne(punishmentBody);

        if (punishment.getType() == Punishment.Type.MUTE || punishment.getType() == Punishment.Type.TEMPORARY_MUTE)
            return;

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        profileHandler.getProfiles().remove(punishment.getCheaterUuid());
    }

    @Override
    public void pardon(UUID uuid, Punishment punishment) {
        Task.async(() -> {
            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
            Optional<Profile> profileOptional = Optional.ofNullable(profileHandler.getProfile(uuid));
            Profile profile1 = profileOptional.orElse(profileHandler.load(uuid, punishment.getCheaterName(), false));
            profile1.setBan(null);
            move(profile1, punishment);
        });
    }

    @Override
    public void load(Profile profile) {
        activePunishments.find(Filters.eq("cheaterUuid", profile.getId().toString()))
                .forEach((Consumer<Document>) document -> {
                    long expire = document.getLong("expire");
                    String json = document.toJson();

                    if (expire < System.currentTimeMillis() && document.getString("type").contains("TEMPORARY")) {
                        move(profile, Spotify.GSON.fromJson(json, Punishment.class));
                        return;
                    }

                    Punishment punishment = new Punishment(Punishment.Type.valueOf(document.getString("type")),
                            document.getLong("punishedAt"), document.getLong("expire"), document.getString("cheaterName"),
                            UUID.fromString(document.getString("cheaterUuid")), document.getString("staffName"), document.getBoolean("silent"),
                            document.getString("reason"), document.getString("address"));

                    switch (document.getString("type")) {
                        case "BAN":
                        case "TEMPORARY_BAN":
                        case "BLACKLIST":
                            profile.setBan(punishment);
                           // profile.setBan(Spotify.GSON.fromJson(json, Punishment.class));
                            break;
                        default:
                            profile.setMute(punishment);
                           // profile.setMute(Spotify.GSON.fromJson(json, Punishment.class));
                            break;
                    }
                });
    }

    @Override
    public void move(UUID id, String name, Punishment punishment) {
        Document punishmentBody = new Document();

        punishmentBody.put("cheaterName", name == null ? "null" : name);
        punishmentBody.put("cheaterUuid", id.toString());
        punishmentBody.put("staffName", punishment.getStaffName());
        punishmentBody.put("reason", punishment.getReason());
        punishmentBody.put("silent", punishment.isSilent());
        punishmentBody.put("type", punishment.getType().name());
        punishmentBody.put("punishedAt", punishment.getPunishedAt());
        punishmentBody.put("removedBy", punishment.getRemovedBy());
        punishmentBody.put("removedAt", punishment.getRemovedAt());
        punishmentBody.put("address", punishment.getAddress());

        punishments.insertOne(punishmentBody);

        System.out.println("Moved a punishment from activePunishments to punishments (holder " + name + ")");

        activePunishments.findOneAndDelete(new Document("cheaterUuid", id.toString())
                .append("type", punishment.getType().name())
                .append("expire", punishment.getExpire())
                .append("staffName", punishment.getStaffName())
                .append("punishedAt", punishment.getPunishedAt()));
    }

    @Override
    public void move(Profile profile, Punishment punishment) {
        this.move(profile.getId(), profile.getName(), punishment);
    }
}
