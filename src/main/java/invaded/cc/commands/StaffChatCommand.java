package invaded.cc.commands;

import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.util.Color;
import invaded.cc.util.Task;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand extends InvadedCommand {

    public StaffChatCommand(){
        super("staffchat", PermLevel.STAFF, "sc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        if(args.length < 1){
            sender.sendMessage(Color.translate("&cPlease use /sc <message>"));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(String s : args) stringBuilder.append(s).append(" ");

        Task.async(() -> {
            new JedisPoster(JedisAction.STAFF_CHAT)
                    .addInfo("profileId", ((Player)sender).getUniqueId().toString())
                    .addInfo("message", stringBuilder.toString())
                    .post();
        });
    }
}
