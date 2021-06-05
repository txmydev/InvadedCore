package invaded.cc.core.menu;

import invaded.cc.core.Spotify;
import invaded.cc.core.manager.DisguiseHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.Skin;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.menu.Menu;
import lombok.SneakyThrows;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DisguiseRankMenu extends Menu {

    private final ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

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
        List<Rank> ranks = DisguiseHandler.getAvailableDisguiseRanks(profile);
        ranks.sort((rank1, rank2) -> rank2.getPriority() - rank1.getPriority());

        int slot = 0;

        for (Rank rank : ranks) {
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
    }


    @SneakyThrows
    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (!values.containsKey(event.getSlot())) return;

        Rank rank = values.get(event.getSlot());

        player.sendMessage(Color.translate("&aAlright, you selected your rank, now please choose a skin!"));
        player.closeInventory();

        profile.setFakeName(nick);
        profile.setFakeRank(rank);

        Skin skin = profile.getRealSkin();

        if (Spotify.getInstance().getDisguiseHandler().getSkinManager().getSkins().size() == 0) {
            profile.setFakeSkin(skin);
            disguise();

            player.sendMessage(Color.translate("&cYou are using your own skin due to there are no skins available."));
            player.sendMessage(Color.translate("&aYou have been disguised!"));
            return;
        }

        Task.asyncLater(() -> {
            new SkinMenu(player, nick, Spotify.getInstance().getDisguiseHandler().getSkinManager().fetchSkin(nick)).open(player);
        }, 2L);
    }

    public void disguise() {
        GameProfile gameProfile = new GameProfile(profile.getId(), profile.getFakeName());
        gameProfile.getProperties().put("textures", new Property("textures", profile.getFakeSkin().getTexture(), profile.getFakeSkin().getSignature()));

        profile.setFakeProfile(gameProfile);

        String info = profile.getFakeName() + ";" + profile.getFakeRank().getName()
                + ";" + profile.getFakeSkin().getTexture() + ";" + profile.getFakeSkin().getSignature();

        profile.disguise();
        DisguiseHandler.getDisguisedPlayers().put(profile.getId(), info);
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
