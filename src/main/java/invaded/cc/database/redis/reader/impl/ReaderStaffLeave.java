package invaded.cc.database.redis.reader.impl;

import com.google.gson.JsonObject;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;
import invaded.cc.util.Common;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;

import java.util.UUID;

public class ReaderStaffLeave implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String profileId = jsonObject.get("profileId").getAsString();
        String coloredName = jsonObject.get("coloredName").getAsString();

        Common.broadcastMessage(PermLevel.STAFF
                , "&3[Staff] &b" + coloredName
                        + " &4left &bthe network.");
    }
}
