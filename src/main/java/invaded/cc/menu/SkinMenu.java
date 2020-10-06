package invaded.cc.menu;

import invaded.cc.Core;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.rank.Rank;
import invaded.cc.util.Color;
import invaded.cc.util.Skin;
import invaded.cc.util.menu.Menu;
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

    public SkinMenu(Player player, String nick) {
        super("&bChoose your skin!", 54);

        datas.put(player.getName(), nick);
    }

    @Override
    public void update() {
        int slot = 0;
        List<String> displays = new ArrayList<>(Core.getInstance().getDisguiseHandler().getSkinManager().getSkins().keySet());

        ItemStack chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta meta = (LeatherArmorMeta) chestplate.getItemMeta();

        DyeColor dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
        meta.setColor(dyeColor.getColor());

        meta.setDisplayName("&bOwn");
        chestplate.setItemMeta(meta);

        inventory.setItem(slot++, chestplate);

        for(String display : displays) {
            chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
            meta = (LeatherArmorMeta) chestplate.getItemMeta();

            dyeColor = DyeColor.values()[ThreadLocalRandom.current().nextInt(DyeColor.values().length - 1)];
            meta.setColor(dyeColor.getColor());

            meta.setDisplayName("&b" + display);
            chestplate.setItemMeta(meta);

            inventory.setItem(slot++, chestplate);
        }
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

        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Profile profile =profileHandler.getProfile(player.getUniqueId());

        String nick = datas.get(player.getName());
        String display = ChatColor.stripColor(event.getCurrentItem().getItemMeta().getDisplayName());

        Skin skin = null;

        if(!display.equals("Own")) skin = Core.getInstance().getDisguiseHandler().getSkinManager().getSkinOf(display);
        else skin = profile.getRealSkin();

        if(skin == null){
            skin = profile.getRealSkin();
            Bukkit.getLogger().info("Didn't found skin for display name " + display+ ", check it out later.");
        }

        Rank rank = profile.getFakeRank();

        if(rank == null) {
            player.closeInventory();
            player.sendMessage(Color.translate("&cFake rank not found, did you select it?"));
            return;
        }

        new JedisPoster(JedisAction.DISGUISE)
                .addInfo("profileId", profile.getId().toString())
                .addInfo("realName",profile.getName())
                .addInfo("name", nick)
                .addInfo("rank", rank.getName())
                .addInfo("skin", skin.getTexture() + ";"+ skin.getSignature())
                .post();

        player.sendMessage(Color.translate("&aYou've been disguised!"));

        datas.remove(player.getName());
        player.closeInventory();
    }
}
