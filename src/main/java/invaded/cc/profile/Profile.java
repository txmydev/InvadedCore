package invaded.cc.profile;

import com.google.common.collect.Lists;
import invaded.cc.Spotify;
import invaded.cc.grant.Grant;
import invaded.cc.manager.DisguiseHandler;
import invaded.cc.punishment.Punishment;
import invaded.cc.rank.Rank;
import invaded.cc.tags.Tag;
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
    private boolean italic = false, spaceBetweenRank = false;
    private boolean messages;
    private boolean messagesSound;

    private boolean staffChat = false;

    // Staff stuff
    private boolean filter = true;
    private boolean staffAlerts = true;

    private Tag activePrefix;
    private Tag activeSuffix;

    // Disguise stuff
    private boolean allowDisguise;
    private GameProfile fakeProfile;
    private Rank fakeRank;
    private String fakeName;
    private Skin fakeSkin;

    private int coins;

    private List<Tag> tags = Lists.newArrayList();

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
                name;
    }
    public String getColoredName() {
        if (name == null)
            name = (Bukkit.getPlayer(id) != null ? Bukkit.getPlayer(id).getName() : Bukkit.getOfflinePlayer(id).getName());

        if (isDisguised()) return fakeRank.getColors() + fakeName;

        return highestRank.getColors() + (chatColor == null ? "" : chatColor) + (italic ? ChatColor.ITALIC : "") +
                name;
    }

    public boolean isDisguised() {
        return fakeProfile != null;
    }

    public String getChatFormat() {
        if (isDisguised()) {
               return fakeRank.getPrefix() + fakeRank.getColors() + fakeName
                    + fakeRank.getSuffix();
        }

        return (activePrefix != null ? activePrefix.getDisplay() : "") + (activePrefix != null ? " " : "") + highestRank.getPrefix() + highestRank.getColors() + (chatColor == null ? "" : chatColor) + (italic ? ChatColor.ITALIC : "") +
                (spaceBetweenRank ? " " : "") +name
                + highestRank.getSuffix() + (activeSuffix != null ?" " + activeSuffix.getDisplay() : "");
    }

    public boolean hasCustomColor() {
        return chatColor != null;
    }

    public void aggregateCoins(int coins) {
        this.coins+=coins;
    }

    public void removeCoins(int coins) {
        this.coins-=coins;
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

        Spotify.getInstance().getPermissionHandler().updatePermissions(player, perms);
    }

    public boolean isOnline() {
        return Bukkit.getPlayer(id) != null;
    }
}
