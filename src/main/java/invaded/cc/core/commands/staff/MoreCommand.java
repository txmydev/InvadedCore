package invaded.cc.core.commands.staff;

import invaded.cc.core.util.CC;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MoreCommand extends BasicCommand {

    public MoreCommand() {
        super("more", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;
        if(args.length != 0) {
            player.sendMessage(CC.RED + "Use /more.");
            return;
        }

        ItemStack stack = player.getItemInHand();
        if(stack == null || stack.getType() == Material.AIR) {
            player.sendMessage(CC.RED + "You're not holding anything.");
            return;
        }

        stack.setAmount(64);
        player.updateInventory();
    }
}
