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

public class ReaderHelpop implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String serverId = jsonObject.get("server-id").getAsString();
        String currentServer = Spotify.SERVER_NAME;
        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = profileHandler.getProfile(UUID.fromString(profileId));

        String message = jsonObject.get("message").getAsString();
        boolean sameServer = serverId.equals(currentServer);

        Clickable clickable = new Clickable("&7[&4Help Request&7] &bRequested by " + profile.getColoredName()
                + "&b: &7" + message)
                .hover(HoverEvent.Action.SHOW_TEXT, sameServer
                        ? "&bClick to be teleported to him "
                        : "&bClick to go to " + serverId + " to teleport to him.");

        clickable.clickEvent(ClickEvent.Action.RUN_COMMAND, sameServer ?
                "/tp " + profile.getName() : "/join " + serverId);

        Common.broadcastMessage(PermLevel.STAFF, clickable.get());
    }
}
