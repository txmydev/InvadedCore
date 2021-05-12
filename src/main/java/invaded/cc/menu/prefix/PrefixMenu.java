package invaded.cc.menu.prefix;

import invaded.cc.commands.prefix.PrefixCommand;
import invaded.cc.prefix.Prefix;
import invaded.cc.profile.Profile;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.ItemBuilder;
import invaded.cc.util.menu.Menu;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrefixMenu extends Menu {

    private Profile profile;
    private Map<Integer, Prefix> slots;

    public PrefixMenu(Profile profile){
        super("&aPrefixes", 54);

        this.profile = profile;
        this.slots = new HashMap<>();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(event.getSlot() == 0){
            profile.setActivePrefix(null);
            player.sendMessage(Color.translate("&cYou are no longer using a prefix."));
        }else if(slots.containsKey(event.getSlot())) {
            Prefix prefix = slots.get(event.getSlot());
            if(profile.getActivePrefix() != null && prefix.getId().equals(profile.getActivePrefix().getId())){
                player.sendMessage(Color.translate("&cYou are already using that prefix."));
                return;
            }

            profile.setActivePrefix(prefix);
            player.sendMessage(Color.translate("&aYou are now using prefix &6"+prefix.getId()));
        }
    }

    @Override
    public void update() {
        List<Prefix> list = profile.getPrefixes();

        inventory.setItem(0, new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(14).name("&c&nDisable prefix").build());

        int slot = 1;
        for (Prefix prefix : list) {
            inventory.setItem(slot, new ItemBuilder().type(Material.NAME_TAG).name(prefix.getDisplay())
                    .lore(Common.getLine(35),
                            "&bYou will be looking like this:",
                            getFormat(prefix),
                            " ",
                            isUsing(prefix) ? "&aYou are using these prefix" : "&6Click to use this prefix",
                            Common.getLine(35)).build());
            slots.put(slot, prefix);
            slot++;
        }


    }

    private String getFormat(Prefix prefix){
        return prefix.getDisplay() + " "+ profile.getHighestRank().getPrefix() + profile.getHighestRank().getColors() + (profile.getChatColor() == null ? "" : profile.getChatColor()) + (profile.isItalic() ? ChatColor.ITALIC : "") +
                (profile.isSpaceBetweenRank() ? " " : "") +profile.getName()
                + profile.getHighestRank().getSuffix();
    }

    private boolean isUsing(Prefix prefix){
        return profile.getActivePrefix() != null && profile.getActivePrefix().getId().equals(prefix.getId());
    }


}
