package invaded.cc.database.redis.reader.impl;

import invaded.cc.Basic;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;
import invaded.cc.util.Common;
import invaded.cc.util.perms.PermLevel;
import net.minecraft.util.com.google.gson.JsonObject;

import java.util.UUID;

public class ReaderStaffSwitch implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String profileId = jsonObject.get("profileId").getAsString();
        String name = jsonObject.get("name").getAsString();
        String from = jsonObject.get("from").getAsString();
        String to = jsonObject.get("to").getAsString();

        Profile profile = profileHandler.getProfile(UUID.fromString(profileId));
        if (profile == null) profile = profileHandler.load(UUID.fromString(profileId), name, false);

        String current = Basic.getInstance().getServerName();
        String message = "&3[Staff] " + profile.getColoredName() + " ";

        if (from.equals(current)) Common.broadcastMessage(PermLevel.STAFF, message + "&cleft &bthis server to &7" + to);
        else if (to.equals(current)) Common.broadcastMessage(PermLevel.STAFF, message +"&ajoined &bthis server from &7" + from);
    }
}
