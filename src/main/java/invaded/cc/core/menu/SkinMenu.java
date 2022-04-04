package invaded.cc.core.menu;

import invaded.cc.core.Spotify;
import invaded.cc.core.manager.DisguiseHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.tasks.SkinFetcherTask;
import invaded.cc.core.util.*;
import invaded.cc.core.util.menu.Menu;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

public class SkinMenu extends Menu {

    private static final ConcurrentMap<String, String> datas = new ConcurrentHashMap<>();

    private Player player;
    private String nick;
    private Skin nickSkin;

    public SkinMenu(Player player, String nick, Skin skin) {
        super("&bChoose your skin!", 54);

        this.player = player;
        datas.put(player.getName(), nick);
        this.nick = nick;
        this.nickSkin = skin;
    }

    @Override
    public void update() {
        if(Common.getVersion(player) > 5) updateNormal();
        else updateLegacy();
    }

    private void updateLegacy() {
        int slot = 0;
        List<String> displays = new ArrayList<>(Spotify.getInstance().getDisguiseHandler().getSkinManager().getSkins().keySet());

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();

        DyeColor dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
        meta.setColor(dyeColor.getColor());

        meta.setDisplayName(Color.translate("&bOwn"));
        chestplate.setItemMeta(meta);

        inventory.setItem(slot++, chestplate);

        for (String display : displays) {
            chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            meta = (LeatherArmorMeta) chestplate.getItemMeta();

            dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
            meta.setColor(dyeColor.getColor());

            meta.setDisplayName(Color.translate("&b" + display));
            chestplate.setItemMeta(meta);

            inventory.setItem(slot++, chestplate);
        }

        if (SkinFetcherTask.hasRequestPending(player)) {
            SkinFetch fetch = SkinFetcherTask.getPendingFetch(player);
            if(fetch != null) {
                ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
                meta = (LeatherArmorMeta) chestplate.getItemMeta();

                dyeColor = DyeColor.YELLOW;
                meta.setColor(dyeColor.getColor());

                meta.setDisplayName(Color.translate("&b" + fetch.getTarget()));

                if(fetch.getSkin() != null && fetch.getSkin().getLoreHead() != null) {
                    List<String> list = new ArrayList<>(fetch.getSkin().getLoreHead());
                    list.add(" ");
                    list.add(CC.YELLOW + "Click to disguise as " + CC.WHITE + nick);

                    meta.setLore(list);
                }

                leggings.setItemMeta(meta);

                inventory.setItem(slot++, leggings);
            }
        }

        if (nickSkin == null) return;

        chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        meta = (LeatherArmorMeta) chestplate.getItemMeta();

        dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
        meta.setColor(dyeColor.getColor());

        meta.setDisplayName(Color.translate("&5" + nick));
        chestplate.setItemMeta(meta);

        inventory.setItem(slot, chestplate);
    }

    private void updateNormal() {
        int slot = 0;
        List<String> displays = new ArrayList<>(Spotify.getInstance().getDisguiseHandler().getSkinManager().getSkins().keySet());

        ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        SkullMeta meta = (SkullMeta) skull.getItemMeta();

        /*DyeColor dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
        meta.setColor(dyeColor.getColor());*/

        meta.setDisplayName(Color.translate("&bOwn"));
        meta.setOwner(this.player.getName());
        skull.setItemMeta(meta);

        inventory.setItem(slot++, skull);

        for (String display : displays) {
            skull = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
            meta = (SkullMeta) skull.getItemMeta();

            meta.setOwner(ChatColor.stripColor(display));
            /*dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
            meta.setColor(dyeColor.getColor());*/

            meta.setDisplayName(Color.translate("&b" + display));
            meta.setOwner(display);

            skull.setItemMeta(meta);

            inventory.setItem(slot++, skull);
        }

        if (SkinFetcherTask.hasRequestPending(player)) {
            SkinFetch fetch = SkinFetcherTask.getPendingFetch(player);
            if(fetch != null) {
                ItemStack fetchedItem = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                meta = (SkullMeta) skull.getItemMeta();

                /*dyeColor = DyeColor.YELLOW;
                meta.setColor(dyeColor.getColor());*/

                meta.setOwner(fetch.getTarget());
                meta.setDisplayName(Color.translate("&b" + fetch.getTarget()));
                fetchedItem.setItemMeta(meta);

                inventory.setItem(slot++, fetchedItem);
            }
        }

        player.updateInventory();
/*
        if (nickSkin == null) return;

        skull = new ItemStack(Material.LEATHER_CHESTPLATE);
        meta = (LeatherArmorMeta) skull.getItemMeta();

        dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
        meta.setColor(dyeColor.getColor());

        meta.setDisplayName(Color.translate("&5" + nick));
        skull.setItemMeta(meta);*/

        //inventory.setItem(slot, skull);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getCurrentItem() == null
                || event.getCurrentItem().getType() == Material.AIR
                || !event.getCurrentItem().hasItemMeta()
                || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        if (!datas.containsKey(player.getName())) {
            player.sendMessage(Color.translate("&cSomething failed."));
            return;
        }

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        String nick = datas.get(player.getName());
        String display = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

        Skin skin;

        SkinFetch fetch = SkinFetcherTask.getPendingFetch(player);
        if (fetch != null && display.equals(fetch.getTarget())) skin = fetch.getSkin();

        else if (!display.equals("Own"))
            skin = Spotify.getInstance().getDisguiseHandler().getSkinManager().getSkinOf(display);
        else skin = profile.getRealSkin();

        if (skin == null) {
            skin = profile.getRealSkin();
            Bukkit.getLogger().info("Didn't found a skin for display name " + display + ", check it out later.");
        }

        Rank rank = profile.getFakeRank();

        if(SkinFetcherTask.hasRequestPending(player)) {
            SkinFetcherTask.remove(player);
        }

        if (rank == null) {
            player.closeInventory();
            player.sendMessage(Color.translate("&cFake rank not found, did you select it?"));
            return;
        }

        profile.setFakeName(nick);
        profile.setFakeSkin(skin);
        disguise(profile);

        player.sendMessage(Color.translate("&aYou've been disguised!"));

        datas.remove(player.getName());
        player.closeInventory();
    }

    public void disguise(Profile profile) {
        GameProfile gameProfile = new GameProfile(profile.getId(), profile.getFakeName());
        gameProfile.getProperties().put("textures", new Property("textures", profile.getFakeSkin().getTexture(), profile.getFakeSkin().getSignature()));

        profile.setFakeProfile(gameProfile);

        String info = profile.getFakeName() + ";" + profile.getFakeRank().getName()
                + ";" + profile.getFakeSkin().getTexture() + ";" + profile.getFakeSkin().getSignature();

        profile.disguise();

        Spotify plugin = Spotify.getInstance();
        plugin.getDisguiseHandler().getDisguisedPlayers().put(profile.getId(), info);
    }
}
