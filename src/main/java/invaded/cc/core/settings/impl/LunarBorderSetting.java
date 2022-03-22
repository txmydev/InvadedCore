package invaded.cc.core.settings.impl;

import invaded.cc.core.settings.SettingsOption;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ItemBuilder;
import org.bukkit.Material;

public class LunarBorderSetting extends SettingsOption {
    public LunarBorderSetting() {
        super("Lunar Border",
                profile -> new ItemBuilder()
                        .type(Material.NETHER_STAR)
                        .name((profile.isLunarBorder() ? "&a" : "&c") + "Lunar Border")
                        .lore(  Common.getLine(40),
                                "&7When enabled, this setting allows you to display",
                                "&7the 1.8 border among the current border of the world",
                                Common.getLine(40))
                        .build(), profile -> {
                    profile.setLunarBorder(!profile.isLunarBorder());
                    profile.sendMessage((profile.isLunarBorder() ? "&a" : "&7") + "You've toggled your lunar border.");
                });
    }
}
