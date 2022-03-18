package invaded.cc.core.commands.staff;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FeedCommand extends BasicCommand {

    public FeedCommand() {
        super("feed", PermLevel.STAFF);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = null;
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        switch (args.length) {
            case 0:
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Color.translate("&cYou can only heal other players."));
                    break;
                }

                player = (Player) sender;
                player.setFoodLevel(20);

                player.sendMessage(Color.translate("&aYou have been feeded."));
                break;
            case 1:
                if (args[0].equalsIgnoreCase("all")) {
                    Common.getOnlinePlayers().forEach(other -> {
                        other.setHealth(other.getMaxHealth());

                        other.sendMessage(Color.translate("&aYou have been feeded by " + (sender instanceof Player ?
                                profileHandler.getProfile(((Player) sender).getUniqueId()).getColoredName() :
                                "&4Console") + "&a."));
                    });
                    return;
                }

                player = Bukkit.getPlayer(args[0]);

                if (player == null) {
                    sender.sendMessage(Color.translate("&cThat player is offline."));
                    return;
                }

                player.sendMessage(Color.translate("&aYou have been feeded."));
                player.setFoodLevel(20);
                sender.sendMessage(Color.translate("&aYou have feeded " + profileHandler.getProfile(player.getUniqueId()).getColoredName()));
                break;
            default:
                sender.sendMessage(Color.translate("&cPlease use /feed <player> or /feed."));
                break;
        }
    }
}
