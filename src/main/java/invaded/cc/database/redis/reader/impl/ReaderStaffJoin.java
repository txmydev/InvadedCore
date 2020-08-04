package invaded.cc.database.redis.reader.impl;

import com.google.gson.JsonObject;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;
import invaded.cc.util.Common;
import invaded.cc.util.perms.PermLevel;

import java.util.UUID;

public class ReaderStaffJoin implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = profileHandler.getProfile(UUID.fromString(profileId));

        Common.broadcastMessage(PermLevel.STAFF
        , "&3[Staff] &b" + profile.getRealColoredName()
        + " &ajoined &bthe network.");
    }
}
