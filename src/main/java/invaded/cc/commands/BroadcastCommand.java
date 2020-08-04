package invaded.cc.commands;

import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class BroadcastCommand extends InvadedCommand {

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
