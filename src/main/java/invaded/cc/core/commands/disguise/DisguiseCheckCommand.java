package invaded.cc.core.commands.disguise;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisguiseCheckCommand extends BasicCommand {

    public DisguiseCheckCommand() {
        super("checkdisguise", PermLevel.ADMIN, "checkd");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 1) {
            sender.sendMessage(Color.translate("&cUse /checkdisguise <player>."));
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if (player == null) {
            sender.sendMessage(Color.translate("&cThat player is offline."));
            return;
        }

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        profileHandler.ifPresent(player.getUniqueId(), profile -> {
            if (!profile.isDisguised())
                sender.sendMessage(Color.translate(profile.getColoredName() + " &cisn't disguised."));
            else
                sender.sendMessage(Color.translate(profile.getRealColoredName() + " &ais disguised as " + profile.getColoredName()));
        }, sender, "That player is offline.");
    }
}
