package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Clickable;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.Cooldown;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpOpCommand extends InvadedCommand {

    public HelpOpCommand(){
        super("helpop", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

        if(args.length == 0){
            sender.sendMessage(Color.translate("&cPlease use /helpop <message>"));
            return;
        }

        Player player = (Player) sender;
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if(!profile.getHelpOpCooldown().hasExpired()) {
            player.sendMessage(Color.translate("&CYou may wait " + profile.getHelpOpCooldown().getTimeLeft() + " seconds until you can use helpop again."));
            return;
        }

        StringBuilder builder = new StringBuilder();

        for(String arg : args) builder.append(arg).append(" ");

        profile.setHelpOpCooldown(new Cooldown(50_000));

        /*new JedisPoster(JedisAction.HELPOP)
                .addInfo("profileId", profile.getId().toString())
                .addInfo("message", builder.toString())
                .post();*/

        String message =builder.toString();

        Clickable clickable = new Clickable("&7[&4Help Request&7] &bRequested by " + profile.getColoredName()
                + "&b: &7" + message)
                .hover(HoverEvent.Action.SHOW_TEXT,  "&bClick to be teleported to him ");

        clickable.clickEvent(ClickEvent.Action.RUN_COMMAND,
                "/tp " + profile.getName());

        Common.broadcastMessage(PermLevel.STAFF, clickable.get());

        player.sendMessage(Color.translate("&aWe received your request."));
    }
}
