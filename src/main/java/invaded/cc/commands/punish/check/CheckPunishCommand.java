package invaded.cc.commands.punish.check;

import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class CheckPunishCommand extends InvadedCommand {

    public CheckPunishCommand() {
        super("checkban", PermLevel.STAFF);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

    }
}
