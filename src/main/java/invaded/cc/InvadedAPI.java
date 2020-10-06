package invaded.cc;

import invaded.cc.profile.ProfileHandler;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class InvadedAPI {

    private static final ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

    public static boolean isDisguised(UUID uuid){
        return profileHandler.getProfile(uuid).isDisguised();
    }

    public static boolean isPunished(UUID uuid){
        return profileHandler.getProfile(uuid).isBanned();
    }

    public static String getColoredName(UUID uuid){
        return profileHandler.getProfile(uuid).getColoredName();
    }

    public static List<String> getFlyEnabledWorlds(){
        return Core.getInstance().getCommandHandler().getFlyWorlds();
    }

    public static void addEnabledFlyWorld(String s){
        Core.getInstance().getCommandHandler().getFlyWorlds().add(s);
    }

    public static void removeEnabledFlyWorld(String s){
        Core.getInstance().getCommandHandler().getFlyWorlds().remove(s);
    }

    public static Set<String> getServers(){
        return Core.getInstance().getServerHandler().getServers().keySet();
    }

    public static String getRank(UUID uuid){
        return profileHandler.getProfile(uuid).getHighestRank().getName();
    }

    public static Set<String> getAutoReplys(){
        return Core.getInstance().getChatHandler().getAutoReply().keySet();
    }

    public static int getRankPriority(Player player){
        return profileHandler.getProfile(player.getUniqueId()).getHighestRank().getPriority();
    }

    public static void addAutoReply(String key, String val) {
        Core.getInstance().getChatHandler().getAutoReply().put(key, val);
    }

    public static boolean hasRank(Player player){
        return !profileHandler.getProfile(player.getUniqueId()).getHighestRank().isDefaultRank();
    }

    public static String getColoredRank(Player player){
        return profileHandler.getProfile(player.getUniqueId()).getHighestRank().getColoredName();
    }

    public static String getCompleteFormat(Player player){
        return profileHandler.getProfile(player.getUniqueId()).getChatFormat();
    }
}
