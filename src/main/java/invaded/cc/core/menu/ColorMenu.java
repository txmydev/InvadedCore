package invaded.cc.core.menu;

import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ItemBuilder;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ColorMenu extends Menu {

    private final ConcurrentMap<Integer, ChatColor> colors;

    private Profile profile;

    public ColorMenu(Profile profile) {
        super("&eChoose your color.", 45);

        this.profile = profile;
        this.colors = new ConcurrentHashMap<>();
    }

    @Override
    public void update() {
        int slot = 11;

        for (ChatColor color : ChatColor.values()) {
            if (color == ChatColor.ITALIC || color == ChatColor.MAGIC
                    || color == ChatColor.BOLD || color == ChatColor.UNDERLINE
                    || color == ChatColor.STRIKETHROUGH || color == ChatColor.RESET
            ) continue;

            inventory.setItem(slot, new ItemBuilder().type(Material.WOOL).data(getWoolData(color)
            ).name(color + color.name())
                    .lore(Common.getLine(40),
                            "&7You will look like this: " + getFormat(color),
                            Common.getLine(40))
                    .build());

            colors.put(slot, color);
            if (slot == 16 || slot == 25) slot = slot + 2;
            slot++;
        }

        inventory.setItem(slot, new ItemBuilder().type(Material.GLOWSTONE_DUST)
                .name("&eToggle your italic mode.")
                .lore(Common.getLine(40), "&7You will look like this: " + profile.getHighestRank().getColors() +
                                (profile.hasCustomColor() ? profile.getChatColor() + "" : "") +
                                ChatColor.ITALIC + profile.getName()
                        , Common.getLine(40)
                ).build());
        colors.put(slot, ChatColor.ITALIC);

        if (slot == 16 || slot == 25) slot = slot + 2;
        slot++;

        inventory.setItem(slot, new ItemBuilder().type(Material.COOKIE)
                .name("&eToggle your space between rank display name.")
                .lore(Common.getLine(40),
                        "&7You will look like this: " + getNameWithSpaceBetweenRank(profile, !profile.isSpaceBetweenRank()),
                        Common.getLine(40)).build());
        colors.put(slot, ChatColor.MAGIC);

        inventory.setItem(10, new ItemBuilder().type(Material.STAINED_GLASS_PANE)
                .data(14).name("&cReset Color").build());

        inventory.setItem(44, new ItemBuilder().type(Material.ENDER_CHEST).name("&7Back to &dCosmetics").build());

        for (int i = 0; i < inventory.getSize(); i++) {
            if (colors.containsKey(i) || i == 10 || i == 45) continue;
            inventory.setItem(i, pane());
        }
    }

    private ItemStack pane() {
        return new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(7).name("&7 ").build();
    }

    private String getFormat(ChatColor color) {
        return profile.getHighestRank().getPrefix() + profile.getHighestRank().getColors()
                + color + (profile.isItalic() ? ChatColor.ITALIC.toString() : "") +
                profile.getName() + profile.getHighestRank().getSuffix();
    }

    private String getNameWithSpaceBetweenRank(Profile profile, boolean toggle) {
        return profile.getHighestRank().getPrefix() + profile.getHighestRank().getColors() +
                (profile.getChatColor() == null ? "" : profile.getChatColor()) + (profile.isItalic() ? ChatColor.ITALIC : "") +
                (toggle ? " " : "") + profile.getName()
                + profile.getHighestRank().getSuffix();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if (event.getSlot() == 10) {
            profile.setChatColor(null);
            player.sendMessage(Color.translate("&cYou are now using your rank's color."));
            return;
        }

        if (event.getSlot() == 44) {
            player.closeInventory();
            Task.later(() -> new CosmeticsMenu(profile).open(player), 2L);
            return;
        }

        if (colors.containsKey(event.getSlot())) {
            ChatColor color = colors.get(event.getSlot());

            switch (color) {
                case ITALIC:
                    profile.setItalic(!profile.isItalic());
                    break;
                case MAGIC:
                    profile.setSpaceBetweenRank(!profile.isSpaceBetweenRank());
                    break;
                default:
                    profile.setChatColor(color);
                    break;
            }

            player.sendMessage(Color.translate("&aYou are now using color &e'" + color.name() + "&e'"));
        }
    }

    private int getWoolData(ChatColor color) {
        switch (color) {
            case WHITE:
                return 0;
            case AQUA:
            case BLUE:
                return 3;
            case YELLOW:
                return 4;
            case GREEN:
                return 5;
            case LIGHT_PURPLE:
                return 6;
            case DARK_GRAY:
                return 7;
            case GRAY:
                return 8;
            case DARK_AQUA:
                return 9;
            case DARK_PURPLE:
                return 10;
            case DARK_BLUE:
                return 11;
            case DARK_GREEN:
                return 13;
            case GOLD:
                return 1;
            case DARK_RED:
            case RED:
                return 14;
            case BLACK:
                return 15;
        }
        return 0;
    }

}
