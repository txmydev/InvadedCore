package invaded.cc.core.tasks;

import invaded.cc.core.trails.Trail;
import invaded.cc.core.util.Common;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Effect;
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

        while (iterator.hasNext()) {
            Map.Entry<Entity, Trail> entry = iterator.next();

            Entity e = entry.getKey();
            Trail val = entry.getValue();

            if (e == null || e.isOnGround() || e.isDead()) {
                iterator.remove();
                return;
            }

            display(e.getLocation(), val);
        }
    }

    private void display(Location location, Trail trail) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(trail.getParticle(), true,
                (float) location.getX(),
                (float) location.getY(),
                (float) location.getZ(),
                0.1f,
                0.1f,
                0.1f,
                1.0f,
                1);

        Common.getOnlinePlayers().forEach(player -> Common.sendPacket(player, packet));
    }
}
