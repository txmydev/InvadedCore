package invaded.cc.core.commands;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class ServerTestingCommand extends BasicCommand {

    public ServerTestingCommand(){
        super("testingmode", PermLevel.ADMIN, "settestingmode", "toggletesting");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 0) return;

        boolean newValue = !Spotify.getInstance().getServerHandler().isTesting();
        Spotify.getInstance().getServerHandler().setTestingMode(Spotify.SERVER_NAME, newValue);
    }
}
