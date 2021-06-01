package invaded.cc.menu;

import invaded.cc.menu.tags.TagsMenu;
import invaded.cc.profile.Profile;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.ItemBuilder;
import invaded.cc.util.menu.Menu;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class CosmeticsMenu extends Menu {

    private Profile profile;
    private static final int DISGUISE_PRICE = 475;

    public CosmeticsMenu(Profile profile) {
        super("&dCosmetics Menu", 45);
        this.profile = profile;
        this.inventory.getViewers().forEach(humanEntity -> humanEntity.getWorld().playSound(humanEntity.getLocation(), Sound.NOTE_STICKS, 1.0f, 1.0f));
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(event.getSlot() == 11) openColorMenu(player);
        if(event.getSlot() == 29) openTrailMenu(player);
        if(event.getSlot() == 33) handleDisguiseAccess(player);
        if(event.getSlot() == 15) openTagsMenu(player);
        if(event.getSlot() == 22) openRankBuyMenu(player);

    }

    private void openRankBuyMenu(Player player) {
        new SellRankMenu(profile).open(player);
    }

    private void openColorMenu(Player player){
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 0.5f);
        new ColorMenu(profile).open(player);
    }

    private void openTagsMenu(Player player){
        if(profile.getHighestRank().isDefaultRank()) {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 0.5f);
            player.sendMessage(Color.translate("&cYou cannot change your color unfortunately, you can buy a rank at &e&nstore.skulluhc.club &cor in the &e&nBuy Rank &emenu."));
            return;
        }

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 0.5f);
        new TagsMenu(profile).open(player);
    }

    private void handleDisguiseAccess(Player player) {
        if(profile.isAllowDisguise() || Permission.test(player, PermLevel.MEDIA)) {
            player.closeInventory();
            player.sendMessage(Color.translate("&eYou can do /disguise to disguise yourself."));
            player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1.0f,1.0f);
            return;
        }

        if(profile.getCoins() >= DISGUISE_PRICE) {
            profile.removeCoins(DISGUISE_PRICE);
            player.sendMessage(Color.translate("&aYou successfully bought &e'Disguise Access'&a. You can do /disguise to disguise yourself."));
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
            profile.setAllowDisguise(true);
        }
    }

    private void openTrailMenu(Player player) {
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 0.5f);
        new TrailsMenu(profile).open(player);
    }

    private ItemStack pane() {
        return new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(7).name("&7 ").build();
    }


    @Override
    public void update() {
        for(int i = 0; i < inventory.getSize(); i ++ ) {
            inventory.setItem(i, pane());
        }

        inventory.setItem(11, new ItemBuilder().type(Material.INK_SACK).data(8).name("&eName Color")
        .lore(Common.getLine(40)
        , "&7Click this display the name",
                "&7color customization menu",
                Common.getLine(40)).build());

        inventory.setItem(29, new ItemBuilder().type(Material.BOW).name("&eProjectile Trails")
        .lore(Common.getLine(40),
                "&7Click to display the trails menu in which",
                "&7you can buy and use trails for bows and rods.",
                Common.getLine(40)).build());

        inventory.setItem(33, new ItemBuilder().type(Material.INK_SACK).data(10).name("&eDisguise Access")
        .lore(Common.getLine(40),
                "&7Click to gain access to /disguise for you to appear as anybody else",
                "&7you can select the rank you want and also the skin",
                "&7this feature price is &6&l"+ DISGUISE_PRICE +" coins&7.",
                " ",
                hasDisguiseAccess(),
                Common.getLine(40)).build());

        inventory.setItem(15, new ItemBuilder().type(Material.NAME_TAG).name("&eTags")
            .lore(Common.getLine(40),
                    "&7Click to display our tag management menu",
                    "&7where you can buy tags like prefixs",
                    "&7and suffixs",
                    Common.getLine(40)).build());

        inventory.setItem(22, new ItemBuilder().type(Material.DIAMOND_BLOCK)
        .name("&eBuy Ranks!")
        .lore(Common.getLine(40),
                "&b&lNEW FEATURE!",
                "&7You can now buy ranks by using",
                "&7your coins which are earned by",
                "&7playing our UHC Games!",
                Common.getLine(40)).build());

        inventory.setItem(40, new ItemBuilder().type(Material.GOLD_NUGGET).name("&bYour Coins&7: &6" + profile.getCoins() + " ©").build());


    }

    private String hasDisguiseAccess() {
        return profile.isAllowDisguise() || Permission.test(profile, PermLevel.MEDIA) ? "&eYou currently have disguise access, you may use /d" : (profile.getCoins() >= 500 ? "&aBuy this feature for &6&l" + DISGUISE_PRICE + " coins&a!" : "&cYou cannot afford the disguise access feature.");
    }

}
