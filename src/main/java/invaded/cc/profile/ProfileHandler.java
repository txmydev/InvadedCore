package invaded.cc.profile;

import invaded.cc.Core;
import invaded.cc.grant.GrantHandler;
import invaded.cc.manager.RequestHandler;
import invaded.cc.punishment.PunishmentHandler;
import jodd.http.HttpResponse;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfileHandler {

    private final Map<UUID, Profile> profiles;

    public ProfileHandler() {
        this.profiles = new ConcurrentHashMap<>();
    }

    public Profile load(UUID uuid, String name) {
        return load(uuid, name, true);
    }

    public void save(Profile profile) {
        Map<String, Object> body = new HashMap<>();

        body.put("uuid", profile.getId().toString());
        body.put("name", profile.getName());
        body.put("color", profile.getChatColor() == null ? "none" : profile.getChatColor().name());
        body.put("bold", profile.isBold());
        body.put("italic", profile.isItalic());
        body.put("privateMessages", profile.isMessages());
        body.put("privateMessagesSound", profile.isMessagesSound());
        body.put("allowDisguise", profile.isAllowDisguise());
        body.put("ignoreList", profile.getIgnoreList());

        Map<String, Object> query = new HashMap<>();
        query.put("uuid", profile.getId().toString());
        query.put("name", profile.getName());

        HttpResponse response = RequestHandler.put("/profiles", body, query);
        response.close();
    }

    public Profile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }

    public boolean canDisguise(String arg) {
        for (Profile val : profiles.values()) {
            if (val.getName().equalsIgnoreCase(arg)
                    || (val.getFakeName() != null && val.getFakeName().equalsIgnoreCase(arg))) {
                return false;
            }
        }

        return true;
    }

    public Profile load(UUID uuid, String name, boolean cache) {
        if(cache) {
            if (profiles.containsKey(uuid)) profiles.remove(uuid);

            profiles.putIfAbsent(uuid, new Profile(uuid, name));
        }

        Map<String, Object> body = new HashMap<>();

        body.put("uuid", uuid.toString());
        body.put("name", name);

        HttpResponse response = RequestHandler.post("/profiles", body);
        Profile profile = profiles.get(uuid);

        JsonObject jsonObject = new JsonParser().parse(response.bodyText()).getAsJsonObject();

        if (jsonObject.has("color"))
            profile.setChatColor(jsonObject.get("color").getAsString().equals("none") ? null : ChatColor.valueOf(jsonObject.get("color").getAsString()));
        if (jsonObject.has("italic")) profile.setItalic(jsonObject.get("italic").getAsBoolean());
        if (jsonObject.has("bold")) profile.setBold(jsonObject.get("bold").getAsBoolean());
        if (jsonObject.has("privateMessages")) profile.setMessages(jsonObject.get("privateMessages").getAsBoolean());
        if (jsonObject.has("privateMessagesSound"))
            profile.setMessagesSound(jsonObject.get("privateMessagesSound").getAsBoolean());

        if (jsonObject.has("ignoreList")) {
            List<String> list = new ArrayList<>();
            jsonObject.get("ignoreList").getAsJsonArray().forEach(element -> list.add(element.getAsString()));

            profile.setIgnoreList(list);
        }

        GrantHandler grantHandler = Core.getInstance().getGrantHandler();

        profile.setGrants(grantHandler.get(profile));
        profile.setHighestRank(grantHandler.getHighestGrant(profile.getGrants()));

        profile.setBan(null);
        profile.setMute(null);

        PunishmentHandler punishmentHandler = Core.getInstance().getPunishmentHandler();
        punishmentHandler.load(profile);

        if (jsonObject.has("allowDisguise")) profile.setAllowDisguise(jsonObject.get("allowDisguise").getAsBoolean());
        response.close();

        grantHandler.setupPermissions(profile);

        profile.setLoaded(true);
        return profile;
    }
}