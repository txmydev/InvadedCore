package invaded.cc.core.commands.staff;

import invaded.cc.core.util.CC;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public class SearchCommand extends BasicCommand {

    public SearchCommand() {
        super("searchcommands", PermLevel.DEVELOPER);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        for(Plugin plugin : Bukkit.getServer().getPluginManager().getPlugins()) {
            sender.sendMessage(CC.AQUA + "Searching through " + plugin.getName() + " registered commands..");
            if(plugin.getDescription().getCommands() != null) {
                plugin.getDescription().getCommands().keySet().forEach(command -> {
                    if(command != null) sender.sendMessage(CC.GRAY + "   - " + command);
                });
            }
        }
    }
}
