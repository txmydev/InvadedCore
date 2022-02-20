package invaded.cc.core.util.command;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;


public abstract class BasicCommand extends Command {

    private static CommandMap COMMAND_MAP;

    public final String name;
    public final PermLevel permLevel;
    public final String[] aliases;

    public BasicCommand(String name, PermLevel permLevel, String... aliases) {
        super(name, "", "", Arrays.asList(aliases));

        this.name = name;
        this.aliases = aliases;
        this.permLevel = permLevel;


        if (COMMAND_MAP == null) {
            try {
                Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
                field.setAccessible(true);
                COMMAND_MAP = (CommandMap) field.get(Bukkit.getServer());
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        register();
    }

    public void register() {
        if(COMMAND_MAP.getCommand(name) != null) COMMAND_MAP.getCommand(name).unregister(COMMAND_MAP);
        COMMAND_MAP.register(name, "Spotify", this);
    }

    public abstract void execute(CommandSender sender, String[] args);

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (!Permission.test(commandSender, permLevel) && commandSender instanceof Player) {
            commandSender.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
            return true;
        }

        this.execute(commandSender, strings);
        return true;
    }

    public Player getPlayer(String name, CommandSender sender, String fallbackText) {
        Player player = Bukkit.getPlayer(name);
        if(player == null) {
            sender.sendMessage(CC.RED + fallbackText);
            return null;
        }
        return player;
    }
}
