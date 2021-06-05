package invaded.core.menu;

import invaded.core.Spotify;
import invaded.core.manager.DisguiseHandler;
import invaded.core.profile.Profile;
import invaded.core.profile.ProfileHandler;
import invaded.core.rank.Rank;
import invaded.core.util.Color;
import invaded.core.util.Skin;
import invaded.core.util.menu.Menu;
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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;

public class SkinMenu extends Menu {

    private static final ConcurrentMap<String, String> datas = new ConcurrentHashMap<>();

    private String nick;
    private Skin nickSkin;

    public SkinMenu(Player player, String nick, Skin skin) {
        super("&bChoose your skin!", 54);

        datas.put(player.getName(), nick);
        this.nick = nick;
        this.nickSkin = skin;
    }

    @Override
    public void update() {
        int slot = 0;
        List<String> displays = new ArrayList<>(Spotify.getInstance().getDisguiseHandler().getSkinManager().getSkins().keySet());

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();

        DyeColor dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
        meta.setColor(dyeColor.getColor());

        meta.setDisplayName(Color.translate("&bOwn"));
        chestplate.setItemMeta(meta);

        inventory.setItem(slot++, chestplate);

        for(String display : displays) {
            chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            meta = (LeatherArmorMeta) chestplate.getItemMeta();

            dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
            meta.setColor(dyeColor.getColor());

            meta.setDisplayName(Color.translate("&b" + display));
            chestplate.setItemMeta(meta);

            inventory.setItem(slot++, chestplate);
        }

        if(nickSkin == null) return;

        chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        meta = (LeatherArmorMeta) chestplate.getItemMeta();

        dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
        meta.setColor(dyeColor.getColor());

        meta.setDisplayName(Color.translate("&5" + nick));
        chestplate.setItemMeta(meta);

        inventory.setItem(slot, chestplate);
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(event.getCurrentItem() == null
        || event.getCurrentItem().getType() == Material.AIR
        || !event.getCurrentItem().hasItemMeta()
        || !event.getCurrentItem().getItemMeta().hasDisplayName()) return;

        if(!datas.containsKey(player.getName())) {
            player.sendMessage(Color.translate("&cSomething failed."));
            return;
        }

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile =profileHandler.getProfile(player.getUniqueId());

        String nick = datas.get(player.getName());
        String display = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

        Skin skin;

        if(display.equals(nick) && nickSkin != null) skin = nickSkin;
        else if(!display.equals("Own")) skin = Spotify.getInstance().getDisguiseHandler().getSkinManager().getSkinOf(display);
        else skin = profile.getRealSkin();

        if(skin == null){
            skin = profile.getRealSkin();
            Bukkit.getLogger().info("Didn't found a skin for display name " + display+ ", check it out later.");
        }

        Rank rank = profile.getFakeRank();

        if(rank == null) {
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

        String info = profile.getFakeName()+";" + profile.getFakeRank().getName()
                +";" + profile.getFakeSkin().getTexture() +";" + profile.getFakeSkin().getSignature();

        profile.disguise();
        DisguiseHandler.getDisguisedPlayers().put(profile.getId(), info);
    }
}
