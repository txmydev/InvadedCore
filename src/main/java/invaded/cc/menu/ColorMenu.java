package invaded.cc.menu;

import invaded.cc.profile.Profile;
import invaded.cc.util.Color;
import invaded.cc.util.ItemBuilder;
import invaded.cc.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ColorMenu extends Menu{

    private final ConcurrentMap<Integer, ChatColor> colors;

    private Profile profile;

    public ColorMenu(Profile profile){
        super("Choose your color.", 27);

        this.profile = profile;
        this.colors = new ConcurrentHashMap<>();
    }

    @Override
    public void update() {
        int slot = 0;

        for(ChatColor color : ChatColor.values()){
            if (color == ChatColor.ITALIC || color == ChatColor.MAGIC
                    || color == ChatColor.BOLD || color == ChatColor.UNDERLINE
                    || color == ChatColor.STRIKETHROUGH || color == ChatColor.RESET
            ) continue;

            inventory.setItem(slot, new ItemBuilder().type(Material.WOOL).data(getWoolData(color)
            ).name(color + color.name()).build());

            colors.put(slot, color);
            slot++;
        }

        inventory.setItem(slot, new ItemBuilder().type(Material.GLOWSTONE_DUST)
                .name("&eToggle your italic mode.")
                .lore("&7You will look like this"
                        , " ",
                        profile.getHighestRank().getColors() +
                                (profile.hasCustomColor() ? profile.getChatColor() + "" : "") +
                                ChatColor.ITALIC + profile.getName()).build());
        colors.put(slot, ChatColor.ITALIC);

        slot++;

        inventory.setItem(slot, new ItemBuilder().type(Material.SUGAR)
                .name("&eToggle your bold mode.")
                .lore("&7You will look like this"
                        , " ",
                        profile.getHighestRank().getColors() +
                                (profile.hasCustomColor() ? profile.getChatColor() + "" : "") +
                                ChatColor.BOLD + profile.getName()).build());
        colors.put(slot, ChatColor.BOLD);

        slot++;

        inventory.setItem(slot, new ItemBuilder().type(Material.COOKIE)
                .name("&eToggle your space between rank display name.")
                .lore("&7You will look like this"
                        , " ",
                        getNameWithSpaceBetweenRank(profile, !profile.isSpaceBetweenRank())).build());
        colors.put(slot, ChatColor.MAGIC);

        inventory.setItem(26, new ItemBuilder().type(Material.STAINED_GLASS_PANE)
        .data(15).name("&cReset Color").build());
    }

    private String getNameWithSpaceBetweenRank(Profile profile, boolean toggle) {
        return profile.getHighestRank().getPrefix() + profile.getHighestRank().getColors() +
                (profile.getChatColor() == null ? "" : profile.getChatColor()) + (profile.isItalic() ? ChatColor.ITALIC : "") +
                (profile.isBold() ? ChatColor.BOLD : "") + (toggle ? " " : "") +profile.getName()
                + profile.getHighestRank().getSuffix();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        if(!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();

        if(event.getSlot() == 26){
            profile.setChatColor(null);
            player.sendMessage(Color.translate("&aYou updated your color."));
            return;
        }

        if(colors.containsKey(event.getSlot())){
            ChatColor color = colors.get(event.getSlot());

            switch(color){
                case ITALIC:
                    profile.setItalic(!profile.isItalic());
                    break;
                case BOLD:
                    profile.setBold(!profile.isBold());
                    break;
                case MAGIC:
                    profile.setSpaceBetweenRank(!profile.isSpaceBetweenRank());
                    break;
                default:
                    profile.setChatColor(color);
                    break;
            }

            player.sendMessage(Color.translate("&aYou updated your color."));
        }
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
