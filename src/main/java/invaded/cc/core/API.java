package invaded.cc.core;

import invaded.cc.core.lunarapi.nethandler.LCPacket;
import invaded.cc.core.network.server.Server;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.tablist.TabAdapter;
import invaded.cc.core.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class API {

    private Spotify plugin;

    public API(Spotify plugin) {
        this.plugin = plugin;
    }

    public boolean isDisguised(UUID id) {
        ProfileHandler profileHandler = plugin.getProfileHandler();
        Profile profile = profileHandler.getProfile(id);
        if (profile == null) return false;
        return profile.isDisguised();
    }

    public boolean isBanned(UUID id, String name) {
        return plugin.getProfileHandler().load(id, name, false).isBanned();
    }

    public boolean isBanned(UUID id) {
        return isBanned(id, Bukkit.getOfflinePlayer(id).getName());
    }

    public boolean isMuted(UUID id, String name) {
        return plugin.getProfileHandler().load(id, name, false).isMuted();
    }

    public boolean isMuted(UUID id) {
        return isMuted(id, Bukkit.getOfflinePlayer(id).getName());
    }

    public void sendLunarPacket(Player player, LCPacket packet) {
        plugin.getLunarHandler().sendPacket(player, packet);
    }

    public String getRank(Player player) {
        return plugin.getProfileHandler().getProfile(player).getHighestRank().getName();
    }

    public Rank getRankObj(Player player) {
        return getRankObj(player.getUniqueId());
    }

    private Rank getRankObj(UUID id) {
        return plugin.getProfileHandler().getProfile(id).getHighestRank();
    }

    public String getColoredName(Player player) {
        return getColoredName(player.getUniqueId());
    }

    public String getColoredName(UUID uuid) {
        return plugin.getProfileHandler().getProfile(uuid).getColoredName();
    }

    public List<String> getFlyEnabledWorlds() {
        return plugin.getCommandHandler().getFlyWorlds();
    }

    public void allowFly(String world) {
        plugin.getCommandHandler().getFlyWorlds().add(world);
    }

    public void disallowFly(String world) {
        plugin.getCommandHandler().getFlyWorlds().remove(world);
    }

    public boolean isFlyable(String world) {
        return plugin.getCommandHandler().getFlyWorlds().contains(world);
    }

    public int getRankWeight(UUID id) {
        return plugin.getProfileHandler().getProfile(id) == null ? 0 : plugin.getProfileHandler().getProfile(id).getHighestRank().getPriority();
    }

    public int getRankWeight(Player player) {
        return this.getRankWeight(player.getUniqueId());
    }

    public boolean hasRank(UUID id) {
        return !getRankObj(id).isDefaultRank();
    }

    public String getChatFormat(UUID id) {
        return Color.translate(plugin.getProfileHandler().getProfile(id).getChatFormat());
    }

    public String getChatFormat(Player player) {
        return getChatFormat(player.getUniqueId());
    }

    public void awardCoins(Player player, int coins, boolean message) {
        Spotify.getInstance().getProfileHandler().getProfile(player.getUniqueId()).aggregateCoins(coins, message);
    }

    public void awardCoins(Player player, int coins) {
        Spotify.getInstance().getProfileHandler().getProfile(player.getUniqueId()).aggregateCoins(coins, true);
    }

    public void removeCoins(Player player, int coins) {
        Spotify.getInstance().getProfileHandler().getProfile(player.getUniqueId()).removeCoins(coins);
    }

    public int getOnline(String serverId) {
        return Spotify.getInstance().getServerHandler().getServer(serverId).getOnline();
    }

    public boolean isTesting(String serverId) {
        return Spotify.getInstance().getServerHandler().getServer(serverId).isTesting();
    }

    public boolean isMaintenance(String serverId) {
        return Spotify.getInstance().getServerHandler().getServer(serverId).isMaintenance();
    }

    public String getExtraInfo(String serverId) {
        return Spotify.getInstance().getServerHandler().getServer(serverId).getExtraInfo();
    }

    public Collection<Server> getServerList() {
        return Spotify.getInstance().getServerHandler().getServerMap().values();
    }

    public boolean isBossBar(Player player) {
        return Spotify.getInstance().getProfileHandler().getProfile(player).isBossBar();
    }

    public boolean isLunarBorder(Player player) {
        return Spotify.getInstance().getProfileHandler().getProfile(player).isLunarBorder();
    }

    public Server getServer(String serverId) {
        return Spotify.getInstance().getServerHandler().getServer(serverId);
    }
}
