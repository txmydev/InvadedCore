package invaded.cc.commands;

import invaded.cc.Spotify;
import invaded.cc.menu.TrailsMenu;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
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
