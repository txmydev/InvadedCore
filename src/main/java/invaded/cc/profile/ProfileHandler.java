package invaded.cc.profile;

import com.google.common.collect.Lists;
import invaded.cc.Basic;
import invaded.cc.grant.GrantHandler;
import invaded.cc.manager.RequestHandler;
import invaded.cc.tags.Prefix;
import invaded.cc.tags.Tag;
import invaded.cc.tags.TagsHandler;
import invaded.cc.punishment.PunishmentHandler;
import invaded.cc.util.Color;
import invaded.cc.util.json.JsonChain;
import jodd.http.HttpResponse;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfileHandler {

    private final Map<UUID, Profile> profiles;
    private final List<UUID> deletingPrefix;

    public ProfileHandler() {
        this.profiles = new ConcurrentHashMap<>();
        this.deletingPrefix = new ArrayList<>();
    }

    public Profile load(UUID uuid, String name) {
        return load(uuid, name, true);
    }

    public void save(Profile profile) {
        Map<String, Object> body = new HashMap<>();

        body.put("uuid", profile.getId().toString());
        body.put("name", profile.getName());
        body.put("color", profile.getChatColor() == null ? "none" : profile.getChatColor().name());
        body.put("bold", false);
        body.put("italic", profile.isItalic());
        body.put("privateMessages", profile.isMessages());
        body.put("privateMessagesSound", profile.isMessagesSound());
        body.put("allowDisguise", profile.isAllowDisguise());
        body.put("ignoreList", profile.getIgnoreList());
        body.put("spaceBetweenRank", profile.isSpaceBetweenRank());
        body.put("tags", getTagListToJson(profile));
        body.put("activePrefix", profile.getActivePrefix() == null ? "none" : profile.getActivePrefix().getId());
        body.put("activeSuffix", profile.getActiveSuffix() == null ? "none" : profile.getActiveSuffix().getId());
        body.put("coins", profile.getCoins());

        HttpResponse response = RequestHandler.post("/profiles", body);
        response.close();
    }

    private List<String> getTagListToJson(Profile profile) {
        List<String> list = Lists.newArrayList();

        profile.getTags().forEach(prefix -> {
            list.add(new JsonChain().addProperty("id", prefix.getId()).addProperty("display", prefix.getDisplay()).get().toString());
        });

        return list;
    }

    public Profile getProfile(UUID uuid) {
        return profiles.get(uuid);
    }
    public Profile getProfile(Player player){
        return getProfile(player.getUniqueId());
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
            profiles.putIfAbsent(uuid, new Profile(uuid, name));
        }

        Map<String, Object> body = new HashMap<>();

        body.put("uuid", uuid.toString());
        body.put("name", name);

        HttpResponse response = RequestHandler.post("/profiles", body);
        Profile profile = cache ? profiles.get(uuid) : new Profile(uuid, name);

        JsonObject jsonObject = new JsonParser().parse(response.bodyText()).getAsJsonObject();

        if (jsonObject.has("color")) profile.setChatColor(jsonObject.get("color").getAsString().equals("none") ? null : ChatColor.valueOf(jsonObject.get("color").getAsString()));
        if (jsonObject.has("italic")) profile.setItalic(jsonObject.get("italic").getAsBoolean());
        // if (jsonObject.has("bold")) profile.setBold(jsonObject.get("bold").getAsBoolean());
        if (jsonObject.has("privateMessages")) profile.setMessages(jsonObject.get("privateMessages").getAsBoolean());
        if (jsonObject.has("privateMessagesSound"))
            profile.setMessagesSound(jsonObject.get("privateMessagesSound").getAsBoolean());

        if (jsonObject.has("ignoreList")) {
            jsonObject.get("ignoreList").getAsJsonArray().forEach(element -> profile.getIgnoreList().add(element.getAsString()));
        }

        TagsHandler tagsHandler = Basic.getInstance().getTagsHandler();

        if(jsonObject.has("activePrefix")) {
            String pref = jsonObject.get("activePrefix").getAsString();
            if(pref.equals("none") || tagsHandler.getPrefix(pref) == null) profile.setActivePrefix(null);
            else profile.setActivePrefix(tagsHandler.getPrefix(pref));
        }

        if(jsonObject.has("tags")) {
            JsonParser parser = new JsonParser();
            jsonObject.get("tags").getAsJsonArray().forEach(element -> {
                JsonObject object = parser.parse(element.getAsString()).getAsJsonObject();
                Tag tag = tagsHandler.getPrefix(object.get("id").getAsString());
                if(tag == null) return;
                profile.getTags().add(tag);
            });
        }

        if(jsonObject.has("activeSuffix")) {
            String suffix = jsonObject.get("activeSuffix").getAsString();
            if(suffix.equals("none") || tagsHandler.getPrefix(suffix) == null) profile.setActivePrefix(null);
            else profile.setActivePrefix(tagsHandler.getPrefix(suffix));
        }

        if(jsonObject.has("coins")) profile.setCoins(jsonObject.get("coins").getAsInt());

        if(jsonObject.has("spaceBetweenRank")) profile.setSpaceBetweenRank(jsonObject.get("spaceBetweenRank").getAsBoolean());

        GrantHandler grantHandler = Basic.getInstance().getGrantHandler();

        profile.setGrants(grantHandler.get(profile));
        profile.setHighestRank(grantHandler.getHighestGrant(profile.getGrants()));

        profile.setBan(null);
        profile.setMute(null);

        PunishmentHandler punishmentHandler = Basic.getInstance().getPunishmentHandler();
        punishmentHandler.load(profile);

        if (jsonObject.has("allowDisguise")) profile.setAllowDisguise(jsonObject.get("allowDisguise").getAsBoolean());
        response.close();

        grantHandler.setupPermissions(profile);

        profile.setLoaded(true);
        return profile;
    }

    public void ifPresent(UUID id, ProfileCallback callback, CommandSender sender, String s) {
        Profile profile = getProfile(id);
        if(profile == null) {
            sender.sendMessage(Color.translate(s));
            return;
        }

        callback.apply(profile);
    }

    public interface ProfileCallback {

        void apply(Profile profile);

    }
}
