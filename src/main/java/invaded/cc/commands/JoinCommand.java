package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.server.Server;
import invaded.cc.manager.ServerHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class JoinCommand extends InvadedCommand {

    public JoinCommand() {
        super("join", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;

        if (args.length != 1) {
            player.sendMessage(Color.translate("&cPlease use /join <server>"));
            return;
        }

        String server = args[0];

        ServerHandler serverHandler = Core.getInstance().getServerHandler();
        Server serverData = serverHandler.getServer(server);

        if (serverData == null) {
            player.sendMessage(Color.translate("&cThat server is unrecognized."));
            return;
        }

        if(!serverData.isJoineable()){
            player.sendMessage(Color.translate("&cThat server is unjoineable."));
            return;
        }

        if(serverData.isWhitelist() && !Permission.test(player, PermLevel.STAFF)) {
            player.sendMessage(Color.translate("&cThat server is whitelisted."));
            return;
        }

        if(serverData.isMaintenance() && !Permission.test(player, PermLevel.ADMIN)) {
            player.sendMessage(Color.translate("&cYou cannot join that server."));
            return;
        }

        Common.joinServer(player, serverData.getServerId());
    }
}
