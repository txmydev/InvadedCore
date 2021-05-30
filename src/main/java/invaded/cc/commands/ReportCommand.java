package invaded.cc.commands;

import invaded.cc.Spotify;
import invaded.cc.profile.Profile;
import invaded.cc.util.Clickable;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReportCommand extends BasicCommand {

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

        /*Task.async(() -> new JedisPoster(JedisAction.REPORT)
        .addInfo("profileId", ((Player)sender).getUniqueId().toString())
                .addInfo("reportedId", target.getUniqueId().toString())
                .addInfo("reason", stringBuilder.toString())
        .post());*/
        Player player = (Player) sender;
        Profile profile = Spotify.getInstance().getProfileHandler().getProfile(player.getUniqueId());
        Profile targetProfile = Spotify.getInstance().getProfileHandler().getProfile(target.getUniqueId());
        String reason = stringBuilder.toString();

        Clickable clickable = new Clickable("&7[&4Report&7] " + profile.getColoredName() + " &fhas reported " + targetProfile.getColoredName()
                + " &fwith a reason of &b"  + reason);

        clickable.hover(HoverEvent.Action.SHOW_TEXT,  "&bClick to teleport to " + targetProfile.getColoredName());

        clickable.clickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + targetProfile.getName());

        Common.broadcastMessage(PermLevel.STAFF, clickable.get());
    }
}
