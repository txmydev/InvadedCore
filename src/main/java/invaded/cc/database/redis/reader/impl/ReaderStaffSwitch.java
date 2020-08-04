package invaded.cc.database.redis.reader.impl;

import com.google.gson.JsonObject;
import invaded.cc.Core;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;
import invaded.cc.util.Common;
import invaded.cc.util.perms.PermLevel;

import java.util.UUID;

public class ReaderStaffSwitch implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = profileHandler.getProfile(UUID.fromString(profileId));

        String from = jsonObject.get("from").getAsString();
        String to = jsonObject.get("to").getAsString();

        String currentServer = Core.getInstance().getServerName();

        String message = "&3[Staff] " + profile.getRealColoredName() + " ";

        if(from.equals(currentServer)) message+="&cleft &bthis server to &7" + to;
        else if(to.equals(currentServer)) message += "&ajoined &bthis server from &7" + from;
        else message+= "&bjoined to &7" + to + " &bfrom &7"+from;

        Common.broadcastMessage(PermLevel.STAFF, message);
    }
}
