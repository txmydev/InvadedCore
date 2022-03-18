package invaded.cc.core.menu;

import invaded.cc.core.profile.Profile;
import invaded.cc.core.trails.Trail;
import invaded.cc.core.util.*;
import invaded.cc.core.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class TrailsMenu extends Menu {

    private Profile profile;
    private Map<Integer, Trail> trailMap;

    public TrailsMenu(Profile profile) {
        super("&bBuy & use trails!", 45);

        this.profile = profile;
        this.trailMap = new HashMap<>();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 44) {
            player.closeInventory();
            Task.later(() -> new CosmeticsMenu(profile).open(player), 2L);
            return;
        }

        if (event.getSlot() == 10) {
            profile.setActiveTrail(null);
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 0.5f);
            player.sendMessage(Color.translate("&cYou are no longer using a trail."));
            return;
        }

        if (!trailMap.containsKey(event.getSlot())) return;
        Trail trail = trailMap.get(event.getSlot());

        if (hasTrail(trail)) {
            if(trail == Trail.FIREWORKS) {
                player.sendMessage(CC.RED + "This trail is deactivated for now.");
                return;
            }
            toggle(player, trail, true);
            return;
        }

        if (profile.getCoins() >= trail.getPrice()) {
            this.buy(player, trail);
            return;
        }
    }

    private void buy(Player player, Trail trail) {
        profile.setCoins(profile.getCoins() - trail.getPrice());
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
        profile.getTrails().add(trail);
        player.sendMessage(Color.translate("&aYou've successfully bought the trail &e'" + trail.getDisplay() + "'"));

        toggle(player, trail, false);
    }

    private void toggle(Player player, Trail trail, boolean msgAndSound) {
        profile.setActiveTrail(trail);
        player.closeInventory();

        if (!msgAndSound) return;
        player.playSound(player.getLocation(), Sound.ANVIL_USE, 1.0f, 1.0f);
        player.sendMessage(Color.translate("&aYou are now using &e" + trail.getDisplay() + " &afor the bow and the rod."));
    }

    private boolean hasTrail(Trail trail) {
        return profile.getTrails().stream().map(Trail::getId).collect(Collectors.toList()).contains(trail.getId());
    }

    @Override
    public void update() {
        List<Integer> whitelistedSlots = new ArrayList<>();

        inventory.setItem(10, new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(14).name("&cNone").lore(Common.getLine(40),
                "&7Disable your current trail",
                Common.getLine(40)).build());
        int slot = 11;
        for (Trail trail : Trail.values()) {
            if(trail == Trail.FIREWORKS) continue;
            if (slot > 34) break;

            ItemBuilder item = new ItemBuilder().type(trail.getMaterial()).name("&e" + trail.getDisplay());
            item.lore(Common.getLine(40),
                    "&bThe price of this trail is &6" + trail.getPrice() + " coins&a.",
                    " ",
                    hasIt(trail),
                    Common.getLine(40));
            inventory.setItem(slot, item.build());
            whitelistedSlots.add(slot);
            trailMap.put(slot, trail);

            if (slot == 16 || slot == 25) slot = slot + 2;
            slot++;
        }

        inventory.setItem(44, new ItemBuilder().type(Material.ENDER_CHEST).name("&7Back to &dCosmetics").build());
        inventory.setItem(40, new ItemBuilder().type(Material.GOLD_NUGGET).name("&bYour Coins&7: &6" + profile.getCoins() + " ©").build());
        whitelistedSlots.addAll(Arrays.asList(10, 44, 40));

        for (int i = 0; i < inventory.getSize(); i++) {
            if (whitelistedSlots.contains(i)) continue;
            inventory.setItem(i, pane());
        }
    }

    private ItemStack pane() {
        return new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(7).name("&7 ").build();
    }

    private String hasIt(Trail trail) {
        return hasTrail(trail) ? "&eYou have this trail, click to toggle it" : (profile.getCoins() >= trail.getPrice() ? "&aYou can afford this trail, click to buy it" : "&cYou cannot afford for this trail.");
    }
}
