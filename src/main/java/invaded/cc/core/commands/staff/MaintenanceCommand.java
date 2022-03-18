package invaded.cc.core.commands.staff;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class MaintenanceCommand extends BasicCommand {

    public MaintenanceCommand() {
        super("maintenance", PermLevel.ADMIN, "togglemaintenance");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 0) return;
        boolean newValue = !Spotify.getInstance().getServerHandler().isMaintenance();
        Spotify.getInstance().getServerHandler().setMaintenanceMode(Spotify.SERVER_NAME, newValue);
    }
}
