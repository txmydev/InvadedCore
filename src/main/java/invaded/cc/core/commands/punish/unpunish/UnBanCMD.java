package invaded.cc.core.commands.punish.unpunish;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.Punishment;
import invaded.cc.core.punishment.PunishmentHandler;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class UnBanCMD extends BasicCommand {

    public UnBanCMD() {
        super("unban", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

            String executor = sender instanceof Player ? profileHandler.getProfile(((Player) sender).getUniqueId())
                    .getColoredName() : "&4Console";

            AtomicBoolean silent = new AtomicBoolean(false);

            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cPlease use /unban <player>"));
                return;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("-s")) {
                silent.set(true);
            }

            String targetName = Common.getName(args[0]);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if (targetData == null)
                targetData = profileHandler.load(offlinePlayer.getUniqueId(), offlinePlayer.getName(), false);

            if (targetData.getBan() == null) {
                sender.sendMessage(Color.translate("&cThat player isn't banned."));
                return;
            }

            if (targetData.getBan().getType() == Punishment.Type.BLACKLIST) {
                sender.sendMessage(Color.translate("&cThat player punishment requires an unblacklist, not an unban, you may use /unblacklist <player>"));
                return;
            }

            Punishment punishment = targetData.getBan();
            punishment.setRemovedAt(System.currentTimeMillis());
            punishment.setRemovedBy(executor);

            PunishmentHandler punishmentHandler = Spotify.getInstance().getPunishmentHandler();
            punishmentHandler.pardon(offlinePlayer.getUniqueId(), punishment);

            if (silent.get())
                Common.broadcastMessage(PermLevel.STAFF, "&7[Silent] " + targetData.getColoredName() + " &awas unbanned by " + executor);
            else
                Common.broadcastMessage(PermLevel.DEFAULT, targetData.getColoredName() + " &awas unbanned by " + executor);
        });
    }
}
