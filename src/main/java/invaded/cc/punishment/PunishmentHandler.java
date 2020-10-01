package invaded.cc.punishment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import invaded.cc.Core;
import invaded.cc.manager.RequestHandler;
import invaded.cc.profile.Profile;
import jodd.http.HttpResponse;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class PunishmentHandler {

    public void punish(UUID id, String name, Punishment punishment) {
        Map<String, Object> punishmentBody = new HashMap<>();

        punishmentBody.put("cheaterName", name);
        punishmentBody.put("cheaterUuid", id.toString());
        punishmentBody.put("staffName", punishment.getStaffName());
        punishmentBody.put("reason", punishment.getReason());
        punishmentBody.put("silent", punishment.isSilent());
        punishmentBody.put("type", punishment.getType().name());
        punishmentBody.put("punishedAt", punishment.getPunishedAt());
        punishmentBody.put("expire", punishment.getExpire());

        HttpResponse response = RequestHandler.post("/activePunishments", punishmentBody);
        response.close();
    }

    public void pardon(UUID uuid, Punishment punishment) {
        Map<String, Object> query = new HashMap<>();

        query.put("type", punishment.getType().name());
        query.put("cheaterUuid", uuid.toString());
        query.put("punishedAt", punishment.getPunishedAt());

        HttpResponse response = RequestHandler.delete("/activePunishments", query);
        response.close();

        if(response.statusCode() != 200) {
            Bukkit.getLogger().info("Request Handler - Failed to pardon " + punishment.getCheaterName() + " with response: " + response.bodyText());
            return;
        }

        Optional<Profile> op = Optional.of(Core.getInstance().getProfileHandler().getProfile(uuid));
        if(!punishment.isBan() && op.isPresent()) op.get().setMute(null);
    }

    public void load(Profile profile) {
        Map<String, Object> query = new HashMap<>();

        query.put("cheaterUuid", profile.getId().toString());
        query.put("cheaterName", profile.getName());

        HttpResponse response = RequestHandler.get("/activePunishments", query);

        if(response.statusCode() == 200) {
            JsonArray jsonArray = new JsonParser().parse(response.bodyText()).getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                long expire = jsonObject.get("expire").getAsLong();

                if(expire < System.currentTimeMillis() && jsonObject.get("type").getAsString().contains("TEMPORARY")) {
                    move(profile, Core.GSON.fromJson(jsonObject.toString(), Punishment.class));
                    continue;
                }

                System.out.println("Active punishment on user" + profile.getName());

                switch (jsonObject.get("type").getAsString()) {
                    case "BAN":
                    case "TEMPORARY_BAN":
                    case "BLACKLIST":
                        profile.setBan(Core.GSON.fromJson(jsonObject.toString(), Punishment.class));
                        break;
                    default:
                        profile.setMute(Core.GSON.fromJson(jsonObject.toString(), Punishment.class));
                        break;
                }
            }
        }

        response.close();
    }

    private void move(Profile profile, Punishment punishment) {
        Map<String, Object> punishmentBody = new HashMap<>();

        punishmentBody.put("cheaterName", profile.getName());
        punishmentBody.put("cheaterUuid", profile.getId().toString());
        punishmentBody.put("staffName", punishment.getStaffName());
        punishmentBody.put("reason", punishment.getReason());
        punishmentBody.put("silent", punishment.isSilent());
        punishmentBody.put("type", punishment.getType().name());
        punishmentBody.put("punishedAt", punishment.getPunishedAt());
        punishmentBody.put("removedBy", punishment.getRemovedBy());
        punishmentBody.put("removedAt", punishment.getRemovedAt());

        HttpResponse post = RequestHandler.post("/punishments", punishmentBody);
        post.close();

        System.out.println("Moved a punishment from activePunishments to punishments (holder " + profile.getName()+ ")");

        Map<String, Object> deleteQuery = new HashMap<>();

        deleteQuery.put("cheaterUuid", profile.getId().toString());
        deleteQuery.put("cheaterName", profile.getName());
        deleteQuery.put("type", punishment.getType().name());
        deleteQuery.put("expire", punishment.getExpire());
        deleteQuery.put("staffName", punishment.getStaffName());
        deleteQuery.put("punishedAt", punishment.getPunishedAt());

        HttpResponse delete = RequestHandler.delete("/activePunishments", deleteQuery);
        delete.close();
    }

   /* public void load() {
        Database db = Core.getInstance().getDb();

        db.getCollection("punishments").find().forEach((Consumer<? super Document>) document -> {
            Punishment punishment = new Punishment(Punishment.Type.valueOf(document.getString("type")),
                    document.getLong("punishedAt"), document.getLong("expire")
                    , document.getString("cheaterName"), UUID.fromString(document.getString("cheaterUuid"))
                    , document.getString("staffName"), document.getBoolean("s"), document.getString("reason"));

            punishment.setRemovedBy(document.getString("removedBy"));
            punishment.setRemovedAt(document.getLong("removedAt"));

            punishments.add(punishment);
        });

        db.getCollection("activePunishments").find().forEach((Consumer<? super Document>) document -> {
            Punishment punishment = new Punishment(Punishment.Type.valueOf(document.getString("type")),
                    document.getLong("punishedAt"), document.getLong("expire")
                    , document.getString("cheaterName"), UUID.fromString(document.getString("cheaterUuid"))
                    , document.getString("staffName"), document.getBoolean("s"), document.getString("reason"));

            Profile profile = Core.getInstance().getProfileHandler().getProfile(punishment.getCheaterUuid());
            if(profile == null) profile = profileHandler.load(punishment.getCheaterUuid(), punishment.getCheaterName());

            switch (punishment.getType()) {
                case MUTE:
                case TEMPORARY_MUTE:
                    profile.setMute(punishment);
                    break;
                case WARN:
                    break;
                default:
                    profile.setBan(punishment);
                    break;
            }
        });
    }

    public void unload() {
        for (Punishment punishment : punishments) {
            Profile profile = Core.getInstance().getProfileHandler().getProfile(punishment.getCheaterUuid());
            if(profile == null) continue;

            switch(punishment.getType()) {
                case MUTE:
                case TEMPORARY_MUTE:
                    if(profile.getMute() == null) {
                        requestSaveHistory(punishment);
                    }else {
                        if(punishment.getPunishedAt() == profile.getMute().getPunishedAt()
                        && punishment.getExpire() == profile.getMute().getExpire())
                            requestSaveActive(punishment);
                        else requestSaveHistory(punishment);
                    }
                    break;
                case WARN:
                    break;
                default:
                    if(profile.getBan() == null) {
                        requestSaveHistory(punishment);
                    } else {
                        if(punishment.getPunishedAt() == profile.getBan().getPunishedAt()
                                && punishment.getExpire() == profile.getBan().getExpire())
                            requestSaveActive(punishment);
                        else requestSaveHistory(punishment);
                    }
                    break;
            }
        }
    }

    public void requestSaveActive(Punishment punishment) {
        Database db = Core.getInstance().getDb();

        MongoCollection<Document> collection = db.getCollection("activePunishments");

        Document find = collection.find(new Document("punishedAt", punishment.getPunishedAt())
                .append("type", punishment.getType().name()).append("cheaterUuid", punishment.getCheaterUuid().toString())).first();

        Document document = new Document("punishedAt", punishment.getPunishedAt());

        document.append("type", punishment.getType().name())
                .append("cheaterName", punishment.getCheaterName())
                .append("cheaterUuid", punishment.getCheaterUuid().toString())
                .append("expire", punishment.getExpire())
                .append("punishedAt", punishment.getPunishedAt())
                .append("staffName", punishment.getStaffName())
                .append("s", punishment.isS())
                .append("reason", punishment.getReason())
                .append("removedBy", punishment.getRemovedBy())
                .append("removedAt", punishment.getRemovedAt());


        if(find != null) {
            db.getCollection("punishments").insertOne(find);

            collection.replaceOne(find, document);
        }else {
            collection.insertOne(document);
        }
    }

    public void requestSaveHistory(Punishment punishment) {
        Database db = Core.getInstance().getDb();

        MongoCollection<Document> collection = db.getCollection("punishments");

        Document find = collection.find(new Document("punishedAt", punishment.getPunishedAt())
                .append("type", punishment.getType().name()).append("cheaterUuid", punishment.getCheaterUuid().toString())).first();

        Document document = new Document("punishedAt", punishment.getPunishedAt());

        document.append("type", punishment.getType().name())
                .append("cheaterName", punishment.getCheaterName())
                .append("cheaterUuid", punishment.getCheaterUuid().toString())
                .append("expire", punishment.getExpire())
                .append("punishedAt", punishment.getPunishedAt())
                .append("staffName", punishment.getStaffName())
                .append("s", punishment.isS())
                .append("reason", punishment.getReason())
                .append("removedBy", punishment.getRemovedBy())
                .append("removedAt", punishment.getRemovedAt());

        if (find == null) {
            collection.insertOne(document);
            return;
        }

        collection.replaceOne(find, document);
    }

    public void requestUnPunish(Punishment punishment) {
        Database db = Core.getInstance().getDb();

        MongoCollection<Document> collection = db.getCollection("activePunishments");
        Document find = collection.find(new Document("punishedAt", punishment.getPunishedAt())
                .append("type", punishment.getType().name()).append("cheaterUuid", punishment.getCheaterUuid().toString())).first();

        if (find == null) {
            throw new IllegalArgumentException("User " + punishment.getCheaterName() + " not in the active punishments collection!");
        }

        collection.deleteOne(find);
        collection = db.getCollection("punishments");

        find.replace("removedBy", punishment.getRemovedBy());
        find.replace("removedAt", punishment.getRemovedAt());

        collection.insertOne(find);
    }

    public Punishment getPunishment(long punishDate, UUID queryUuid) {
        for (Punishment punishment : punishments) {
            if (punishment.getPunishedAt() == punishDate
                    && punishment.getCheaterUuid().equals(queryUuid)) return punishment;
        }

        return null;
    }

    public List<Punishment> getPunishments(Profile profile) {
        List<Punishment> list = new ArrayList<>();

        for(Punishment punishment : punishments) {
            if(punishment.getCheaterUuid().equals(profile.getId())) list.add(punishment);
        }

        return list;
    }*/
}
