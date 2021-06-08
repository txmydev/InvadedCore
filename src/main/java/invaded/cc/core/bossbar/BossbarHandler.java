package invaded.cc.core.bossbar;

import com.lunarclient.bukkitapi.LunarClientAPI;
import com.lunarclient.bukkitapi.nethandler.client.LCPacketBossBar;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftEnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;

@Getter @Setter
public class BossbarHandler {

    private BossbarAdapter adapter;

    public BossbarHandler() {
        adapter = new TestBossbarAdapter();
    }

    public void display(Player player) {
        if(adapter.getIgnoredPlayers().contains(player)) return;

        String title = adapter.getTitle();
        double health = adapter.getHealth();
        Location location = player.getLocation();

       /* if(LunarClientAPI.getInstance().isRunningLunarClient(player)) {
            LCPacketBossBar bossBar = new LCPacketBossBar(0, title, (float) health);
            LunarClientAPI.getInstance().sendPacket(player, bossBar);
      //  } else {
            DataWatcher dataWatcher = new DataWatcher((Entity) null);
            dataWatcher.a(4, "LA CONCHA DETU MADRE");
            //dataWatcher.a(10, "LA CONCHA DETU MADRE");
            dataWatcher.a(3, (float) health);
            dataWatcher.a(0, (byte) 0x20);


            PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving();
            spawn.a = 1393;
            spawn.b = EntityType.ENDER_DRAGON.getTypeId();
            spawn.c = (int) Math.floor(location.getBlockX() * 32.0D);
            spawn.d = (int) Math.floor(-500 * 32.0D);
            spawn.e = (int) Math.floor(location.getBlockZ() * 32.0D);
            spawn.i = (byte) 0;
            spawn.j = (byte) 0;
            spawn.k = (byte) 0;
            spawn.l = dataWatcher;*/

        //Common.sendPacket(player, spawn);

       // }
    }

    public void remove(Player player) {
        DataWatcher dataWatcher = new DataWatcher((Entity) null);
        dataWatcher.a(4, " ");
        dataWatcher.a(0, 0x20);
        dataWatcher.a(3, (float) 200);

        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(1393, dataWatcher.c(), false);
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(1393);

        Common.sendPacket(player, metadata);
        Common.sendPacket(player, destroy);
    }

}
