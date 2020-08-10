package invaded.cc.profile;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import invaded.cc.Core;
import invaded.cc.grant.GrantHandler;
import invaded.cc.manager.RequestHandler;
import invaded.cc.punishment.Punishment;
import invaded.cc.punishment.PunishmentHandler;
import jodd.http.HttpResponse;
import lombok.Getter;
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
        profiles.putIfAbsent(uuid, new Profile(uuid, name));
        Map<String, Object> body = new HashMap<>();

        body.put("uuid", uuid.toString());
        body.put("name", name);

        HttpResponse response = RequestHandler.post("/users", body);
        Profile profile = profiles.get(uuid);

        JsonObject jsonObject = new JsonParser().parse(response.body()).getAsJsonObject();

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

  /*  public Profile load(UUID uuid, String name) {
        GrantHandler grantHandler = Core.getInstance().getGrantHandler();
        profiles.putIfAbsent(uuid, new Profile(uuid, name));

        Profile profile = profiles.get(uuid);
        profile.setGrants(grantHandler.get(profile));

        MongoCollection<Document> collection = Core.getInstance().getDb().getCollection("playerData");
        Document find = collection.find(Filters.eq("uuid", uuid.toString())).first();

        if (find != null) {
            if (find.containsKey("color"))
                profile.setChatColor(find.getString("color").equals("none") ? null : ChatColor.valueOf(find.getString("color")));
            if (find.containsKey("italic")) profile.setItalic(find.getBoolean("italic"));
            if (find.containsKey("bold")) profile.setBold(find.getBoolean("bold"));
            if (find.containsKey("privateMessages")) profile.setMessages(find.getBoolean("privateMessages"));
            if (find.containsKey("privateMessagesSound"))
                profile.setMessagesSound(find.getBoolean("privateMessagesSound"));
            if (find.containsKey("ignoreList")) profile.setIgnoreList(find.getList("ignoreList", String.class));
            if (find.containsKey("allowDisguise")) profile.setAllowDisguise(find.getBoolean("allowDisguise"));

            if (find.containsKey("disguised") && find.getBoolean("disguised")) {
                if (find.containsKey("fakeName")) profile.setFakeName(find.getString("fakeName"));
                if (find.containsKey("fakeRank"))
                    profile.setFakeRank(Core.getInstance().getRankHandler().getRankOrDefault(find.getString("fakeRank")));
                if (find.containsKey("fakeSkin"))
                    profile.setFakeSkin(SkinParser.unParse(find.getString("fakeSkin").split(";")));

                GameProfile fakeProfile = new GameProfile(profile.getId(), name);
                fakeProfile.getProperties().put("textures", new Property("textures", profile.getFakeSkin().getTexture(), profile.getFakeSkin().getSignature()));

                profile.setFakeProfile(fakeProfile);
            }
        }

        profile.setHighestRank(grantHandler.getHighestGrant(profile.getGrants()));

        if (profile.getPermissions() == null) profile.setPermissions(new HashSet<>());

        profile.getGrants().forEach(grant -> {
            if (!grant.isUse()) return;

            Core.getInstance().getRankHandler().getRankOrDefault(grant.getRank()).getPermissions().forEach(permission -> profile.getPermissions().add(permission));
        });

        profile.setLoaded(true);
        return profile;
    }*/

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

        if(profile.getBan() != null) {
            JsonObject ban = new JsonObject();
            Punishment p = profile.getBan();

            ban.addProperty("type", p.getType().toString());
            ban.addProperty("punishedAt", p.getPunishedAt());
            ban.addProperty("expire", p.getExpire());
            ban.addProperty("removedAt", p.getRemovedAt());
            ban.addProperty("cheaterUuid", p.getCheaterUuid().toString());
            ban.addProperty("cheaterName", p.getCheaterName());
            ban.addProperty("staffName", p.getStaffName());
            ban.addProperty("silent", p.isSilent());
            ban.addProperty("removedBy", p.getRemovedBy() == null ? "" : p.getRemovedBy());
            ban.addProperty("reason", p.getReason());

            body.put("ban", ban.toString());
        }

        if(profile.getMute() != null) {
            JsonObject mute = new JsonObject();
            Punishment p = profile.getMute();

            mute.addProperty("type", p.getType().toString());
            mute.addProperty("punishedAt", p.getPunishedAt());
            mute.addProperty("expire", p.getExpire());
            mute.addProperty("removedAt", p.getRemovedAt());
            mute.addProperty("cheaterUuid", p.getCheaterUuid().toString());
            mute.addProperty("cheaterName", p.getCheaterName());
            mute.addProperty("staffName", p.getStaffName());
            mute.addProperty("silent", p.isSilent());
            mute.addProperty("removedBy", p.getRemovedBy() == null ? "" : p.getRemovedBy());
            mute.addProperty("reason", p.getReason());

            body.put("mute", mute.toString());
        }

        HttpResponse response = RequestHandler.put("/users/uuid/" + profile.getId().toString(), body);
        response.close();
    }

  /*  public void save(Profile profile) {
        Document document = new Document("uuid", profile.getId().toString());

        document.append("name", profile.getName())
                .append("color", profile.getChatColor() == null ? "none" : profile.getChatColor().name())
                .append("italic", profile.isItalic())
                .append("bold", profile.isBold())
                .append("privateMessages", profile.isMessages())
                .append("privateMessagesSound", profile.isMessagesSound())
                .append("ignoreList", profile.getIgnoreList())
                .append("allowDisguise", profile.isAllowDisguise());

        if (profile.isDisguised()) {
            document.append("disguised", true)
                    .append("fakeName", profile.getFakeName())
                    .append("fakeSkin", SkinParser.parse(profile.getFakeSkin()))
                    .append("fakeRank", profile.getFakeRank().getName());
        }

        MongoCollection<Document> collection = Core.getInstance().getDb().getCollection("playerData");

        Document found = collection.find(Filters.eq("uuid", profile.getId().toString())).first();

        if (found != null) collection.replaceOne(found, document);
        else collection.insertOne(document);
    }*/

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
}
