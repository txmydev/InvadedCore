package invaded.cc.core.profile.settings;

import invaded.cc.core.event.PlayerChangeSettingEvent;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.ItemBuilder;
import invaded.cc.core.util.menu.Menu;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Arrays;

public class SettingsMenu extends Menu {

    private Profile profile;

    public SettingsMenu(Profile profile) {
        super(CC.PINK + "Settings", 4 * 9);
        this.profile = profile;
    }

    @Override
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(player.getUniqueId() != profile.getId()) return;

        for(Settings setting : Settings.values()) {
            if(setting.getStack().apply(profile).isSimilar(event.getCurrentItem())) {
                setting.getClick().accept(profile);
                new PlayerChangeSettingEvent(player, setting).call();
                this.update();
                break;
            }
        }
    }

    @Override
    public void update() {
        inventory.clear();
        for(int i = 0; i < inventory.getSize(); i++) this.inventory.setItem(i, new ItemBuilder().type(Material.STAINED_GLASS_PANE).data(7).name(" ").build());

        int startIndex = 10, endIndex = 25;

        for (Settings setting : Settings.values()) {
            if(startIndex > endIndex) return;
            if(startIndex == 17 || startIndex == 18) startIndex = 19;

            inventory.setItem(startIndex++, setting.getStack().apply(profile));

        }
    }
}
