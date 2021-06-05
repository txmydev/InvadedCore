package invaded.core.commands;

import invaded.core.Spotify;
import invaded.core.profile.Profile;
import invaded.core.profile.ProfileHandler;
import invaded.core.util.Color;
import invaded.core.util.command.BasicCommand;
import invaded.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleStaffCommand extends BasicCommand {

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

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        boolean v = !profile.isStaffAlerts();

        profile.setStaffAlerts(v);

        player.sendMessage(Color.translate((v ? "&a" : "&c") + "You toggled your helpop & report requests."));
    }
}
