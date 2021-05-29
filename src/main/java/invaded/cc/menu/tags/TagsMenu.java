package invaded.cc.menu.tags;

import invaded.cc.Basic;
import invaded.cc.tags.Prefix;
import invaded.cc.tags.Suffix;
import invaded.cc.tags.Tag;
import invaded.cc.tags.TagsHandler;
import invaded.cc.profile.Profile;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.ItemBuilder;
import invaded.cc.util.menu.Menu;
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

public class TagsMenu extends Menu {

    private Profile profile;
    private int page;
    private int maxPrefixesPerPage = 21;

    public TagsMenu(Profile profile) {
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

            return;
        }

        if(event.getSlot() == 8){
            if(page == getMaxPages()) return;
            page++;
            this.update();
            return;
        }

        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) return;
        ItemMeta meta = event.getCurrentItem().getItemMeta();
        String strippedItemName = Color.translate(meta.getDisplayName());
        Tag tag = check(strippedItemName);
        if(tag == null) return;

        if(hasTag(tag)) {
            toggle(profile, tag);
            return;
        }

        if(hasCoins2Buy(tag)) {
            this.buy(profile, tag);
            this.update();
            return;
        }
    }

    private Tag check(String itemName){
        for (Tag tag : Basic.getInstance().getTagsHandler().getTags()) {
            String displayStripped = Color.translate(tag.getDisplay());

            if (!displayStripped.equalsIgnoreCase(itemName))
                continue;


            return tag;
        }

        return null;
    }

    private int getMaxPages() {
        return Basic.getInstance().getTagsHandler().getTags().size() / maxPrefixesPerPage + 1;
    }

    private void buy(Profile profile, Tag tag) {
        Player player = Bukkit.getPlayer(profile.getId());

        profile.removeCoins(tag.getPrice());
        profile.getTags().add(tag);

        player.sendMessage(Color.translate("&aYou successfully bought &6" + tag.getId() + "&a! Feel free to use it ;)"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
    }

    private void togglePrefix(Profile profile, Prefix prefix) {
        profile.setActivePrefix(prefix);
        Bukkit.getPlayer(profile.getId()).sendMessage(Color.translate("&aYou are now using &e" + prefix.getId() + " &aprefix."));
    }

    private void toggleSuffix(Profile profile, Suffix suffix) {
        profile.setActiveSuffix(suffix);
        Bukkit.getPlayer(profile.getId()).sendMessage(Color.translate("&aYou are now using &e" + suffix.getId() + " &asuffix."));
    }

    private void toggle(Profile profile, Tag tag) {
        if(tag instanceof Prefix) togglePrefix(profile, (Prefix) tag);
        else toggleSuffix(profile, (Suffix) tag);

        Player player = Bukkit.getPlayer(profile.getId());
        player.closeInventory();

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
    }

    @Override
    public void update() {
        this.inventory.clear();

        TagsHandler tagsHandler = Basic.getInstance().getTagsHandler();
        List<Tag> list = tagsHandler.getTags();

        for (int i = 0; i <= 8; i++)
            inventory.setItem(i, pane());


        for (int i = 36; i <= 44; i++)
            inventory.setItem(i, pane());


        for (int i : Arrays.asList(17, 26, 35, 9, 18, 27))
            inventory.setItem(i, pane());


        inventory.setItem(0, new ItemBuilder().type(Material.CARPET).data(14).name("&cPrevious Page").build());
        inventory.setItem(8, new ItemBuilder().type(Material.CARPET).data(5).name("&aNext Page").build());

        int itemSlot = 10;
        int index = page * maxPrefixesPerPage - maxPrefixesPerPage;
        while (index < list.size() && itemSlot <= 34) {
            Tag tag = list.get(index);

            ItemBuilder builder = new ItemBuilder().type(Material.NAME_TAG)
                    .name(tag.getDisplay())
                    .lore(Common.getLine(30),
                            "&bYou'll look like this: " + getFormat(tag),
                            " ",
                            "&bThis " + tag.getType() +  " costs &6" + tag.getPrice() + " coins&b.",
                            boughtIt(tag));

            if(!hasCoins2Buy(tag) && !hasTag(tag)) builder.lore("&bYou can buy coins at &6store.skulluhc.club");
            builder.lore(Common.getLine(30));
            inventory.setItem(itemSlot, builder.build());

            if(itemSlot == 16 || itemSlot == 25) itemSlot = itemSlot + 2 ;
            itemSlot++;
            index++;
        }
    }

    private boolean hasTag(Tag tag){
        return profile.getTags().stream().map(Tag::getId)
                .collect(Collectors.toList()).contains(tag.getId());
    }

    private String boughtIt(Tag tag){
        return hasTag(tag) ? "&aYou already have this "  + tag.getType() + ", click it to toggle it."
                : hasCoins(tag);
    }

    private String hasCoins(Tag tag) {
        return profile.getCoins() >= tag.getPrice() ? "&6Click to buy this " + tag.getType() : "&cYou don't have enough coins to buy this prefix.";
    }

    private boolean hasCoins2Buy(Tag tag) {
        return profile.getCoins() >= tag.getPrice();
    }

    private ItemStack pane() {
        return new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(7).name("&7 ").build();
    }

    private String getFormat(Tag tag) {
        if(tag.isSuffix()) {
            return profile.getHighestRank().getPrefix() + profile.getHighestRank().getColors() + (profile.getChatColor() == null ? "" : profile.getChatColor()) + (profile.isItalic() ? ChatColor.ITALIC : "") +
                    (profile.isSpaceBetweenRank() ? " " : "") + profile.getName()
                    + profile.getHighestRank().getSuffix() + " " + tag.getDisplay();
        }

        return tag.getDisplay() + " " + profile.getHighestRank().getPrefix() + profile.getHighestRank().getColors() + (profile.getChatColor() == null ? "" : profile.getChatColor()) + (profile.isItalic() ? ChatColor.ITALIC : "") +
                (profile.isSpaceBetweenRank() ? " " : "") + profile.getName()
                + profile.getHighestRank().getSuffix();
    }

    private boolean isUsingPrefix(Tag tag) {
        return profile.getActivePrefix() != null && profile.getActivePrefix().getId().equals(tag.getId());
    }

    private boolean isUsingSuffix(Tag tag) {
        return profile.getActiveSuffix() != null && profile.getActiveSuffix().getId().equals(tag.getId());
    }


}
