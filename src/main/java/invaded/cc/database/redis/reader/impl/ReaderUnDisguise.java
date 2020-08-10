package invaded.cc.database.redis.reader.impl;

import com.google.gson.JsonObject;
import invaded.cc.Core;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.manager.DisguiseHandler;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;

import java.util.UUID;

public class ReaderUnDisguise implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String serverId = jsonObject.get("server-id").getAsString();
        String currentServer = Core.getInstance().getServerName();
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = profileHandler.getProfile(UUID.fromString(profileId));

        DisguiseHandler.getDisguisedPlayers().remove(UUID.fromString(profileId));
        if(profile == null) return;

        if(serverId.equals(currentServer)) {
            profile.unDisguise();
        }else {
            profile.setFakeProfile(null);
            profile.setFakeRank(null);
            profile.setFakeSkin(null);
            profile.setFakeName(null);
        }
    }
}
