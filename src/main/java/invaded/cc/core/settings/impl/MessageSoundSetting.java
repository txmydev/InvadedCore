package invaded.cc.core.settings.impl;

import invaded.cc.core.settings.SettingsOption;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ItemBuilder;
import org.bukkit.Material;

public class MessageSoundSetting extends SettingsOption {

    public MessageSoundSetting() {
        super("PM Sound", profile -> new ItemBuilder()
                .type(Material.JUKEBOX)
                .name((profile.isMessagesSound() ? "&a" : "&c") + "Toggle your PM Sound")
                .lore(   Common.getLine(40),
                        "&7Are you annoyed by the sound you hear when you get a",
                        "&7pm, well then toggle the sound by clicking this item.",
                        Common.getLine(40)).build(), profile -> Common.getPlayer(profile).performCommand("pmsound"));
    }

}
