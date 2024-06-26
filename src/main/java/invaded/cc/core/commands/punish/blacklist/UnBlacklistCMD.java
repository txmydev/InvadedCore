package invaded.cc.core.commands.punish.blacklist;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.Punishment;
import invaded.cc.core.punishment.PunishmentHandler;
import invaded.cc.core.util.Clickable;
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

public class UnBlacklistCMD extends BasicCommand {

    public UnBlacklistCMD() {
        super("unblacklist", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

            String executor = sender instanceof Player ? profileHandler.getProfile(((Player) sender).getUniqueId())
                    .getColoredName() : "&4Console";

            AtomicBoolean silent = new AtomicBoolean(false);

            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cPlease use /unblacklist <player>"));
                return;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("-s")) {
                silent.set(true);
            }

            if (args.length > 2) {
                sender.sendMessage(Color.translate("&cPlease use /unblacklist <player>"));
                return;
            }

            String targetName = Common.getName(args[0]);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if (targetData == null)
                targetData = profileHandler.load(offlinePlayer.getUniqueId(), offlinePlayer.getName(), false);

            if (targetData.getBan() == null) {
                sender.sendMessage(Color.translate("&cThat player isn't blacklisted."));
                return;
            }

            if (targetData.getBan().getType() != Punishment.Type.BLACKLIST) {
                sender.sendMessage(Color.translate("&cThat player punishment type isn't a blacklist, you may use /unban <player>"));
                return;
            }

            Punishment punishment = targetData.getBan();
            punishment.setRemovedAt(System.currentTimeMillis());
            punishment.setRemovedBy(executor);

            if (silent.get())
                Common.broadcastMessage(PermLevel.STAFF, new Clickable("&7[Silent] " + targetData.getColoredName()
                        + " &awas unblacklisted by " + executor, null ,null).asComponents());
            else
                Common.broadcastMessage(PermLevel.STAFF, targetData.getColoredName() + " &awas unblacklisted by " + executor);

            PunishmentHandler punishmentHandler = Spotify.getInstance().getPunishmentHandler();
            punishmentHandler.pardon(targetData.getId(), punishment);
        });
    }
}
