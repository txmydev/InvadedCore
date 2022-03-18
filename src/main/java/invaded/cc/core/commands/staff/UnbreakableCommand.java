package invaded.cc.core.commands.staff;

import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class UnbreakableCommand extends BasicCommand {

    public UnbreakableCommand() {
        super("unbreakable", PermLevel.DEVELOPER);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;
        for(ItemStack stack : player.getInventory().getArmorContents()) {
            if(stack != null){
                stack.addUnsafeEnchantment(Enchantment.DURABILITY, 200);
            }

        }

        for(ItemStack stack : player.getInventory().getContents()) {
            if(stack != null) stack.addUnsafeEnchantment(Enchantment.DURABILITY, 200);
        }
    }
}
