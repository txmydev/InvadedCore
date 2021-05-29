package invaded.cc.menu.prefix;

import invaded.cc.Basic;
import invaded.cc.prefix.Prefix;
import invaded.cc.prefix.PrefixHandler;
import invaded.cc.profile.Profile;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.ItemBuilder;
import invaded.cc.util.menu.Menu;
import net.exe.euhc.util.ItemCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class PrefixMenu extends Menu {

    private Profile profile;
    private int page;
    private int maxPrefixesPerPage = 21;

    public PrefixMenu(Profile profile) {
        super("&dPrefixes", 45);

        this.profile = profile;
        this.page = 1;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final Profile profile = Basic.getInstance().getProfileHandler().getProfile(player.getUniqueId());

        if(event.getSlot() == 0) {
            if(page == 1) return;
            page--;
            this.update();

            player.sendMessage("page " + this.page);
            return;
        }

        if(event.getSlot() == 8){
            if(page == getMaxPages()) return;
            page++;
            this.update();
            player.sendMessage("page " + this.page);
            return;
        }

        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) return;
        ItemMeta meta = event.getCurrentItem().getItemMeta();
        String strippedItemName = ChatColor.stripColor(meta.getDisplayName());

        Basic.getInstance().getPrefixHandler().getPrefixes().forEach(prefix -> {
            String displayStripped = ChatColor.stripColor(prefix.getDisplay());

            if(!strippedItemName.equals(displayStripped)) return;

            if(hasPrefix(prefix)) {
                togglePrefix(profile, prefix);
                return;
            }

            if(hasCoins2Buy(prefix)) {
                this.buy(profile, prefix);
                this.update();
            }
        });
    }

    private int getMaxPages() {
        return Basic.getInstance().getPrefixHandler().getPrefixes().size() / maxPrefixesPerPage + 1;
    }

    private void buy(Profile profile, Prefix prefix) {
        Player player = Bukkit.getPlayer(profile.getId());

        profile.removeCoins(prefix.getPrice());
        profile.getPrefixes().add(prefix);

        player.sendMessage(Color.translate("&aYou successfully bought &6" + prefix.getId() + "&a! Feel free to use it ;)"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
    }

    private void togglePrefix(Profile profile, Prefix prefix) {
        profile.setActivePrefix(prefix);
        Player player = Bukkit.getPlayer(profile.getId());
        player.closeInventory();
        player.sendMessage(Color.translate("&aYou are now using &e" + prefix.getId() + " &a prefix."));
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
    }

    @Override
    public void update() {
        this.inventory.clear();

        PrefixHandler prefixHandler = Basic.getInstance().getPrefixHandler();
        List<Prefix> list = prefixHandler.getPrefixes();

        for (int i = 0; i <= 8; i++)
            inventory.setItem(i, pane());


        for (int i = 36; i <= 44; i++)
            inventory.setItem(i, pane());


        for (int i : Arrays.asList(17, 26, 35, 9, 18, 27))
            inventory.setItem(i, pane());


        inventory.setItem(0, new ItemCreator(Material.CARPET, 1, 14).setName("&cPrevious Page").get());
        inventory.setItem(8, new ItemCreator(Material.CARPET, 1, 5).setName("&aNext Page").get());

        int itemSlot = 10;
        int index = page * maxPrefixesPerPage - maxPrefixesPerPage;
        while (index < list.size() && itemSlot <= 34) {
            Prefix prefix = list.get(index);

            ItemBuilder builder = new ItemBuilder().type(Material.NAME_TAG)
                    .name(prefix.getDisplay())
                    .lore(Common.getLine(30),
                            "&bYou'll look like this: " + getFormat(prefix),
                            " ",
                            "&bThis prefix costs &6" + prefix.getPrice() + " coins&b.",
                            boughtIt(prefix));

            if(!hasCoins2Buy(prefix) && !hasPrefix(prefix)) builder.lore("&bYou can buy coins at &6store.skulluhc.club");
            builder.lore(Common.getLine(30));
            inventory.setItem(itemSlot, builder.build());

            if(itemSlot == 16 || itemSlot == 25) itemSlot = itemSlot + 2 ;
            itemSlot++;
            index++;
        }
    }

    private boolean hasPrefix(Prefix prefix ){
        return profile.getPrefixes().stream().map(Prefix::getId)
                .collect(Collectors.toList()).contains(prefix.getId());
    }

    private String boughtIt(Prefix prefix){
        return hasPrefix(prefix) ? "&aYou already have this prefix, click it to toggle it."
                : hasCoins(prefix);
    }

    private String hasCoins(Prefix prefix) {
        return profile.getCoins() >= prefix.getPrice() ? "&6Click to buy this prefix" : "&cYou don't have enough coins to buy this prefix.";
    }

    private boolean hasCoins2Buy(Prefix prefix) {
        return profile.getCoins() >= prefix.getPrice();
    }

    private ItemStack pane() {
        return new ItemCreator(Material.STAINED_GLASS_PANE, 1, 7).setName("&7 ").get();
    }

    private String getFormat(Prefix prefix) {
        return prefix.getDisplay() + " " + profile.getHighestRank().getPrefix() + profile.getHighestRank().getColors() + (profile.getChatColor() == null ? "" : profile.getChatColor()) + (profile.isItalic() ? ChatColor.ITALIC : "") +
                (profile.isSpaceBetweenRank() ? " " : "") + profile.getName()
                + profile.getHighestRank().getSuffix();
    }

    private boolean isUsing(Prefix prefix) {
        return profile.getActivePrefix() != null && profile.getActivePrefix().getId().equals(prefix.getId());
    }


}
