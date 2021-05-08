package invaded.cc.util.command;

import invaded.cc.util.Color;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Arrays;


public abstract class InvadedCommand extends Command {

    private static CommandMap COMMAND_MAP;

    public final String name;
    public final PermLevel permLevel;
    public final String[] aliases;

    public InvadedCommand(String name, PermLevel permLevel, String... aliases){
        super(name,"", "", Arrays.asList(aliases));

        this.name = name;
        this.aliases = aliases;
        this.permLevel = permLevel;


        if(COMMAND_MAP == null){
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

    public void register(){
        if(COMMAND_MAP.getCommand(name) != null) {
            COMMAND_MAP.getCommand(name).unregister(COMMAND_MAP);
            System.out.println("Unregistered command " + name + " because it was duplicated.");
            return;
        }
        COMMAND_MAP.register(name, this);
    }

    public abstract void execute(CommandSender sender, String[] args);

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!Permission.test(commandSender, permLevel) && commandSender instanceof Player){
            commandSender.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
            return true;
        }

        this.execute(commandSender, strings);
        return true;
    }
}
