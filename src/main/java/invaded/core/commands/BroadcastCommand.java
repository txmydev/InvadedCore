package invaded.core.commands;

import invaded.core.database.redis.JedisAction;
import invaded.core.database.redis.poster.JedisPoster;
import invaded.core.util.Color;
import invaded.core.util.command.BasicCommand;
import invaded.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends BasicCommand {

    public BroadcastCommand(){
        super("broadcast", PermLevel.ADMIN, "alert");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 1) sender.sendMessage(Color.translate("&cPlease add a message for the announcement."));
        else {
            StringBuilder stringBuilder = new StringBuilder();

            for (String arg : args) {
                stringBuilder.append(Color.translate(arg)).append(" ");
            }

            new JedisPoster(JedisAction.BROADCAST).addInfo("message", "&8[&4Alert&8] &b" + stringBuilder.toString());
        }
    }
}
