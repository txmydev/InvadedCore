package invaded.cc.core.settings.impl;

import invaded.cc.core.settings.SettingsOption;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ItemBuilder;
import org.bukkit.Material;

public class ScoreboardTypeOption extends SettingsOption {
    public ScoreboardTypeOption() {
        super("Scoreboard Type", profile ->
                        new ItemBuilder().type(Material.PAINTING).name(CC.GREEN + "Customize your Scoreboard!")
                                .lore(CC.GRAY + "This item allows you to change your scoreboard style",
                                        CC.GRAY + "to " + CC.YELLOW + "Badlion, Ultra, Lunar and Skull").build()
                , profile -> Common.getPlayer(profile).performCommand("togglescoreboardtype"));
    }
}
