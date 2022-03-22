package invaded.cc.core.settings.impl;

import invaded.cc.core.Spotify;
import invaded.cc.core.settings.SettingsOption;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ItemBuilder;
import org.bukkit.Material;

public class BossbarSetting extends SettingsOption {

    public BossbarSetting() {
        super("Boss Bar", profile -> new ItemBuilder()
                .type(Material.ENDER_PEARL)
                .name((profile.isBossBar() ? "&a" : "&c") + "Boss Bar")
                .lore(Common.getLine(40),
                        "&7When enabled, it will display a bossbar corresponding",
                        "&7to the current gamemode, this may cause FPS problems",
                        "&7so feel free to disable it if you're experiencing some.",
                        Common.getLine(40)).build(), profile -> {

            profile.setBossBar(!profile.isBossBar());
            profile.sendMessage((profile.isBossBar() ? "&a" : "&c") + "You've toggled your bossbar.");

            if (!profile.isBossBar()) Spotify.getInstance().getBossbarHandler().remove(Common.getPlayer(profile));
        });
    }
}
