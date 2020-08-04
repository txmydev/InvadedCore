package invaded.cc.punishment;

import com.mongodb.client.MongoCollection;
import invaded.cc.Core;
import invaded.cc.database.Database;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import lombok.Getter;
import org.bson.Document;

import java.util.*;
import java.util.function.Consumer;

@Getter
public class PunishmentHandler {

    private Set<Punishment> punishments;
    private final ProfileHandler profileHandler;

    public PunishmentHandler() {
        this.punishments = new HashSet<>();
        profileHandler = Core.getInstance().getProfileHandler();

        load();
    }

    public void load() {
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
    }
}
