package invaded.cc.profile;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import invaded.cc.Core;
import invaded.cc.grant.GrantHandler;
import invaded.cc.rank.Rank;
import invaded.cc.util.SkinParser;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class ProfileHandler {

    private final Map<UUID, Profile> profiles;

    public ProfileHandler() {
        this.profiles = new ConcurrentHashMap<>();
    }

    public Profile load(UUID uuid, String name) {
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
        profile.getGrants().forEach(grant ->
        {
            Core.getInstance().getRankHandler().getRankOrDefault(grant.getRank()).getPermissions().forEach(permission -> profile.getPermissions().add(permission));
        });

        profile.setLoaded(true);
        return profile;
    }

    public void save(Profile profile) {
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
}
