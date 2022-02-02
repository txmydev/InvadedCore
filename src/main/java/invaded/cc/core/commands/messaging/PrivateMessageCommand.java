package invaded.cc.core.commands.messaging;

import invaded.cc.core.API;
import invaded.cc.core.Spotify;
import invaded.cc.core.manager.SocialSpyHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.rank.RankHandler;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.Filter;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PrivateMessageCommand extends BasicCommand {
    public PrivateMessageCommand() {
        super("msg", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        if (!(sender instanceof Player)) {
            sender.sendMessage(Color.translate("&cPlayer only command."));
            return;
        }

        Player player = (Player) sender;

        if (args.length <= 1) {
            player.sendMessage(Color.translate("&CPlease use /msg <player> <message>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Color.translate("&cThat player is offline."));
            return;
        }

        Profile profile = profileHandler.getProfile(player.getUniqueId());
        Profile targetData = profileHandler.getProfile(target.getUniqueId());

        if (targetData.isDisguised() && !targetData.getFakeName().equalsIgnoreCase(args[0])) {
            player.sendMessage(Color.translate("&cThat player is offline."));
            return;
        }

        if (player.getUniqueId() == target.getUniqueId()) {
            player.sendMessage(Color.translate("&cYou cannot message yourself."));
            return;
        }

        if (!profile.isMessages()) {
            player.sendMessage(Color.translate("&cYour pm's are off."));
            return;
        }

        if (!targetData.isMessages()) {
            player.sendMessage(Color.translate("&cThat player has pm's toggled off."));
            return;
        }

        if (profile.getIgnoreList().contains(targetData.getName())) {
            player.sendMessage(Color.translate("&cYou cannot send a message to a player you're ignoring."));
            return;
        }

        if (profile.isMuted()) {
            player.sendMessage(Color.translate("&cYou cannot send private messages as your are currently muted."));
            return;
        }

        String from = "&7(From " + profile.getColoredName() + "&7) ";
        String to = "&7(To " + targetData.getColoredName() + "&7) ";

        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 1; i < args.length; i++) {
            stringBuilder.append(args[i]).append(" ");
        }

        String message = stringBuilder.toString();
        
        from = from + message;
        to = to + message;

        player.sendMessage(Color.translate(to));
        if (!targetData.getIgnoreList().contains(player.getName())) target.sendMessage(Color.translate(from));

        if (profile.isMessagesSound()) player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
        if (!targetData.getIgnoreList().contains(player.getName()) && targetData.isMessagesSound())
            target.playSound(target.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

        profile.setRecentTalker(targetData);
        targetData.setRecentTalker(profile);

        if (Filter.needFilter(message.trim())) {
            String filter = Filter.PREFIX + " &e(" + profile.getColoredName() + " &eto " + targetData.getColoredName() + "&e) " + message;

            Common.getOnlinePlayers().forEach(other -> {
                if (!Permission.test(other, PermLevel.STAFF)) return;

                Profile otherData = profileHandler.getProfile(other.getUniqueId());
                if (!otherData.isFilter()) return;

                other.sendMessage(Color.translate(filter));
            });
        }

        SocialSpyHandler socialSpyHandler = Spotify.getInstance().getSocialSpyHandler();
        if (!socialSpyHandler.isEnabled()) return;

        RankHandler rankHandler = Spotify.getInstance().getRankHandler();
        if (rankHandler.isHighestRank(player) || rankHandler.isHighestRank(target)) return;

        Common.getOnlinePlayers().forEach(other -> {
            Profile otherProfile = profileHandler.getProfile(other);
            if (!otherProfile.isSocialSpy()) return;

            other.sendMessage(Filter.SOCIAL_SPY_PREFIX + profile.getColoredName() + CC.GRAY + " to " 
                    + targetData.getColoredName() + CC.GRAY + ": " + message);
        });
    }
}
