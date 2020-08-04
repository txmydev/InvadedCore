package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.util.Common;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommand extends InvadedCommand {

    public HubCommand() {
        super("hub", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;

        String targetServer = "";

        if(Core.getInstance().getServerName().contains("sa")) {
            targetServer = "sa-hub-1";
        }

        Common.joinServer(player, targetServer);
    }
}
