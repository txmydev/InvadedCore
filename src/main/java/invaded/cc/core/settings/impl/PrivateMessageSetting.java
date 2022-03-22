package invaded.cc.core.settings.impl;

import invaded.cc.core.settings.SettingsOption;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ItemBuilder;
import org.bukkit.Material;

public class PrivateMessageSetting extends SettingsOption {
    public PrivateMessageSetting() {
        super("Private Messages",
                profile -> new ItemBuilder()
                        .type(Material.BOOK_AND_QUILL)
                        .name((profile.isMessages() ? "&a" : "&c") + "Toggle your PM's")
                        .lore(Common.getLine(40),
                                "&7Other user's will be able to send private messages",
                                "&7to you, but if you disable it you won't be able",
                                "&7to message anyone.",
                                Common.getLine(40))
                        .build(), profile -> Common.getPlayer(profile).performCommand("pm"));
    }
}
