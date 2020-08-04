package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FlyCommand extends InvadedCommand {

    public FlyCommand() {
        super("fly", PermLevel.VIP);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.translate("&CPlayer only command."));
            return;
        }

        Player player = (Player) sender;

        if (args.length != 0) {
            player.sendMessage(Color.translate("&cPlease use /fly."));
            return;
        }

        if(!Core.getInstance().getCommandHandler().getFlyWorlds().contains(player.getWorld().getName())) {
            player.sendMessage(Color.translate("&cYou cannot fly in that world!"));
            return;
        }

        if (player.isFlying()) {
            if (player.getAllowFlight()) player.setAllowFlight(false);
            player.setFlying(false);
        } else {
            if(!player.getAllowFlight()) player.setAllowFlight(true);
            player.setFlying(true);
        }

        player.sendMessage(Color.translate( (player.isFlying() ? "&a" : "&7") + "You toggled your fly mode."));
    }
}
