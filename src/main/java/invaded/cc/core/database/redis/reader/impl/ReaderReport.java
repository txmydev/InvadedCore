package invaded.cc.core.database.redis.reader.impl;

import invaded.cc.core.Spotify;
import invaded.cc.core.database.redis.reader.Callback;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.Clickable;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.perms.PermLevel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.util.com.google.gson.JsonObject;

import java.util.UUID;

public class ReaderReport implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String serverId = jsonObject.get("server-id").getAsString();
        String currentServer = Spotify.getInstance().getServerName();
        boolean sameSv = serverId.equals(currentServer);

        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = profileHandler.getProfile(UUID.fromString(profileId));

        String reportedId = jsonObject.get("reportedId").getAsString();
        Profile reportedProfile = profileHandler.getProfile(UUID.fromString(reportedId));

        String reason = jsonObject.get("reason").getAsString();

        Clickable clickable = new Clickable("&7[&4Report&7] " + profile.getColoredName() + " &fhas reported " + reportedProfile.getColoredName()
                + " &fwith a reason of &b" + reason);

        clickable.hover(HoverEvent.Action.SHOW_TEXT, sameSv ? "&bClick to teleport " + reportedProfile.getColoredName()
                : "&bClick to go to &e" + serverId);

        clickable.clickEvent(ClickEvent.Action.RUN_COMMAND, sameSv ? "/tp " + reportedProfile.getName()
                : "/join " + serverId);

        Common.broadcastMessage(PermLevel.STAFF, clickable.get());
    }
}
