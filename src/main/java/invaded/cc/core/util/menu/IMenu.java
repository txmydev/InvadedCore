package invaded.cc.core.util.menu;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public interface IMenu extends InventoryHolder {

    void onClick(InventoryClickEvent event);

    default void open(Player player) {
        player.openInventory(this.getInventory());
    }

}
