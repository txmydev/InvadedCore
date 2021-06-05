package invaded.core.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemBuilder {

    private Material mat;
    private int data, amount;
    private String name;
    private List<String> lore;

    public ItemBuilder() {
        this.mat = Material.AIR;
        this.amount = 1;
        this.data = -1;
        this.lore = new ArrayList<>();
        this.name = "";
    }

    public ItemBuilder type(Material mat) {
        this.mat = mat;
        return this;
    }

    public ItemBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder lore(String... lines) {
        for (String s : lines)
            this.lore.add(Color.translate(s));
        return this;
    }

    public ItemBuilder data(int data) {
        this.data = data;
        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemStack build() {
        ItemStack item;
        if (data == -1)
            item = new ItemStack(mat, amount);
        else
            item = new ItemStack(mat, amount, (short) data);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(Color.translate(name));
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }


    public ItemStack buildSkull(String name) {
        ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setDisplayName(Color.translate(name));
        meta.setOwner(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }
}
