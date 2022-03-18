package invaded.cc.core.commands.staff;

import invaded.cc.core.util.Color;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class SkullCommand extends BasicCommand {

    public SkullCommand() {
        super("skull", PermLevel.STAFF);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        if (args.length != 1) {
            sender.sendMessage(Color.translate("&c/skull <player>"));
            return;
        }

        String target = args[0];
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();

        meta.setOwner(target);
        item.setItemMeta(meta);

        Player player = (Player) sender;
        player.getInventory().addItem(item);
    }
}
