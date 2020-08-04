package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleStaffCommand extends InvadedCommand {

    public ToggleStaffCommand(){
        super("togglestaff", PermLevel.STAFF);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;

        if(args.length != 0) {
            player.sendMessage(Color.translate("&cPlease use /togglestaff"));
            return;
        }

        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        boolean v = !profile.isStaffAlerts();

        profile.setStaffAlerts(v);

        player.sendMessage(Color.translate((v ? "&a" : "&c") + "You toggled your helpop & report requests."));
    }
}
