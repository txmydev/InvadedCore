package invaded.cc.core.database.redis.reader.impl;

import invaded.cc.core.database.redis.reader.Callback;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.perms.PermLevel;
import net.minecraft.util.com.google.gson.JsonObject;

import java.util.UUID;

public class ReaderStaffLeave implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String profileId = jsonObject.get("profileId").getAsString();
        String name = jsonObject.get("name").getAsString();
        String from = jsonObject.get("from").getAsString();

        Profile profile = profileHandler.getProfile(UUID.fromString(profileId));
        if (profile == null) profile = profileHandler.load(UUID.fromString(profileId), name, false);

        Common.broadcastMessage(PermLevel.STAFF
                , "&3[Staff] &b" + profile.getColoredName()
                        + " &4left &bthe network. &7(from " + from + ")");
    }
}
