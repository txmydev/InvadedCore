package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class ReloadRanksCommand extends InvadedCommand {

    public ReloadRanksCommand() {
        super("rlranks", PermLevel.DEVELOPER);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 0){
            sender.sendMessage(Color.translate("&cThis command ain't that hard bro"));
            return;
        }

        Core.getInstance().getRankHandler().loadAll();
        sender.sendMessage(Color.translate("&aDone"));
    }
}
