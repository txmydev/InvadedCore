package invaded.cc.database.redis.reader.impl;

import com.google.gson.JsonObject;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;
import org.bukkit.ChatColor;

import java.util.UUID;

public class ReaderColorUpdate implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
       /* String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = Profile.getByUuid(UUID.fromString(profileId));

        String color = jsonObject.get("color").getAsString();
        boolean bold = jsonObject.get("bold").getAsBoolean();
        boolean italic = jsonObject.get("italic").getAsBoolean();

        profile.setChatColor(color.equals("none") ? null : ChatColor.valueOf(color));
        profile.setBold(bold);
        profile.setItalic(italic);*/
    }
}
