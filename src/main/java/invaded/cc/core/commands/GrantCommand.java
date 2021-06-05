package invaded.cc.core.commands;

import invaded.cc.core.Spotify;
import invaded.cc.core.grant.Grant;
import invaded.cc.core.grant.GrantHandler;
import invaded.cc.core.menu.GrantMenu;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.rank.RankHandler;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantCommand extends BasicCommand {

    public GrantCommand() {
        super("grant", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
            RankHandler rankHandler = Spotify.getInstance().getRankHandler();

            if (!(sender instanceof Player)) {
                if (args.length != 2) {
                    sender.sendMessage(Color.translate("&cPlease use /grant <player> <rank>"));
                    return;
                }

                Rank rank = rankHandler.getRank(args[1]);

                if (rank == null) {
                    sender.sendMessage(Color.translate("&CThat rank doesn't exist."));
                    return;
                }

                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                Profile profile = profileHandler.getProfile(offlinePlayer.getUniqueId());

                if (profile == null) profile = profileHandler.load(offlinePlayer.getUniqueId(), args[0]);

                Grant grant = new Grant(profile, System.currentTimeMillis(), rank.getName(), "console");

                GrantHandler grantHandler = Spotify.getInstance().getGrantHandler();
                grantHandler.updateGrant(grant);
                return;
            }

            if (args.length != 1) {
                sender.sendMessage(Color.translate("&cPlease use /grant <player>"));
                return;
            }

            Profile senderData = profileHandler.getProfile(((Player)sender).getUniqueId());
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

            Profile profile = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if (profile == null) profile = profileHandler.load(offlinePlayer.getUniqueId(), args[0]);

            if(profile.getHighestRank().getPriority() > senderData.getHighestRank().getPriority()) {
                sender.sendMessage(Color.translate("&cYou cannot modify " + profile.getColoredName() + "'s rank."));
                return;
            }

            Profile finalProfile = profile;
            Task.run(() -> new GrantMenu(finalProfile).open(((Player) sender)));
        });
    }

}
