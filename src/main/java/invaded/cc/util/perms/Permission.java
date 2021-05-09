package invaded.cc.util.perms;

import invaded.cc.profile.Profile;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Permission {

    public static boolean test(CommandSender player, PermLevel level) {
        if (!(player instanceof Player)) return true;
        if (level == PermLevel.DEFAULT) return true;

        for (String s : level.getPerm())
            if (player.hasPermission(s)) return true;

        return false;
    }

    public static boolean test(Profile player, PermLevel level) {
        if (level == PermLevel.DEFAULT) return true;

        for (String s : level.getPerm())
            if (player.getPermissions().contains(s)) return true;

        return false;
    }

}
