package invaded.core.commands;

import invaded.core.Spotify;
import invaded.core.util.Color;
import invaded.core.util.Common;
import invaded.core.util.command.BasicCommand;
import invaded.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class ReloadRanksCommand extends BasicCommand {

    public ReloadRanksCommand() {
        super("rlranks", PermLevel.DEVELOPER);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 0){
            sender.sendMessage(Color.translate("&cThis command ain't that hard bro"));
            return;
        }

        Spotify.getInstance().getRankHandler().loadAll();
        sender.sendMessage(Color.translate("&aDone"));

        Common.getOnlinePlayers().forEach(player -> player.kickPlayer(Color.translate("&cReloaded ranks")));
    }
}
