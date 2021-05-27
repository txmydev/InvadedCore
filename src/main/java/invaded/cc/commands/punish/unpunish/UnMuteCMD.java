package invaded.cc.commands.punish.unpunish;

import invaded.cc.Basic;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.Punishment;
import invaded.cc.punishment.PunishmentHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.Task;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class UnMuteCMD extends BasicCommand {

    public UnMuteCMD(){
        super("unmute", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProfileHandler profileHandler = Basic.getInstance().getProfileHandler();

        String executor = sender instanceof Player ? profileHandler.getProfile(((Player) sender).getUniqueId())
                .getColoredName() : "&4Console";

        Task.async(() -> {
            AtomicBoolean silent = new AtomicBoolean(false);

            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cPlease use /unmute <player>"));
                return;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("-s")) {
                silent.set(true);
            }

            String targetName = args[0];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if(targetData == null) targetData = profileHandler.load(offlinePlayer.getUniqueId(), offlinePlayer.getName());

            if (targetData.getMute() == null) {
                sender.sendMessage(Color.translate("&cThat player isn't muted."));
                return;
            }

            Punishment punishment = targetData.getMute();
            punishment.setRemovedAt(System.currentTimeMillis());
            punishment.setRemovedBy(executor);

            PunishmentHandler punishmentHandler = Basic.getInstance().getPunishmentHandler();
            punishmentHandler.pardon(targetData.getId(), punishment);

            targetData.setMute(null);

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
            if(silent.get()) Common.broadcastMessage(PermLevel.STAFF, "&7[Silent] " + targetData.getColoredName() + " &awas unmuted by " + executor);
            else Common.broadcastMessage(PermLevel.DEFAULT, targetData.getColoredName() + " &awas unmuted by " +executor);
        });
    }
}
