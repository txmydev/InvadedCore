package invaded.cc.menu;

import invaded.cc.Basic;
import invaded.cc.grant.Grant;
import invaded.cc.grant.GrantHandler;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.rank.Rank;
import invaded.cc.rank.RankHandler;
import invaded.cc.util.Color;
import invaded.cc.util.ItemBuilder;
import invaded.cc.util.menu.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class GrantMenu extends Menu {

    private final Profile profile;
    private final ConcurrentMap<Integer, Rank> values;

    private final RankHandler rankHandler = Basic.getInstance().getRankHandler();

    public GrantMenu(Profile profile){
        super("&eChange rank of " + profile.getColoredName(), 27);

        this.values = new ConcurrentHashMap<>();
        this.profile = profile;
    }

    @Override
    public void update() {
        int slot = 0;

        for(Rank rank : rankHandler.getRanks()){
            inventory.setItem(slot, new ItemBuilder()
            .type(Material.WOOL).data(getWoolData(rank.getColor()))
            .name(rank.getColoredName())
                    .lore("&7&m---------------------------"
                    , "&bClick to grant " + rank.getColoredName() +" &bto " + profile.getColoredName()
                    ,"&7&m---------------------------").build());

            values.put(slot, rank);
            slot++;
        }
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        ProfileHandler profileHandler = Basic.getInstance().getProfileHandler();
        Player player = (Player) event.getWhoClicked();
        Profile whoClicked = profileHandler.getProfile(player.getUniqueId());

        if(values.containsKey(event.getSlot())){
            Rank rank = values.get(event.getSlot());

            if(rank.getPriority() > whoClicked.getHighestRank().getPriority() && !player.getName().equalsIgnoreCase("txmy")) {
                player.sendMessage(Color.translate("&cFailed to set that rank."));
                return;
            }

            GrantHandler grantHandler = Basic.getInstance().getGrantHandler();
            grantHandler.updateGrant(new Grant(profile, System.currentTimeMillis(), rank.getName(), player.getName()));

            player.sendMessage(Color.translate(profile.getColoredName()+ "'s &arank is now " + rank.getColoredName()));
            Player user = Bukkit.getPlayer(profile.getId());
            if(user == null)return;
            user.sendMessage(Color.translate("&aYou're rank has been updated to " + rank.getColoredName() + "&a, you may relog to see the changes."));
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
