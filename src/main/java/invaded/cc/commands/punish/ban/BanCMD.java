package invaded.cc.commands.punish.ban;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.event.PlayerPunishEvent;
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

public class BanCMD extends InvadedCommand {

    public BanCMD() {
        super("ban", PermLevel.STAFF);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

        String executor = sender instanceof Player ? profileHandler.getProfile(((Player) sender).getUniqueId())
                .getColoredName() : "&4Console";

        Task.async(() -> {
            AtomicBoolean silent = new AtomicBoolean(false);

            if (args.length < 1) {
                sender.sendMessage(Color.translate("&cPlease use /ban <player> <reason>"));
                return;
            }

            StringBuilder reason = new StringBuilder();

            for (String s : args) {
                if (s.equalsIgnoreCase(args[0])) continue;

                if (s.equalsIgnoreCase("-s")) {
                    silent.set(true);
                    continue;
                }

                reason.append(Color.translate(s)).append(" ");
            }

            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if (targetData == null) targetData = profileHandler.load(offlinePlayer.getUniqueId(), args[0]);

            if (targetData.isBanned()) {
                sender.sendMessage(Color.translate("&cThat player is already banned, unban him before banning him again."));
                return;
            }
            Punishment punishment = new Punishment(Punishment.Type.BAN, System.currentTimeMillis(), -1L, targetData.getName()
            , targetData.getId(), executor, silent.get(),reason.toString());

            PlayerPunishEvent event = new PlayerPunishEvent(executor, offlinePlayer, punishment, false);
            event.call();

            if (event.isCancelled()) {
                return;
            }

            if (silent.get()) Common.broadcastMessage(PermLevel.STAFF, new Clickable("&7[Silent] " + executor + " &ahas permanently banned " + targetData.getColoredName()).hover(HoverEvent.Action.SHOW_TEXT, "&bReason&7: &f" + reason.toString()).get());
            else Common.broadcastMessage(PermLevel.DEFAULT, executor + " &ahas permanently banned " + targetData.getColoredName());

            if(offlinePlayer.getPlayer() != null) Task.run(() -> offlinePlayer.getPlayer().kickPlayer(Common.getDisallowedReason(punishment)));
        });
    }
}
