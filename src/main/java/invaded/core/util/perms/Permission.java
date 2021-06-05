package invaded.core.util.perms;

import invaded.core.Spotify;
import invaded.core.profile.Profile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Permission {

    public static boolean test(CommandSender player, PermLevel level) {
        if (!(player instanceof Player)) return true;
        if (level == PermLevel.DEFAULT) return true;
        return player.hasPermission(level.getPerm()) || Spotify.getAPI().getRankWeight(((Player) player).getUniqueId()) > 80;
    }

    public static boolean test(Profile player, PermLevel level) {
        if (level == PermLevel.DEFAULT) return true;

        return player.getPermissions().contains(level.getPerm()) || player.getHighestRank().getPriority() > 80;
    }

}
