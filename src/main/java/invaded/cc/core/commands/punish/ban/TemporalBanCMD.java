package invaded.cc.core.commands.punish.ban;

import invaded.cc.core.Spotify;
import invaded.cc.core.event.PlayerPunishEvent;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.Punishment;
import invaded.cc.core.util.*;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicBoolean;

public class TemporalBanCMD extends BasicCommand {

    public TemporalBanCMD() {
        super("tempban", PermLevel.STAFF, "tban");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        String executor = sender instanceof Player ? profileHandler.getProfile(((Player) sender).getUniqueId())
                .getColoredName() : "&4Console";

        Task.async(() -> {
            AtomicBoolean silent = new AtomicBoolean(false);

            if (args.length < 2) {
                sender.sendMessage(Color.translate("&cPlease use /tempban <player> <time> <reason>"));
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

            if (!isValid(args[1])) {
                sender.sendMessage(Color.translate("&cNot a valid time."));
                return;
            }

            long time = System.currentTimeMillis() + DateUtils.parseTime(args[1]);

            String targetName = Common.getName(args[0]);
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if (targetData == null) targetData = profileHandler.load(offlinePlayer.getUniqueId(), args[0]);

            if (targetData.isBanned()) {
                sender.sendMessage(Color.translate("&cThat player is already banned, unban him before banning him again."));
                return;
            }

            Punishment punishment = new Punishment(Punishment.Type.TEMPORARY_BAN, System.currentTimeMillis(), time,
                    offlinePlayer.getName(), offlinePlayer.getUniqueId(), executor, silent.get(), reason.toString(), targetData.getAddress());

            PlayerPunishEvent event = new PlayerPunishEvent(executor, offlinePlayer, punishment, false);
            event.call();

            if (event.isCancelled()) {
                return;
            }

            if (silent.get())
                Common.broadcastMessage(PermLevel.STAFF, new Clickable("&7[Silent] " + executor + " &ahas temporary banned " + targetData.getColoredName(),
                        "&bReason&7: &f" + reason.toString(), null).asComponents());
            else
                Common.broadcastMessage(PermLevel.DEFAULT, executor + " &ahas temporary banned " + targetData.getColoredName());

            if (offlinePlayer.getPlayer() != null)
                Task.run(() -> offlinePlayer.getPlayer().kickPlayer(Common.getDisallowedReason(punishment)));
        });
    }

    public boolean isValid(String input) {
        try {
            Integer.parseInt(input.substring(0, input.length() - 1));
        } catch (NumberFormatException ex) {
            System.out.println("Error occurred while parsing " + input);
            return false;
        }

        String timeInput = input.substring(input.length() - 1).toLowerCase();
        return timeInput.equals("y") || timeInput.equals("mo") || timeInput.equals("d")
                || timeInput.equals("w") || timeInput.equals("h") || timeInput.equals("m")
                || timeInput.equals("s");
    }
}
