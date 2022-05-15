package invaded.cc.core.commands.player;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class NameMcCommand extends BasicCommand {
    private final Spotify plugin;

    public NameMcCommand(Spotify plugin) {
        super("namemc", PermLevel.DEFAULT);

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Profile profile = plugin.getProfileHandler().getProfile((Player) sender);

        if(!profile.isNamemcVerified()) {

        }
    }
}
