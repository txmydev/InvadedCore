package invaded.cc.core.database.player.impl;

import com.google.common.collect.Lists;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.player.PlayerStorage;
import invaded.cc.core.grant.GrantHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.punishment.PunishmentHandler;
import invaded.cc.core.tags.Tag;
import invaded.cc.core.tags.TagsHandler;
import invaded.cc.core.trails.Trail;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

public class MongoPlayerStorage implements PlayerStorage {

    private final ConcurrentMap<UUID, Profile> playerStorage = new ConcurrentHashMap<>();

    private MongoCollection<Document> collection;

    public MongoPlayerStorage(invaded.cc.core.database.MongoDatabase database) {
        this.collection = database.getDatabase().getCollection("profiles");
    }

    @Override
    public Profile load(UUID id, String name, boolean cache) {
        Profile profile = new Profile(id, name);

        Document document = this.collection.find(Filters.eq("uuid", id.toString())).first();
        if (document != null) {
            if (!profile.getName().equals(document.getString("name"))) profile.setName(document.getString("name"));
            if (document.containsKey("color"))
                profile.setChatColor(document.getString("color").equals("none") ? null : ChatColor.valueOf(document.getString("color")));

            profile.setItalic(document.getBoolean("italic", false));
            profile.setMessages(document.getBoolean("privateMessages", true));
            profile.setMessagesSound(document.getBoolean("privateMessagesSound", true));
            profile.setAllowDisguise(document.getBoolean("allowDisguise", false));

            profile.setIgnoreList(new HashSet<>(document.getList("ignoreList", String.class, new ArrayList<>())));
            profile.setSpaceBetweenRank(document.getBoolean("spaceBetweenRank", false));


            TagsHandler tagsHandler = Spotify.getInstance().getTagsHandler();
            profile.setTags(document.getList("tags", String.class, new ArrayList<>()).stream().map(tagsHandler::getTag).collect(Collectors.toSet()));
            profile.getTags().removeIf(Objects::isNull);

            profile.setActivePrefix(tagsHandler.getTag(document.getString("activePrefix")));
            profile.setActiveSuffix(tagsHandler.getTag(document.getString("activeSuffix")));
            profile.setCoins(document.getInteger("coins", 0));

            profile.setTrails(document.getList("trails", String.class, new ArrayList<>(Trail.values().length)).stream().map(Trail::getById).collect(Collectors.toSet()));

            try {
                profile.setActiveTrail(Trail.valueOf(document.getString("activeTrail")));
            } catch (IllegalArgumentException ignored) {
                profile.setActiveTrail(null);
            }

            profile.setBossBar(document.getBoolean("bossbar", true));
            profile.setLunarPrefix(document.getBoolean("lunarPrefix", false));
            profile.setLunarBorder(document.getBoolean("lunarBorder", true));
            profile.setAddress(document.getString("address"));
        }

        GrantHandler grantHandler = Spotify.getInstance().getGrantHandler();

        profile.setGrants(grantHandler.get(profile));
        profile.setHighestRank(grantHandler.getHighestGrant(profile.getGrants()));

        profile.setBan(null);
        profile.setMute(null);

        PunishmentHandler punishmentHandler = Spotify.getInstance().getPunishmentHandler();
        punishmentHandler.load(profile);

        grantHandler.setupPermissions(profile);

        profile.setLoaded(true);

        return cache ? this.playerStorage.computeIfAbsent(id, uuid -> profile) : profile;
    }

    @Override
    public Profile load(UUID id, String name) {
        return this.load(id, name, true);
    }

    @Override
    public Profile getProfile(UUID id) {
        return this.playerStorage.get(id);
    }

    @Override
    public Profile getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }

    @Override
    public Profile getProfile(String name, boolean byPassDisguise) {
        for (Profile profile : this.playerStorage.values()) {
            if (!byPassDisguise && profile.getRawName().equalsIgnoreCase(name)) return profile;
            else if (byPassDisguise && (profile.getName().equalsIgnoreCase(name) || (profile.isDisguised() && profile.getDisguisedName().equalsIgnoreCase(name))))
                return profile;
        }

        return null;
    }

    @Override
    public Profile getProfile(String name) {
        return getProfile(name, false);
    }

    private final List<UUID> deletingPrefix = Lists.newArrayList();

    @Override
    public List<UUID> getDeletingPrefix() {
        return deletingPrefix;
    }

    @Override
    public void save(Profile profile) {
        Document body = new Document();

        body.put("uuid", profile.getId().toString());
        body.put("name", profile.getName());
        body.put("color", profile.getChatColor() == null ? "none" : profile.getChatColor().name());
        body.put("bold", false);
        body.put("italic", profile.isItalic());
        body.put("privateMessages", profile.isMessages());
        body.put("privateMessagesSound", profile.isMessagesSound());
        body.put("allowDisguise", profile.isAllowDisguise());
        body.put("ignoreList", Lists.newArrayList(profile.getIgnoreList()));
        body.put("spaceBetweenRank", profile.isSpaceBetweenRank());
        body.put("tags", Lists.newArrayList(profile.getTags().stream().map(Tag::getId).collect(Collectors.toSet())));
        body.put("activePrefix", profile.getActivePrefix() == null ? "none" : profile.getActivePrefix().getId());
        body.put("activeSuffix", profile.getActiveSuffix() == null ? "none" : profile.getActiveSuffix().getId());
        body.put("activeTrail", profile.getActiveTrail() == null ? "none" : profile.getActiveTrail().getId());
        body.put("trails", Lists.newArrayList(profile.getTrails().stream().map(Trail::getId).collect(Collectors.toSet())));
        body.put("coins", profile.getCoins());
        body.put("bossBar", profile.isBossBar());
        body.put("lunarPrefix", profile.isLunarPrefix());
        body.put("lunarBorder", profile.isLunarBorder());
        body.put("address", profile.getAddress());

        if (collection.find(Filters.eq("uuid", profile.getId().toString())).first() != null)
            collection.replaceOne(Filters.eq("uuid", profile.getId().toString()), body);
        else collection.insertOne(body);

    }

    @Override
    public boolean canDisguise(String arg) {
        for (Profile val : playerStorage.values()) {
            if (val.getName().equalsIgnoreCase(arg)
                    || (val.getFakeName() != null && val.getFakeName().equalsIgnoreCase(arg))) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Map<UUID, Profile> getProfiles() {
        return this.playerStorage;
    }
}
