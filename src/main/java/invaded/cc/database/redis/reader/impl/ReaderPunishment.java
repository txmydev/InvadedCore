package invaded.cc.database.redis.reader.impl;

import invaded.cc.Core;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.Punishment;
import net.minecraft.util.com.google.gson.JsonObject;

import java.util.UUID;

public class ReaderPunishment implements Callback<JsonObject> {

    @Override
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

        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(cheaterUuid);

        Punishment punishment = new Punishment(type, punishedAt, expire, cheaterName, cheaterUuid, staffName, s, reason);

        if(type == Punishment.Type.MUTE || type == Punishment.Type.TEMPORARY_MUTE){
            profile.setMute(punishment);
        } else if( type != Punishment.Type.WARN) {
            profile.setBan(punishment);
        }
    }

}
