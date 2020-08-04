package invaded.cc.commands;

import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.util.Color;
import invaded.cc.util.Task;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand extends InvadedCommand {

    public ReportCommand(){
        super("report", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length < 2) {
            sender.sendMessage(Color.translate("&cPlease use /report <player> <reason>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target == null){
            sender.sendMessage(Color.translate("&cThat player is offline."));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for(String s : args){
            if(s.equalsIgnoreCase(target.getName())) continue;

            stringBuilder.append(s).append(" ");
        }

        sender.sendMessage(Color.translate("&aWe received your request, staff members have been alerted."));

        Task.async(() -> new JedisPoster(JedisAction.REPORT)
        .addInfo("profileId", ((Player)sender).getUniqueId().toString())
                .addInfo("reportedId", target.getUniqueId().toString())
                .addInfo("reason", stringBuilder.toString())
        .post());
    }
}
