package invaded.cc.core.commands;

import invaded.cc.core.menu.TrailsMenu;
import invaded.cc.core.Spotify;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrailsCommand extends BasicCommand {

    public TrailsCommand() {
        super("trails", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
            new TrailsMenu(Spotify.getInstance().getProfileHandler().getProfile((Player) sender)).open(((Player) sender));
    }
}
