package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.grant.GrantHandler;
import invaded.cc.menu.GrantsMenu;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Task;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class GrantsCommand extends InvadedCommand {

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

            ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
            String target = args[0];
            UUID uuid = Bukkit.getOfflinePlayer(target).getUniqueId();

            Profile profile = profileHandler.getProfile(uuid);

            if (profile == null) {
                sender.sendMessage(Color.translate("&cThat profile doesn't exist."));
                return;
            }

            sender.sendMessage(Color.translate("&aFetching recent grants of the player, when its ready, and inventory will be opened."));
            GrantHandler grantHandler = Core.getInstance().getGrantHandler();
            profile.setGrants(grantHandler.get(profile));

            new GrantsMenu(profile).open(((Player) sender));
        });
    }
}
