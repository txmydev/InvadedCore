package invaded.cc.profile;

import invaded.cc.Core;
import invaded.cc.grant.Grant;
import invaded.cc.manager.DisguiseHandler;
import invaded.cc.punishment.Punishment;
import invaded.cc.rank.Rank;
import invaded.cc.util.Common;
import invaded.cc.util.Cooldown;
import invaded.cc.util.Skin;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
@Setter
public class Profile {

   /* @Getter
    private static Map<UUID, Profile> playerDatas = new HashMap<UUID, Profile>();*/

    private final UUID id;
    private String name;

    private Skin realSkin;
    private GameProfile realProfile;

    // Punishment Stuff
    private Punishment ban;
    private Punishment mute;

    private List<Grant> grants;
    private Set<String> permissions;

    private boolean loaded;

    private Rank highestRank;

    // Conversation stuff
    private Profile recentTalker;
    private List<String> ignoreList = new ArrayList<>();
    private ChatColor chatColor = null;
    private boolean italic = false, bold = false, spaceBetweenRank = false;
    private boolean messages;
    private boolean messagesSound;

    // Staff stuff
    private boolean filter = true;
    private boolean staffAlerts = true;

    // Disguise stuff
    private boolean allowDisguise;
    private GameProfile fakeProfile;
    private Rank fakeRank;
    private String fakeName;
    private Skin fakeSkin;

    private Cooldown commandCooldown = new Cooldown(0), chatCooldown = new Cooldown(0), helpOpCooldown = new Cooldown(0);

    protected Profile(UUID uuid, String name) {
        this.id = uuid;
        this.name = name;
    }

    protected Profile(UUID uuid) {
        this(uuid, Bukkit.getOfflinePlayer(uuid).getName());
    }

    public String getRealColoredName() {
        if (name == null)
            name = (Bukkit.getPlayer(id) != null ? Bukkit.getPlayer(id).getName() : Bukkit.getOfflinePlayer(id).getName());

        return highestRank.getColors() + (chatColor == null ? "" : chatColor) + (italic ? ChatColor.ITALIC : "") +
                (bold ? ChatColor.BOLD : "") + (spaceBetweenRank ? " " : "")+ name;
    }

/*
    private void load() {
        Database db = Core.getInstance().getDb();
        Document document = new Document("uuid", id.toString());

        MongoCollection<Document> collection = db.getCollection("playerData");

        Document found = collection.find(document).first();

        System.out.println("Loading data of " + name);

        if (found == null) {
            System.out.println("There's no document in collection playerData");

            this.grant = new GrantOld(this, Rank.getDefault().getName(), "Server");

            document
                    .append("name", name)
                    .append("rank", grant.getRank().getName())
                    .append("color", chatColor == null ? "none" : chatColor.name())
                    .append("italic", italic)
                    .append("bold", bold)
                    .append("privateMessages", messages)
                    .append("privateMessagesSound", messagesSound)
                    .append("ignoreList", ignoreList)
                    .append("lastServer", Core.getInstance().getServerName())
                    .append("allowDisguise", allowDisguise);
            collection.insertOne(document);

            System.out.println("Inserted in the db player " + name);
            loaded = true;
            return;
        }

        System.out.println("[Core] Player already existed, loading data from db.");

        chatColor = found.getString("color").equals("none") ? null : ChatColor.valueOf(found.getString("color"));
        italic = found.getBoolean("italic");
        bold = found.getBoolean("bold");

        ignoreList = found.get("ignoreList", new ArrayList<>());

        allowDisguise = found.getBoolean("allowDisguise", false);

        messages = found.getBoolean("privateMessages");
        messagesSound = found.getBoolean("privateMessagesSound");

        this.grant = new GrantOld(this, found.getString("rank"), "Server");
        loaded = true;
    }

    public void save() {
        Database db = Core.getInstance().getDb();
        MongoCollection<Document> collection = db.getCollection("playerData");

        Document document = new Document("uuid", id.toString())
                .append("name", name)
                .append("rank", grant.getRank().getName())
                .append("color", chatColor == null ? "none" : chatColor.name())
                .append("italic", italic)
                .append("bold", bold)
                .append("privateMessages",messages)
                .append("privateMessagesSound", messagesSound)
                .append("ignoreList", ignoreList)
                .append("allowDisguise", allowDisguise);

        collection.replaceOne(Filters.eq("uuid", id.toString()), document);

        if(ban != null) {
            if(isBanned()) Core.getInstance().getPunishmentHandler().requestSaveActive(ban);
            else Core.getInstance().getPunishmentHandler().requestUnPunish(ban);
        }
        if(mute != null) {
            if(isBanned()) Core.getInstance().getPunishmentHandler().requestSaveActive(mute);
            else Core.getInstance().getPunishmentHandler().requestUnPunish(mute);
        }
    }

    public void setGrant(GrantOld grant) {
        this.grant = grant;

        Player player = Bukkit.getPlayer(id);
        if (player == null) return;

        grant.removeAttachment(player);
        grant.setupAttachment(player);
    }*/
/*    public static Profile getByUuid(UUID uuid) {
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        return profileHandler.getProfiles().get(uuid);
    }*/

    public String getColoredName() {
        if (name == null)
            name = (Bukkit.getPlayer(id) != null ? Bukkit.getPlayer(id).getName() : Bukkit.getOfflinePlayer(id).getName());

        if (isDisguised()) return fakeRank.getColors() + fakeName;

        return highestRank.getColors() + (chatColor == null ? "" : chatColor) + (italic ? ChatColor.ITALIC : "") +
                (bold ? ChatColor.BOLD : "") + name;
    }

    public boolean isDisguised() {
        return fakeProfile != null && fakeName != null;
    }

    public String getChatFormat() {
        if (isDisguised()) {
               return fakeRank.getPrefix() + fakeRank.getColors() + fakeName
                    + fakeRank.getSuffix();
        }

        return highestRank.getPrefix() + highestRank.getColors() + (chatColor == null ? "" : chatColor) + (italic ? ChatColor.ITALIC : "") +
                (bold ? ChatColor.BOLD : "") + (spaceBetweenRank ? " " : "") +name
                + highestRank.getSuffix();

    }

    public boolean hasCustomColor() {
        return chatColor != null;
    }

    public boolean isMuted() {
        if(mute == null) return false;
        if(mute.getType() == Punishment.Type.MUTE) return true;

        return mute.getType() == Punishment.Type.TEMPORARY_MUTE && mute.getExpire() > System.currentTimeMillis();
    }

    public void unDisguise() {
        DisguiseHandler.undisguise(this);

        fakeProfile = null;
        fakeName = null;
        fakeSkin = null;
        fakeRank = null;
    }

    public void disguise() {
        DisguiseHandler.disguise(this);
    }

    public String getDisguisedName() {
        return isDisguised() ? fakeName : name;
    }

    public boolean isBanned() {
        if(ban == null) return false;
        if(ban.getType() != Punishment.Type.TEMPORARY_BAN) return true;

        return ban.getExpire() > System.currentTimeMillis();
    }

    public void updatePermissions(Player player) {
        Map<String, Boolean> perms = Common.convertListToMap(permissions);

        Core.getInstance().getPermissionHandler().updatePermissions(player, perms);
    }
}
