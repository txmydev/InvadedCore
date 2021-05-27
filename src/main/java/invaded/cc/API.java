package invaded.cc;

import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.rank.Rank;
import invaded.cc.util.Color;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class API {

    private Basic plugin;

    public API(Basic plugin) {
        this.plugin = plugin;
    }

    public boolean isDisguised(UUID id) {
        ProfileHandler profileHandler = plugin.getProfileHandler();
        Profile profile = profileHandler.getProfile(id);
        if(profile == null) return false;
        return profile.isDisguised();
    }

    public boolean isBanned(UUID id, String name) {
        return plugin.getProfileHandler().load(id, name, false).isBanned();
    }

    public boolean isBanned(UUID id){
        return isBanned(id, Bukkit.getOfflinePlayer(id).getName());
    }

    public boolean isMuted(UUID id, String name) {
        return plugin.getProfileHandler().load(id, name, false).isMuted();
    }

    public boolean isMuted(UUID id) {
        return isMuted(id, Bukkit.getOfflinePlayer(id).getName());
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

    public String getColoredName(Player player){
        return getColoredName(player.getUniqueId());
    }

    public String getColoredName(UUID uuid){
        return plugin.getProfileHandler().getProfile(uuid).getColoredName();
    }

    public List<String> getFlyEnabledWorlds(){
        return plugin.getCommandHandler().getFlyWorlds();
    }

    public void allowFly(String world){
        plugin.getCommandHandler().getFlyWorlds().add(world);
    }

    public void disallowFly(String world){
        plugin.getCommandHandler().getFlyWorlds().remove(world);
    }

    public boolean isFlyable(String world){
        return plugin.getCommandHandler().getFlyWorlds().contains(world);
    }

    public int getRankWeight(UUID id){
        return plugin.getProfileHandler().getProfile(id).getHighestRank().getPriority();
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
}