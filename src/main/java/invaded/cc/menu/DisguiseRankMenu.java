package invaded.cc.menu;

import invaded.cc.Core;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.profile.Profile;
import invaded.cc.manager.DisguiseHandler;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.rank.Rank;
import invaded.cc.util.*;
import invaded.cc.util.menu.Menu;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.*;

public class DisguiseRankMenu extends Menu {

    private final ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

    private Profile profile;
    private Map<Integer, Rank> values;
    private String nick;

    public DisguiseRankMenu(Player player, String disguiseNick) {
        super("&eChoose Disguise Rank", 54);

        values = new HashMap<>();
        profile = profileHandler.getProfile(player.getUniqueId());
        this.nick = disguiseNick;
    }

    @Override
    public void update() {
        List<Rank> ranks = DisguiseHandler.getAvailableDisguiseRanks(Bukkit.getPlayer(profile.getId()));

        Collections.reverse(ranks);

        int slot = 0;

        List<Integer> toFill = new ArrayList<>();

        for (Rank rank : ranks) {
            if (slot % 2 != 0) {
                toFill.add(slot);
                slot++;
                continue;
            }

            ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
            LeatherArmorMeta meta = (LeatherArmorMeta) helmet.getItemMeta();

            DyeColor dyeColor = DyeColor.getByWoolData((byte) getWoolData(rank.getColor()));
            meta.setColor(dyeColor.getColor());

            meta.setDisplayName(rank.getColoredName());

            meta.setLore(Common.formatList(Arrays.asList("&7&m--------------------------"
                    , "&fYour rank will be " + rank.getColoredName(),
                    "&7&m--------------------------")));

            helmet.setItemMeta(meta);

            inventory.setItem(slot, helmet);
            values.put(slot, rank);
            slot++;
        }

        toFill.forEach(val -> inventory.setItem(val, new ItemBuilder().type(Material.STAINED_GLASS_PANE)
                .data(7).name("&7").build()));
    }


    @SneakyThrows
    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!values.containsKey(event.getSlot())) return;

        Rank rank = values.get(event.getSlot());

        player.sendMessage(Color.translate("&aAlright, you selected your rank, now please choose a skin!"));
        player.closeInventory();

        profile.setFakeRank(rank);

        Skin skin = profile.getRealSkin();

        if(Core.getInstance().getDisguiseHandler().getSkinManager().getSkins().size() == 0) {

            new JedisPoster(JedisAction.DISGUISE)
                    .addInfo("profileId", profile.getId().toString())
                    .addInfo("realName",profile.getName())
                    .addInfo("name", nick)
                    .addInfo("rank", rank.getName())
                    .addInfo("skin", skin.getTexture() + ";"+ skin.getSignature())
                    .post();

            player.sendMessage(Color.translate("&cYou are using your own skin due to there are no skins available."));
            player.sendMessage(Color.translate("&aYou have been disguised!"));
            return;
        }

        Task.later(() -> new SkinMenu(player, nick).open(player), 2L);

      /*  if (!datas.containsKey(player.getName())) {
            throw new IllegalAccessException("Couldn't find disguise nick of player " + player.getName());
        }

        String nick = datas.get(player.getName());

        if (!values.containsKey(event.getSlot())) return;

        Rank rank = values.get(event.getSlot());

        Skin skin = Profile.getByUuid(player.getUniqueId()).getRealSkin();
        if(skin == null) skin = Skin.STEVE_SKIN;

        DisguiseData disguiseData = new DisguiseData(player.getUniqueId(), nick, skin, rank);
        Core.getInstance().getDb().getRedisManager().sendDisguise(disguiseData);

        datas.remove(player.getName());
        player.sendMessage(Color.translate("&aYou've been disguised as &f" + nick));*/
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
