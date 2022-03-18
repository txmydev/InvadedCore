package invaded.cc.core.commands.player;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.settings.SettingsMenu;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SettingsCommand extends BasicCommand {

    public SettingsCommand() {
        super("settings", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        new SettingsMenu(Spotify.getInstance().getProfileHandler().getProfile((Player) sender)).open((Player) sender);
    }
}
