package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.profile.Profile;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.Task;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.ChatColor;
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

       /* Task.async(() -> {
            new JedisPoster(JedisAction.STAFF_CHAT)
                    .addInfo("profileId", ((Player)sender).getUniqueId().toString())
                    .addInfo("message", stringBuilder.toString())
                    .post();
        });*/
        ChatColor color = ChatColor.AQUA;
        Player player = (Player) sender;
        Profile profile = Core.getInstance().getProfileHandler().getProfile(player.getUniqueId());
        Common.broadcastMessage(PermLevel.STAFF, color + "[uhc1] " + profile.getRealColoredName() + "&7: " + color + stringBuilder.toString());
    }
}
