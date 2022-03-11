package invaded.cc.core.commands.punish.mute;

import invaded.cc.core.Spotify;
import invaded.cc.core.event.PlayerPunishEvent;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.Punishment;
import invaded.cc.core.util.Clickable;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class MuteCMD extends BasicCommand {

    public MuteCMD() {
        super("mute", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

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

            String targetName = Common.getName(args[0]);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if (targetData == null) targetData = profileHandler.load(offlinePlayer.getUniqueId(), args[0]);

            if (targetData.isMuted()) {
                sender.sendMessage(Color.translate("&CPlease unmute him first."));
                return;
            }
            if (silent.get()) Common.broadcastMessage(PermLevel.STAFF,
                    new Clickable("&7[Silent] " + executor + " &ahas permanently muted " + targetData.getColoredName())
                            .hover(HoverEvent.Action.SHOW_TEXT, "&bReason&7: &f" + reason.toString()).get());
            else
                Common.broadcastMessage(PermLevel.DEFAULT, executor + " &ahas permanently muted " + targetData.getColoredName());

            Punishment punishment = new Punishment(Punishment.Type.MUTE, System.currentTimeMillis(), -1L, targetData.getName(),
                    offlinePlayer.getUniqueId(), executor, silent.get(), reason.toString(), targetData.getAddress());

            PlayerPunishEvent event = new PlayerPunishEvent(executor, offlinePlayer, punishment, false);
            event.call();

            if (event.isCancelled()) {
                return;
            }

            if (offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().sendMessage(Color.translate("&cYou have been permanently muted."));
            }
        });
    }
}
