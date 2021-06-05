package invaded.cc.core.util.menu;

import invaded.cc.core.util.Color;
import invaded.cc.core.util.Task;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;

public abstract class Menu implements IMenu{

    public final Inventory inventory;

    public static final List<Menu> menus = new ArrayList<>();

    @Setter @Getter
    private boolean update = false;

    public Menu(String title, int size){
        title = Color.translate(title.length() > 32 ? title.substring(0, 32) : title);
        inventory = Bukkit.createInventory(this, size, title);

        if(!menus.contains(this)) menus.add(this);
    }

    @Override
    public void open(Player player) {
        update();
        Task.run(() -> player.openInventory(inventory));
    }

    public abstract void update();

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }
}
