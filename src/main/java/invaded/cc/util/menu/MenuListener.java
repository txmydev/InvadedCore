package invaded.cc.util.menu;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class MenuListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        if(event.getCurrentItem() == null || event.getCurrentItem().getType().equals(Material.AIR)) return;

        if(event.getInventory().getHolder() instanceof IMenu){
            event.setCancelled(true);
            ((IMenu)event.getInventory().getHolder()).onClick(event);
        }
    }
}
