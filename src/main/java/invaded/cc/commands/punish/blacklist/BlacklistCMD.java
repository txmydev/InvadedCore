package invaded.cc.commands.punish.blacklist;

import invaded.cc.Spotify;
import invaded.cc.event.PlayerPunishEvent;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.Punishment;
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

public class BlacklistCMD extends BasicCommand {

    public BlacklistCMD() {
        super("blacklist", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

            String executor = sender instanceof Player ? profileHandler.getProfile(((Player) sender).getUniqueId())
                    .getColoredName() : "&4Console";

            AtomicBoolean silent = new AtomicBoolean(false);

            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cPlease use /blacklist <player>"));
                return;
            }

            if (args.length == 2 && args[1].equalsIgnoreCase("-s")) {
                silent.set(true);
            }

            if (args.length > 2) {
                sender.sendMessage(Color.translate("&cPlease use /blacklist <player>"));
                return;
            }

            String targetName = args[0];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(targetName);

            Profile targetData = profileHandler.getProfile(offlinePlayer.getUniqueId());
            if (targetData == null) targetData = profileHandler.load(offlinePlayer.getUniqueId(), args[0]);


            if (targetData.isBanned()) {
                sender.sendMessage(Color.translate("&cThat player is already banned or blacklisted, unban him before banning him again."));
                return;
            }

            Punishment punishment = new Punishment(Punishment.Type.BLACKLIST, System.currentTimeMillis(), -1L,
                    offlinePlayer.getName(), offlinePlayer.getUniqueId(), executor, silent.get(), "Blacklisted");

            PlayerPunishEvent event = new PlayerPunishEvent(executor, offlinePlayer, punishment, false);
            event.call();

            if (event.isCancelled()) {
                return;
            }

            if (silent.get()) Common.broadcastMessage(PermLevel.STAFF,
                    "&7[Silent] " + executor + " &ahas permanently blacklisted " + targetData.getColoredName());
            else
                Common.broadcastMessage(PermLevel.DEFAULT, executor + " &ahas permanently blacklisted " + targetData.getColoredName());

            if (offlinePlayer.getPlayer() != null) Task.run(() -> offlinePlayer.getPlayer().kickPlayer(Common.getDisallowedReason(punishment)));
        });
    }
}
