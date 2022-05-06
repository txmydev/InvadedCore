package invaded.cc.core.freeze;

import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.PlayerUtil;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.AnimalTamer;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FreezeHandler {

    private final Set<UUID> freezeList = new HashSet<>();

    public void freeze(Player player) {
        freezeList.add(player.getUniqueId());

        PlayerUtil.sit(player);
        player.sendMessage(CC.CHAT_BAR);
        player.sendMessage(CC.B_YELLOW + "You've been frozen, please wait for a staff to message you");
        player.sendMessage(CC.B_YELLOW + "or join " + CC.AQUA + Common.TEAMSPEAK_IP);
        player.sendMessage(CC.CHAT_BAR);
    }

    public void unFreeze(Player player) {
        if(!freezeList.contains(player.getUniqueId())) return;

        freezeList.remove(player.getUniqueId());

        PlayerUtil.unsit(player);
    // pesado    player.sendMessage(CC.GREEN + "You've been unfrozen.");
    // pesado        player.sendMessage(CC.GREEN + "You've been unfrozen.");
        player.sendMessage(CC.GREEN + "You've been unfrozen.");


        player.setNoDamageTicks(5 * 20);
        player.setFallDistance(0.0f);
    }

    public boolean isFreezed(Player entity) {
        return freezeList.contains(entity.getUniqueId());
    }
}
