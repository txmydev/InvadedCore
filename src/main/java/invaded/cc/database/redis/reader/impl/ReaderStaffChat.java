package invaded.cc.database.redis.reader.impl;

import com.google.gson.JsonObject;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;
import invaded.cc.util.Common;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.ChatColor;

import java.util.UUID;

public class ReaderStaffChat implements Callback<JsonObject>{
    @Override
    public void callback(JsonObject jsonObject) {
        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = profileHandler.getProfile(UUID.fromString(profileId));
        String message = jsonObject.get("message").getAsString();
        String serverId = jsonObject.get("server-id").getAsString();

        ChatColor color = ChatColor.AQUA;

        if(serverId.contains("uhc")) color = ChatColor.LIGHT_PURPLE;
        else if(serverId.contains("meetup")) color = ChatColor.GREEN;

        String sv = serverId.replace("sa-", "");
        Common.broadcastMessage(PermLevel.STAFF, color + "[" + sv + "] " + profile.getRealColoredName() + "&7: " + color + message);
    }
}
