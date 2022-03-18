package invaded.cc.core.commands.staff;

import com.google.common.base.Strings;
import invaded.cc.core.Spotify;
import invaded.cc.core.util.BooleanUtils;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class ServerCommand extends BasicCommand {

    private final Spotify plugin;

    public ServerCommand(Spotify plugin) {
        super("servers", PermLevel.ADMIN);

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        sender.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + Strings.repeat("-", 40));
        plugin.getServerHandler().getServerMap().forEach((name, server) -> {
            sender.sendMessage(CC.YELLOW + "Server " + CC.RED + name + CC.YELLOW + ": ");
            sender.sendMessage(Strings.repeat(" ", 5) + CC.WHITE + "Online: " + CC.YELLOW + server.getOnline());
            sender.sendMessage(Strings.repeat(" ", 5) + CC.WHITE + "Testing: " + BooleanUtils.getValueWithSymbols(server.isTesting()));
            sender.sendMessage(Strings.repeat(" ", 5) + CC.WHITE + "Maintenance: " + BooleanUtils.getValueWithSymbols(server.isMaintenance()));
            sender.sendMessage(Strings.repeat(" ", 5) + CC.WHITE + "Has Extra Info: " + BooleanUtils.getValueWithSymbols(!server.getExtraInfo().isEmpty()));
        });

        sender.sendMessage(CC.GRAY + CC.STRIKE_THROUGH + Strings.repeat("-", 40));
    }
}
