package invaded.cc.core.util;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class ItemBuilder {

    private Material mat;
    private int data, amount;
    private String name;
    private List<String> lore;

    private ItemStack stack;

    public ItemBuilder(Material material) {
        this();

        type(material);
    }


    public ItemBuilder() {
        this.mat = Material.AIR;
        this.amount = 1;
        this.data = -1;
        this.lore = new ArrayList<>();
        this.name = "";

        this.stack = new ItemStack(mat, amount);

    }

    public ItemBuilder type(Material mat) {
        this.mat = mat;
        this.stack.setType(mat);
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

    public ItemBuilder loreIf(Supplier<Boolean> condition, String... lines){
        if(condition.get()) lore(lines);
        return this;
    }

    public ItemBuilder data(int data) {
        this.data = data;

        if(data != -1) stack.setDurability((short) data);

        return this;
    }

    public ItemBuilder amount(int amount) {
        this.amount = amount;

        stack.setAmount(amount);
        return this;
    }

    public ItemStack build() {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Color.translate(name));
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemBuilder enchantment(Enchantment enchantment, int level) {
        ItemMeta meta = stack.getItemMeta();
        if(stack.getType() == Material.ENCHANTED_BOOK) {
            EnchantmentStorageMeta storageMeta = (EnchantmentStorageMeta) meta;
            storageMeta.addStoredEnchant(enchantment, level, false);

            stack.setItemMeta(storageMeta);
            return this;
        } else {
            meta.addEnchant(enchantment, level, false);
            stack.setItemMeta(meta);
            return this;
        }
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
