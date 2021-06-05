package invaded.core.database.redis.reader.impl;

import invaded.core.database.redis.reader.Callback;
import net.minecraft.util.com.google.gson.JsonObject;

public class ReaderRemovePunishment implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {

    }
 /*   @Override
    public void callback(JsonObject jsonObject) {
        String typeName = jsonObject.get("type").getAsString();
        Punishment.Type type = Punishment.Type.valueOf(typeName);
        String cheaterName = jsonObject.get("cheaterName").getAsString();
        String cheaterUuidStr = jsonObject.get("cheaterUuid").getAsString();
        UUID cheaterUuid = UUID.fromString(cheaterUuidStr);
        long expire = jsonObject.get("expire").getAsLong();
        long punishedAt = jsonObject.get("punishedAt").getAsLong();
        String staffName = jsonObject.get("staffName").getAsString();
        boolean s = jsonObject.get("s").getAsBoolean();
        String reason = jsonObject.get("reason").getAsString();
        long removedAt = jsonObject.get("removedAt").getAsLong();
        String removedBy = jsonObject.get("removedBy").getAsString();
        Profile profile = profileHandler.getProfile(cheaterUuid);

        PunishmentHandler punishmentHandler = Core.getInstance().getPunishmentHandler();
        Punishment punishment = punishmentHandler.getPunishment(punishedAt, cheaterUuid);

        if(punishment == null) {
            punishment = new Punishment(type, punishedAt, expire, cheaterName, cheaterUuid, staffName, s, reason);
            punishmentHandler.getPunishments().add(punishment);
        }

        punishment.setRemovedAt(removedAt);
        punishment.setRemovedBy(removedBy);
        punishment.setExpire(0L);

        if(type == Punishment.Type.MUTE || type == Punishment.Type.TEMPORARY_MUTE) profile.setMute(null);
        else if (type != Punishment.Type.WARN) profile.setBan(null);

        punishmentHandler.requestUnPunish(punishment);
    }*/
}
