package invaded.cc.core.commands;

import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class JoinCommand extends BasicCommand {

    public JoinCommand() {
        super("join", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
       /* if (!(sender instanceof Player)) return;

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

        Common.joinServer(player, serverData.getServerId());*/
    }
}
