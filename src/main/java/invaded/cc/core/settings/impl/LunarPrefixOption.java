package invaded.cc.core.settings.impl;

import invaded.cc.core.Spotify;
import invaded.cc.core.settings.SettingsOption;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ItemBuilder;
import org.bukkit.Material;

public class LunarPrefixOption extends SettingsOption {
    public LunarPrefixOption() {
        super("Lunar Prefix", profile -> new ItemBuilder().type(Material.NAME_TAG)
                .name((profile.isLunarPrefix() ? CC.GREEN : CC.RED)+ "Lunar Prefix").lore(Common.getLine(40),
                        CC.GRAY + "When running lunar client, your name will appear like this:",
                        Spotify.getInstance().getTagsHandler().getLunarPrefix() + profile.getChatFormat(false),
                        Common.getLine(40))
                .loreIf(() -> !Spotify.getInstance().getLunarHandler().isRunningLunarClient(Common.getPlayer(profile)),
                        CC.RED + "You aren't using Lunar Client, you can't use this feature.",
                        Common.getLine(40)).build(), profile -> {
            if(!Spotify.getInstance().getLunarHandler().isRunningLunarClient(Common.getPlayer(profile))) return;

            profile.setLunarPrefix(!profile.isLunarPrefix());
            profile.sendMessage(CC.getByBoolean(profile.isLunarPrefix()) + "You've toggled your lunar prefix.");
        });
    }
}
