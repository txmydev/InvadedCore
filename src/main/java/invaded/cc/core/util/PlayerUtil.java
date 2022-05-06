package invaded.cc.core.util;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.WeakHashMap;

public class PlayerUtil {

    private static Map entityIds = new WeakHashMap();
    private static int currentFakeEntityId = -1;

    public static void sit(Player player) {
        Location location = player.getLocation();
        EntityBat bat = new EntityBat(((CraftWorld) player.getWorld()).getHandle());
        bat.setPosition(location.getX(), location.getY(), location.getZ());
        bat.setInvisible(true);
        bat.setHealth(6.0F);
        entityIds.put(player.getUniqueId(), bat.getId());
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutSpawnEntityLiving(bat));
        entityPlayer.playerConnection.sendPacket(new PacketPlayOutAttachEntity(0, entityPlayer, bat));
    }

    public static EntityPlayer getNMSPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    public static void unsit(Player player) {
        if (entityIds.containsKey(player.getUniqueId())) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutEntityDestroy(new int[]{(Integer) entityIds.get(player.getUniqueId())}));
            entityIds.remove(player.getUniqueId());
        }

    }
}
