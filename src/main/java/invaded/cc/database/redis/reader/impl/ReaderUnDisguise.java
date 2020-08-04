package invaded.cc.database.redis.reader.impl;

import com.google.gson.JsonObject;
import invaded.cc.Core;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;

import java.util.UUID;

public class ReaderUnDisguise implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
     /*   String serverId = jsonObject.get("server-id").getAsString();
        String currentServer = Core.getInstance().getServerName();

        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = Profile.getByUuid(UUID.fromString(profileId));

        if(serverId.equals(currentServer)) {
            profile.unDisguise();
        }else {
            profile.setFakeProfile(null);
            profile.setFakeRank(null);
            profile.setFakeSkin(null);
            profile.setFakeName(null);
        }*/
    }
}
