package invaded.cc.commands.punish.blacklist;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.Punishment;
import invaded.cc.punishment.PunishmentHandler;
import invaded.cc.util.Clickable;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.Task;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class UnBlacklistCMD extends InvadedCommand {

    public UnBlacklistCMD() {
        super("unblacklist", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

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

            String targetName = args[0];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if(targetData == null) targetData = profileHandler.load(offlinePlayer.getUniqueId(), offlinePlayer.getName(), false);

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

            if(silent.get()) Common.broadcastMessage(PermLevel.STAFF, new Clickable("&7[Silent] " + targetData.getColoredName()
                    +" &awas unblacklisted by " +executor).get());
            else Common.broadcastMessage(PermLevel.STAFF, targetData.getColoredName() + " &awas unblacklisted by " + executor);


//            new JedisPoster(JedisAction.REMOVE_PUNISHMENT)
//                    .addInfo("type", punishment.getType().name())
//                    .addInfo("cheaterName", punishment.getCheaterName())
//                    .addInfo("cheaterUuid", punishment.getCheaterUuid().toString())
//                    .addInfo("expire", punishment.getExpire())
//                    .addInfo("punishedAt", punishment.getPunishedAt())
//                    .addInfo("staffName", punishment.getStaffName())
//                    .addInfo("s", punishment.isS())
//                    .addInfo("reason", punishment.getReason())
//                    .addInfo("removedBy", executor)
//                    .addInfo("removedAt", punishment.getRemovedAt())
//                    .post();

            PunishmentHandler punishmentHandler = Core.getInstance().getPunishmentHandler();
            punishmentHandler.pardon(targetData.getId(), punishment);
           });
    }
}
