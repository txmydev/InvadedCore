package invaded.cc.menu;

import invaded.cc.Core;
import invaded.cc.grant.Grant;
import invaded.cc.grant.GrantHandler;
import invaded.cc.profile.Profile;
import invaded.cc.rank.Rank;
import invaded.cc.rank.RankHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.ItemBuilder;
import invaded.cc.util.Task;
import invaded.cc.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GrantsMenu extends Menu {

    private final Profile profile;
    private Map<Integer, Grant> grants;

    public GrantsMenu(Profile profile) {
        super("&bGrants of " + profile.getColoredName(), 54);

        this.profile = profile;
        this.grants = new HashMap<>();
    }

    @Override
    public void update() {
        RankHandler rankHandler = Core.getInstance().getRankHandler();
        List<Grant> list = profile.getGrants().stream().sorted(Grant.WEIGHT_COMPARATOR).collect(Collectors.toList());
        int slot = 0;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd'/'MM hh:mm:ss");

        for(Grant grant : list) {
            Rank rank = rankHandler.getRank(grant.getRank());
            ItemBuilder itemBuilder = new ItemBuilder().type(Material.PAPER);

            itemBuilder.name("&bGrant of " + rank.getColoredName() +" &brank");
            itemBuilder.lore(Common.getLine(20));
            itemBuilder.lore("&fAdded By&7: &b" + grant.getAddedBy());
            itemBuilder.lore("&fAdded At&7: &b" + simpleDateFormat.format(new Date(grant.getAddedAt())));
            itemBuilder.lore("&fDuration&7: &bPermanent");
            itemBuilder.lore(Common.getLine(20));
            itemBuilder.lore("&bClick to remove this grant");

            inventory.setItem(slot++, itemBuilder.build());
            grants.put(slot, grant);
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if(grants.containsKey(event.getSlot())) {
            Grant grant = grants.get(event.getSlot());

            grant.setRemovedBy(player.getName());
            grant.setRemovedAt(System.currentTimeMillis());

            GrantHandler grantHandler = Core.getInstance().getGrantHandler();
            Task.async(() -> grantHandler.updateGrant(grant));

            player.sendMessage(Color.translate("&aRemoved grant of &f" + grant.getProfile().getColoredName()));
        }
    }
}
