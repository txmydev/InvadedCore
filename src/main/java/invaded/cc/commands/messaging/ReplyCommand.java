package invaded.cc.commands.messaging;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.Filter;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReplyCommand extends InvadedCommand {
    public ReplyCommand() {
        super("reply", PermLevel.DEFAULT, "r");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.translate("&CPlayer only command."));
            return;
        }

        if (args.length < 1) {
            sender.sendMessage(Color.translate("&cPlease use /r <message>"));
            return;
        }

        Player player = (Player) sender;
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        Profile targetData = profile.getRecentTalker();
        if (targetData == null) {
            player.sendMessage(Color.translate("&cYour recently talker is offline."));
            return;
        }

        Player target = Bukkit.getPlayer(targetData.getId());

        if (target == null) {
            player.sendMessage(Color.translate("&cYour recently talker is offline."));
            return;
        }

        if(!profile.isMessages()){
            player.sendMessage(Color.translate("&cYour pm's are off."));
            return;
        }

        if(!targetData.isMessages()){
            player.sendMessage(Color.translate("&cThat player has pm's toggled off."));
            return;
        }

        if (profile.getIgnoreList().contains(targetData.getName())) {
            player.sendMessage(Color.translate("&cYou cannot send a message to a player you're ignoring."));
            return;
        }

        String from = "&7(From " + profile.getColoredName() + "&7) ";
        String to = "&7(To " + targetData.getColoredName() + "&7) ";

        StringBuilder stringBuilder = new StringBuilder();

        for (String arg : args) {
            stringBuilder.append(arg).append(" ");
        }

        from = from + stringBuilder.toString();
        to = to + stringBuilder.toString();

        System.out.println("Sending message to the executor");
        player.sendMessage(Color.translate(to));
        System.out.println("Sent message");

        if (!targetData.getIgnoreList().contains(profile.getName()))
            target.sendMessage(Color.translate(from));

        profile.setRecentTalker(targetData);
        targetData.setRecentTalker(profile);

        if(profile.isMessagesSound()) player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
        if(targetData.isMessagesSound()) target.playSound(target.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

        if (Filter.needFilter(stringBuilder.toString())) {
            String filter = Filter.PREFIX + " &e(" + profile.getColoredName() + " &eto " + targetData.getColoredName() + "&e) " + stringBuilder.toString();

            Common.getOnlinePlayers().forEach(other -> {
                if (!Permission.test(other, PermLevel.STAFF)) return;

                other.sendMessage(Color.translate(filter));
            });
        }
    }
}
