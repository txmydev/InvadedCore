package invaded.cc.core.database.redis.reader.impl;

import invaded.cc.core.Spotify;
import invaded.cc.core.database.redis.reader.Callback;
import invaded.cc.core.manager.DisguiseHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import net.minecraft.util.com.google.gson.JsonObject;

import java.util.UUID;

public class ReaderUnDisguise implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String serverId = jsonObject.get("server-id").getAsString();
        String currentServer = Spotify.getInstance().getServerName();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

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
