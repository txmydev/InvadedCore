package invaded.cc.menu.prefix;

import invaded.cc.Core;
import invaded.cc.commands.prefix.GrantPrefixCommand;
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
import java.util.stream.Collectors;

public class GrantPrefixMenu extends Menu {

    private Profile profile;
    private Map<Integer, Prefix> slots;

    public GrantPrefixMenu(Profile profile) {
        super("&aGrant a prefix to " + profile.getColoredName(), 54);

        this.profile = profile;
        this.slots = new HashMap<>();
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player)event.getWhoClicked();

        if(slots.containsKey(event.getSlot())) {
            Prefix prefix = slots.get(event.getSlot());
            List<String> userPrefixNames = profile.getPrefixes().stream().map(prefix1 -> prefix.getId()).collect(Collectors.toList());

            if(userPrefixNames.contains(prefix.getId())) {
                player.sendMessage(Color.translate(profile.getColoredName() + " &calready has that prefix."));
                return;
            }


            profile.getPrefixes().add(prefix);
            player.sendMessage(Color.translate("&aYou've granted &6" + prefix.getId() + "&a's prefix to " + profile.getColoredName() + "&a."));
        }
    }

    @Override
    public void update() {
        List<Prefix> prefixList = Core.getInstance().getPrefixHandler().getPrefixes();

        int slot = 0;
        for(Prefix prefix : prefixList) {
            inventory.setItem(slot, new ItemBuilder()
                    .type(Material.NAME_TAG)
                    .name("&6" + prefix.getId())
                    .lore(Common.getLine(35),
                           profile.getColoredName() + " &bwill look like this:",
                            getFormat(prefix),
                            " ",
                            hasPrefix(prefix) ? profile.getRealColoredName() +
                                    " &aalready has that prefix." : "&6You can grant this prefix to "
                                    + profile.getRealColoredName(),
                            Common.getLine(35)).build());

            slots.put(slot, prefix);
            slot++;
        }
    }

    private boolean hasPrefix(Prefix prefix) {
        for (Prefix p : profile.getPrefixes())
            if(prefix.getId().equals(p.getId())) return true;
        return false;
    }

    private String getFormat(Prefix prefix){
        return prefix.getDisplay() + " " + profile.getHighestRank().getPrefix() + profile.getHighestRank().getColors() + (profile.getChatColor() == null ? "" : profile.getChatColor()) + (profile.isItalic() ? ChatColor.ITALIC : "") +
                (profile.isSpaceBetweenRank() ? " " : "") +profile.getName()
                + profile.getHighestRank().getSuffix();
    }
}
