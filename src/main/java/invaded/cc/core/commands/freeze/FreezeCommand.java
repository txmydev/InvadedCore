package invaded.cc.core.commands.freeze;

import invaded.cc.core.Spotify;
import invaded.cc.core.freeze.FreezeHandler;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FreezeCommand extends BasicCommand {

    public FreezeCommand() {
        super("freeze", PermLevel.STAFF, "ss");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(CC.RED + "/freeze <player>");
            return;
        }

        Player player = Bukkit.getPlayer(args[0]);
        if(player == null) {
            sender.sendMessage(CC.RED + "Player offline.");
            return;
        }

        FreezeHandler freezeHandler = Spotify.getInstance().getFreezeHandler();
        if(!freezeHandler.isFreezed(player)) {
            freezeHandler.freeze(player);
            sender.sendMessage(player.getDisplayName() + CC.YELLOW + " is now frozen.");
        } else {
            freezeHandler.unFreeze(player);
            sender.sendMessage(player.getDisplayName() + CC.YELLOW + " is no longer frozen.");
        }
    }
}
