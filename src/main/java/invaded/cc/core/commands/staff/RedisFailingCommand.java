package invaded.cc.core.commands.staff;

import invaded.cc.core.util.CC;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class RedisFailingCommand extends BasicCommand {

    public static boolean FAILING = false;

    public RedisFailingCommand() {
        super("redisfailing", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        FAILING = !FAILING;
        sender.sendMessage(CC.YELLOW + "Updated value ");
    }
}
