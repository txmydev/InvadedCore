package invaded.cc.core.commands.punish.ban;

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

public class BanCMD extends BasicCommand {

    public BanCMD() {
        super("ban", PermLevel.STAFF);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

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

            String targetName = Common.getName(args[0]);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);
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
