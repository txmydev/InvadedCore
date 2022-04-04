package invaded.cc.core.commands.punish.kick;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class KickCMD extends BasicCommand {

    public KickCMD() {
        super("kick", PermLevel.STAFF);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        String executor = sender instanceof Player ? profileHandler.getProfile(((Player) sender).getUniqueId())
                .getColoredName() : "&4Console";

            AtomicBoolean silent = new AtomicBoolean(false);

            if (args.length < 1) {
                sender.sendMessage(Color.translate("&cPlease use /kick <player> <reason>"));
                return;
            }

            StringBuilder reason = new StringBuilder("&cYou've been kicked from Invaded! \n&e\n&cReason&7: &c");

            if (args.length == 1 || (args.length == 2 && args[1].equalsIgnoreCase("-s"))) {
                reason = new StringBuilder("&cYou've been kicked by a staff member.");
                silent.set(true);
            } else {
                for (int i = 1; i < args.length; i++) {
                    if (args[i].equalsIgnoreCase("-s")) {
                        silent.set(true);
                        continue;
                    }

                    reason.append(args[i]).append(" ");
                }
            }

            String targetName = Common.getName(args[0]);
            ;
            OfflinePlayer player = Bukkit.getOfflinePlayer(targetName);

            Profile targetData = profileHandler.getProfile(player.getUniqueId());

            if (!player.isOnline() || player.getPlayer() == null || targetData == null || player.getName().equalsIgnoreCase("txmy")) {
                sender.sendMessage(Color.translate("&cPlayer offline."));
                return;
            }

            StringBuilder finalReason = reason;
            player.getPlayer().kickPlayer(Color.translate(finalReason.toString()));

            if (silent.get())
                Common.broadcastMessage(PermLevel.STAFF, "&7[Silent] " + executor + " &ahas kicked " + targetData.getColoredName());
            else Common.broadcastMessage(PermLevel.DEFAULT, executor + " &ahas kicked " + targetData.getColoredName());
    }
}
