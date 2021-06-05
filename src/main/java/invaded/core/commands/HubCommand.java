package invaded.core.commands;

import invaded.core.Spotify;
import invaded.core.util.Common;
import invaded.core.util.command.BasicCommand;
import invaded.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HubCommand extends BasicCommand {

    public HubCommand() {
        super("hub", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;

        String targetServer = "";

        if(Spotify.getInstance().getServerName().contains("sa")) {
            targetServer = "sa-hub-1";
        }

        Common.joinServer(player, targetServer);
    }
}
