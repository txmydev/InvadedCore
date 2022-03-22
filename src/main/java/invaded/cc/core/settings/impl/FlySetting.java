package invaded.cc.core.settings.impl;

import invaded.cc.core.profile.Profile;
import invaded.cc.core.settings.SettingsOption;
import invaded.cc.core.util.BooleanUtils;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ItemBuilder;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import net.minecraft.server.ItemStack;
import org.bukkit.Material;

import java.util.function.Function;

public class FlySetting extends SettingsOption {

    public FlySetting() {
        super("Fly",
                profile -> new ItemBuilder()
                        .type(Material.FEATHER)
                        .name((Common.getPlayer(profile).isFlying() ? "&a" : "&c") + "Toggle your fly mode")
                        .lore(Common.getLine(40),
                                "&7You want to fly in the lobby?",
                                "&7be free and fly around by clicking the item",
                                Common.getLine(40))
                        .loreIf(() -> !Permission.test(profile, PermLevel.VIP), " ",
                                "&cYou currently don't have the rank to fly",
                                "&cyou can buy it at &bstore.skulluhc.club",
                                " ").build(), profile -> Common.getPlayer(profile).performCommand("fly"));
    }
}
