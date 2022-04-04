package invaded.cc.core.commands.staff;

import invaded.cc.core.util.CC;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportWorldCommand extends BasicCommand {

    public TeleportWorldCommand() {
        super("teleportworld", PermLevel.STAFF, "tpw");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0) {
            sender.sendMessage(CC.RED + "Do: /tpw <world>");
            return;
        }

        World world = Bukkit.getWorld(args[0]);
        if(world == null) {
            sender.sendMessage(CC.RED + "The specified world doesn't exist.");
            return;
        }

        ((Player) sender).teleport(world.getSpawnLocation().add(0, 0.5D, 0));
    }
}
