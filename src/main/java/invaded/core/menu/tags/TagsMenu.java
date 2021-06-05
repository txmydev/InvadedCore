package invaded.core.menu.tags;

import invaded.core.Spotify;
import invaded.core.menu.CosmeticsMenu;
import invaded.core.tags.Tag;
import invaded.core.tags.TagsHandler;
import invaded.core.profile.Profile;
import invaded.core.util.Color;
import invaded.core.util.Common;
import invaded.core.util.ItemBuilder;
import invaded.core.util.Task;
import invaded.core.util.menu.Menu;
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
        final Profile profile = Spotify.getInstance().getProfileHandler().getProfile(player.getUniqueId());

        if(event.getSlot() == 1) {
            if(page == 1) return;
            page--;
            this.update();

            return;
        }

        if(event.getSlot() == 7){
            if(page == getMaxPages()) return;
            page++;
            this.update();
            return;
        }

        if(event.getSlot() == 44){
            player.closeInventory();
            Task.later(() -> new CosmeticsMenu(profile).open(player), 2L);
            return;
        }

        if(event.getSlot() == 39) {
            if(profile.getActivePrefix() != null) {
                profile.setActivePrefix(null);
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 0.6f);
                player.sendMessage(Color.translate("&cYou are no longer using a prefix."));
            }
            return;
        }

        if(event.getSlot() == 41) {
            if(profile.getActiveSuffix() != null) {
                profile.setActiveSuffix(null);
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 0.6f);
                player.sendMessage(Color.translate("&cYou are no longer using a suffix."));
            }
        }

        if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR || !event.getCurrentItem().hasItemMeta()) return;
        ItemMeta meta = event.getCurrentItem().getItemMeta();
        String strippedItemName = Color.translate(meta.getDisplayName());
        Tag tag = check(strippedItemName);
        if(tag == null) return;

        if(hasTag(tag)) {
            toggle(profile, tag, true);
            return;
        }

        if(hasCoins2Buy(tag)) {
            this.buy(profile, tag);
            this.update();
            return;
        }
    }

    private Tag check(String itemName){
        for (Tag tag : Spotify.getInstance().getTagsHandler().getTags()) {
            String displayStripped = Color.translate(tag.getDisplay());

            if (!displayStripped.equalsIgnoreCase(itemName))
                continue;


            return tag;
        }

        return null;
    }

    private int getMaxPages() {
        return Spotify.getInstance().getTagsHandler().getTags().size() / maxPrefixesPerPage + 1;
    }

    private void buy(Profile profile, Tag tag) {
        Player player = Bukkit.getPlayer(profile.getId());

        profile.removeCoins(tag.getPrice());
        profile.getTags().add(tag);
        toggle(profile, tag, false);

        player.sendMessage(Color.translate("&aYou successfully bought &6" + tag.getId() + "&a! Try to type anything ;)"));
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0f, 1.0f);
    }

    private void togglePrefix(Profile profile, Tag prefix) {
        this.togglePrefix(profile, prefix, true);
    }

    private void toggleSuffix(Profile profile, Tag suffix) {
        this.toggleSuffix(profile, suffix, true);
    }


    private void togglePrefix(Profile profile, Tag prefix, boolean msg) {
        profile.setActivePrefix(prefix);
        if(msg) Bukkit.getPlayer(profile.getId()).sendMessage(Color.translate("&aYou are now using &e" + prefix.getId() + " &aprefix."));
    }

    private void toggleSuffix(Profile profile, Tag suffix, boolean msg) {
        profile.setActiveSuffix(suffix);
        if(msg) Bukkit.getPlayer(profile.getId()).sendMessage(Color.translate("&aYou are now using &e" + suffix.getId() + " &asuffix."));
    }

    private void toggle(Profile profile, Tag tag, boolean msg) {
        if(!tag.isSuffix()) togglePrefix(profile, tag ,msg);
        else toggleSuffix(profile, tag, msg);

        Player player = Bukkit.getPlayer(profile.getId());
        player.closeInventory();

        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0f, 1.0f);
    }

    @Override
    public void update() {
        this.inventory.clear();

        TagsHandler tagsHandler = Spotify.getInstance().getTagsHandler();
        List<Tag> list = tagsHandler.getTags();

        for (int i = 0; i <= 8; i++)
            inventory.setItem(i, pane());


        for (int i = 36; i <= 44; i++)
            inventory.setItem(i, pane());


        for (int i : Arrays.asList(17, 26, 35, 9, 18, 27))
            inventory.setItem(i, pane());

        inventory.setItem(1, new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(14).name("&cPrevious Page").build());
        inventory.setItem(7, new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(5).name("&aNext Page").build());
        inventory.setItem(44, new ItemBuilder().type(Material.ENDER_CHEST).name("&7Back to &dCosmetics").build());
        inventory.setItem(40, new ItemBuilder().type(Material.GOLD_NUGGET).name("&bYour Coins&7: &6" + profile.getCoins() + " ©").build());
        inventory.setItem(39, new ItemBuilder().type(Material.REDSTONE_BLOCK).data(14).name("&c&nDisable prefix").build());
        inventory.setItem(41, new ItemBuilder().type(Material.REDSTONE_BLOCK).data(14).name("&c&nDisable suffix").build());

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
