package invaded.cc.commands.punish.unpunish;

import invaded.cc.Core;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.Punishment;
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

public class UnBanCMD extends InvadedCommand {

    public UnBanCMD() {
        super("unban", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

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

            String targetName = args[0];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());

            if (targetData == null || targetData.getBan() == null) {
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

            new JedisPoster(JedisAction.REMOVE_PUNISHMENT)
                    .addInfo("type", punishment.getType().name())
                    .addInfo("cheaterName", punishment.getCheaterName())
                    .addInfo("cheaterUuid", punishment.getCheaterUuid().toString())
                    .addInfo("expire", punishment.getExpire())
                    .addInfo("punishedAt", punishment.getPunishedAt())
                    .addInfo("staffName", punishment.getStaffName())
                    .addInfo("s", punishment.isS())
                    .addInfo("reason", punishment.getReason())
                    .addInfo("removedBy", executor)
                    .addInfo("removedAt", punishment.getRemovedAt())
                    .post();

            if(silent.get()) Common.broadcastMessage(PermLevel.STAFF, "&7[Silent] " + targetData.getColoredName() + " &awas unbanned by " + executor);
            else Common.broadcastMessage(PermLevel.DEFAULT, targetData.getColoredName() + " &awas unbanned by " +executor);
        });
    }
}
