package invaded.core.tasks;

import invaded.core.trails.Trail;
import invaded.core.util.Common;
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;
import java.util.Map;

public class CosmeticsTask extends BukkitRunnable {

    public CosmeticsTask() {
    }

    @Override
    public void run() {
        Iterator<Map.Entry<Entity, Trail>> iterator = Trail.getToDisplay().entrySet().iterator();

        while(iterator.hasNext()) {
            Map.Entry<Entity, Trail> entry = iterator.next();

            Entity e = entry.getKey();
            Trail val = entry.getValue();

            if(e == null || e.isOnGround() || e.isDead()) {
                iterator.remove();
                return;
            }

            display(e.getLocation(), val);
        }
    }

    private void display(Location location, Trail trail) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(trail.getId(),
                (float) location.getX(),
                (float) location.getY(),
                (float) location.getZ(),
                0.1f,
                0.1f,
                0.1f,
                0,
                1);

        Common.getOnlinePlayers().forEach(player -> Common.sendPacket(player, packet));
    }
}
