package invaded.cc.core.database.redis.reader.impl;

import invaded.cc.core.database.redis.reader.Callback;
import invaded.cc.core.util.Clickable;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.perms.PermLevel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.minecraft.util.com.google.gson.JsonObject;

public class ReaderBroadcast implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String message = jsonObject.get("message").getAsString();

        if (!jsonObject.has("hover") && !jsonObject.has("click")) {
            Common.broadcastMessage(PermLevel.DEFAULT, message);
            return;
        }

        String[] hoverVal = jsonObject.get("hover").getAsString().split(";");

        boolean hover = Boolean.parseBoolean(hoverVal[0]);
        String hoverText = hoverVal[1];

        String[] clickV = jsonObject.get("click").getAsString().split(";");
        boolean click = Boolean.parseBoolean(clickV[0]);
        String clickCommand = clickV[1];

        if (hover || click) {
            Clickable clickable = new Clickable(message);

            if (hover) clickable.hover(HoverEvent.Action.SHOW_TEXT, hoverText);
            if (click) clickable.clickEvent(ClickEvent.Action.RUN_COMMAND, clickCommand);

            Common.broadcastMessage(PermLevel.DEFAULT, clickable.get());
        } else {
            Common.broadcastMessage(PermLevel.DEFAULT, message);
        }
    }
}
