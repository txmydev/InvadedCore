package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.menu.PunishmentsMenu;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PunishmentsCommand extends InvadedCommand {

    public PunishmentsCommand() {
        super("punishments", PermLevel.ADMIN, "c");
    }

    private ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof ConsoleCommandSender) {
            Bukkit.getLogger().info("You cannot execute this, please log in.");
            return;
        }

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(Color.translate("&cPlease specify a player!"));
            return;
        }

        Profile target = profileHandler.getProfile(Bukkit.getOfflinePlayer(args[0]).getUniqueId());

        if(target == null) {
            player.sendMessage(Color.translate("&cNon punishments on record."));
            return;
        }

        new PunishmentsMenu(target).open(player);
    }
}
