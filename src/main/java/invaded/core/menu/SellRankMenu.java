package invaded.core.menu;

import invaded.core.Spotify;
import invaded.core.grant.Grant;
import invaded.core.profile.Profile;
import invaded.core.rank.Rank;
import invaded.core.util.Color;
import invaded.core.util.Common;
import invaded.core.util.ItemBuilder;
import invaded.core.util.menu.Menu;
import invaded.core.util.perms.PermLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.stream.Collectors;

public class SellRankMenu extends Menu {

    private Profile profile;

    public SellRankMenu(Profile profile) {
        super("&eBuy your rank!", 45);

        this.profile = profile;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(event.getSlot() == 44) {
            new CosmeticsMenu(profile).open(player);
            return;
        }

        if(event.getSlot() == 20) {
            this.buy(player, Ranks.TITAN);
        }

        if(event.getSlot() == 22) {
            this.buy(player, Ranks.LEGEND);
        }

        if(event.getSlot() == 24) {
            this.buy(player, Ranks.ULTRA);
        }
    }

    private void buy(Player player, Ranks rankEnum) {
        Rank rank = Spotify.getInstance().getRankHandler().getRank(rankEnum.getName());
        if(!profile.canAfford(rankEnum.getPrice())) return;

        profile.removeCoins(rankEnum.getPrice());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "grant " + profile.getName() + " " + rank.getName());
        Common.broadcastMessage(PermLevel.ADMIN, "&7[&eRank Sold&7] "+ profile.getColoredName() + " &ejust bought " + rank.getColoredName() + " &erank.");

        player.sendMessage(Color.translate("&aYou're rank has been updated to " + rank.getColoredName() + "&a, you may relog to see the changes."));
        player.closeInventory();

    }

    private ItemStack pane() {
        return new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(7).name("&7 ").build();
    }

    @Override
    public void update() {
        Rank titan = Spotify.getInstance().getRankHandler().getRank(Ranks.TITAN.getName());
        Rank ultra = Spotify.getInstance().getRankHandler().getRank(Ranks.ULTRA.getName());
        Rank legend = Spotify.getInstance().getRankHandler().getRank(Ranks.LEGEND.getName());

        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, pane());
        }

        inventory.setItem(20, new ItemBuilder().type(Material.IRON_BLOCK)

                .name("&cTitan Rank")
                .lore(Common.getLine(40),
                        "&7If you purchase this rank, you will be",
                        "&7looking like this: " + getFormat(titan),
                        " ",
                        price(Ranks.TITAN, titan.getColoredName()),
                        Common.getLine(40)).build());

        inventory.setItem(24, new ItemBuilder().type(Material.GOLD_BLOCK).name("&eUltra Rank")
                .lore(Common.getLine(40),
                        "&7If you purchase this rank, you will be",
                        "&7looking like this: " + getFormat(ultra),
                        " ",
                        price(Ranks.ULTRA, ultra.getColoredName()),
                        Common.getLine(40)).build());

        inventory.setItem(22, new ItemBuilder().type(Material.DIAMOND_BLOCK).name("&bLegend Rank")
                .lore(Common.getLine(40),
                        "&7If you purchase this rank, you will be",
                        "&7looking like this: " + getFormat(legend),
                        " ",
                        price(Ranks.LEGEND, legend.getColoredName()),
                        Common.getLine(40)).build());

        inventory.setItem(44, new ItemBuilder().type(Material.ENDER_CHEST).name("&7Back to &dCosmetics").build());

    }

    private String price(Ranks rank, String name) {
        return name + "'s &eprice is &6&l" + rank.getPrice() + " coins&e.";
    }

    private String hasRank(Ranks rank) {
        return profile.getGrants().stream().map(Grant::getRank).collect(Collectors.toList()).contains(rank.getName()) ? "&eYou already have this rank"
                + (profile.getHighestRank().getName().equals(rank.getName()) ? "&e, this is your current rank." : "&e, but you have a higher grant active.") :
                profile.canAfford(rank.getPrice()) ? "&aYou can afford this rank, click to buy it." : "&cYou cannot afford for this rank.";
    }

    private String getFormat(Rank rank) {
        return rank.getPrefix() + rank.getColors() + profile.getName() + rank.getSuffix() + " &7: &fHello!";
    }


    @AllArgsConstructor
    @Getter
    public enum Ranks {

        TITAN("Titan", 500, 1),
        ULTRA("Ultra", 750, 2),
        LEGEND("Legend", 1000, 3);

        private final String name;
        private final int price;
        private final int weight;
    }
}
