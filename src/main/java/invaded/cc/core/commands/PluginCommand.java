package invaded.cc.core.commands;

import invaded.cc.core.util.Color;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PluginCommand extends BasicCommand {

    public PluginCommand() {
        super("plugins", PermLevel.STAFF, "pl", "checkplugins");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player jugador = (Player) sender;

        jugador.sendMessage(Color.translate("&7&m---------------------------------"));
        jugador.sendMessage(Color.translate("&bTotal Plugins&7: &7(&f" + Bukkit.getPluginManager().getPlugins().length) + "§7)");
        jugador.sendMessage(" ");
        Plugin[] plugins = Bukkit.getServer().getPluginManager().getPlugins();
        for (Plugin p : plugins) {
            String message = "§7┣ §a" + p.getName() + " " + "§7(§7Author§7: §c" + String.valueOf(p.getDescription().getAuthors()).replace("[", "").replace("]", "") + "§7)" + "§a §7(§cv§c" + p.getDescription().getVersion() + "§7)";
            jugador.sendMessage(message);
        }
        jugador.sendMessage(Color.translate("&7&m---------------------------------"));

    }

}


