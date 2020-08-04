package invaded.cc.commands.punish.mute;

import invaded.cc.Core;
import invaded.cc.event.PlayerPunishEvent;
import invaded.cc.profile.Profile;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.Punishment;
import invaded.cc.util.Clickable;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.Task;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class MuteCMD extends InvadedCommand {

    public MuteCMD(){
        super("mute", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

            String executor = sender instanceof Player ? profileHandler.getProfile(((Player) sender).getUniqueId())
                    .getColoredName() : "&4Console";

            AtomicBoolean silent = new AtomicBoolean(false);

            if (args.length < 2) {
                sender.sendMessage(Color.translate("&cPlease use /mute <player> <reason>"));
                return;
            }

            StringBuilder reason = new StringBuilder();

            for (int i = 2; i < args.length; i++) {
                if (args[i].equalsIgnoreCase("-s")) {
                    silent.set(true);
                    continue;
                }

                reason.append(args[i]).append(" ");
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if (targetData == null) targetData = profileHandler.load(offlinePlayer.getUniqueId(), args[0]);

            if(targetData.isMuted()) {
                sender.sendMessage(Color.translate("&CPlease unmute him first."));
                return;
            }
            if (silent.get()) Common.broadcastMessage(PermLevel.STAFF,
                    new Clickable("&7[Silent] " + executor + " &ahas permanently muted " + targetData.getColoredName())
            .hover(HoverEvent.Action.SHOW_TEXT, "&bReason&7: &f" + reason.toString()).get());
            else
                Common.broadcastMessage(PermLevel.DEFAULT, executor + " &ahas permanently muted " + targetData.getColoredName());

            Punishment punishment = new Punishment(Punishment.Type.MUTE, System.currentTimeMillis(), -1L, targetData.getName(),
                    offlinePlayer.getUniqueId(), executor, silent.get(), reason.toString());

            PlayerPunishEvent event = new PlayerPunishEvent(executor, offlinePlayer, punishment, false);
            event.call();

            if (event.isCancelled()) {
                return;
            }

            if(offlinePlayer.isOnline()){
                offlinePlayer.getPlayer().sendMessage(Color.translate("&cYou have been permanently muted."));
            }
        });
    }
}
