package invaded.cc.core.profile.settings;

import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ItemBuilder;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

@RequiredArgsConstructor @Getter
public enum Settings {
    BOSSBAR("bossBar",
                                         "Boss Bar", profile -> new ItemBuilder()
                                    .type(Material.ENDER_PEARL)
                                    .name((profile.isBossBar() ? "&a" : "&c") + "Boss Bar")
            .lore(Common.getLine(40),
                           "&7When enabled, it will display a bossbar corresponding",
                           "&7to the current gamemode, this may cause FPS problems",
                           "&7so feel free to disable it if you're experiencing some." ,
                    Common.getLine(40)).build(), profile -> {
        boolean v = !profile.isBossBar();

        profile.setBossBar(v);
        profile.sendMessage((v ? "&a" : "&c") + "You've toggled your bossbar.");
    })
    ,
    LUNAR_BORDER("lunarBorder", "Lunar Border",
                 profile -> new ItemBuilder()
                                    .type(Material.NETHER_STAR)
                                    .name("&7Lunar Border")
                                    .lore(  Common.getLine(40),
                                                    "&7When enabled, this setting allows you to display",
                                                    "&7the 1.8 border among the current border of the world",
                                                    " ",
                                                    "&7Coming soon!",
                                            Common.getLine(40))
                                    .build(), profile -> profile.sendMessage(CC.GRAY + "This feature is under development.")),

    PRIVATE_MESSAGE("privateMessages",
            "Private Messages",
            profile -> new ItemBuilder()
                    .type(Material.BOOK_AND_QUILL)
                    .name((profile.isMessages() ? "&a" : "&c") + "Toggle your PM's")
                    .lore(Common.getLine(40),
                            "&7Other user's will be able to send private messages",
                            "&7to you, but if you disable it you won't be able",
                            "&7to message anyone.",
                            Common.getLine(40))
                    .build(), profile -> Common.getPlayer(profile).performCommand("pm")),

    PM_SOUND("privateMessageSound",
            "PM Sound", profile -> new ItemBuilder()
            .type(Material.JUKEBOX)
            .name((profile.isMessagesSound() ? "&a" : "&c") + "Toggle your PM Sound")
            .lore(   Common.getLine(40),
                    "&7Are you annoyed by the sound you hear when you get a",
                    "&7pm, well then toggle the sound by clicking this item.",
                    Common.getLine(40)).build(), profile -> Common.getPlayer(profile).performCommand("pmsound")),

    FLY("fly", "Fly",
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
            " ").build(), profile -> Common.getPlayer(profile).performCommand("fly")),

    SIDEBAR("sidebar", "Sidebar",
            profile -> new ItemBuilder()
                            .type(Material.BLAZE_POWDER)
                            .name("&7Toggle Scoreboard")
                            .lore(Common.getLine(40),
                                    "&7Trying to free up some space on the screen",
                                    "&7you can disable the scoreboard by clicking",
                                    "&7this item.",
                                    Common.getLine(40)).build(), profile -> profile.sendMessage(CC.GRAY + "Not available now."))
    ,
    TIME("time", "Time Item", profile -> new ItemBuilder()
                                                            .type(Material.WATCH)
                                                            .name("&dChange Time &e(To " + profile.getTimeCycle().getNext().name() + ")")
                                                            .lore(Common.getLine(40),
                                                                    "&7You can set your minecraft time to day, ",
                                                                            "night and evening. With this item, every click you will",
                                                                    "be set into the next time stamp."
                                                            ,Common.getLine(40)).build(), profile -> {
        DayCycle to = profile.getTimeCycle().getNext();
        Player player = Common.getPlayer(profile);
        switch(to) {
            case DAY:
                player.setPlayerTime(0, false);
                break;
            case EVENING:
                player.setPlayerTime(11000, false);
                break;
            case NIGHT:
                player.setPlayerTime(13000, false);
                break;
        }

        profile.setTimeCycle(to);
    });
    ;



    private final String name, niceName;
    private final Function<Profile, ItemStack> stack;
    private final Consumer<Profile> click;




}