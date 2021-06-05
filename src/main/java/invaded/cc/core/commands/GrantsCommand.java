package invaded.cc.core.commands;

import invaded.cc.core.Spotify;
import invaded.cc.core.grant.GrantHandler;
import invaded.cc.core.menu.GrantsMenu;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GrantsCommand extends BasicCommand {

    public GrantsCommand() {
        super("grants", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            if (!(sender instanceof Player)) return;

            if (args.length != 1) {
                sender.sendMessage(Color.translate("&c/grants <name>"));
                return;
            }

            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
            String target = args[0];
            UUID uuid = Bukkit.getOfflinePlayer(target).getUniqueId();

            Profile profile = profileHandler.getProfile(uuid);

            if (profile == null) {
                profile = profileHandler.load(uuid, target, false);
            }

            sender.sendMessage(Color.translate("&aFetching recent grants of the player, when its ready, and inventory will be opened."));
            GrantHandler grantHandler = Spotify.getInstance().getGrantHandler();
            profile.setGrants(grantHandler.get(profile));

            new GrantsMenu(profile).open(((Player) sender));
        });
    }
}
