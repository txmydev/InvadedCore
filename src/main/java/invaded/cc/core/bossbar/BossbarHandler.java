package invaded.cc.core.bossbar;

import invaded.cc.core.Spotify;
import invaded.cc.core.tasks.BossBarThread;
import invaded.cc.core.util.Common;
import lombok.Getter;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

@Getter
public class BossbarHandler {

    private BossbarAdapter adapter;
    private BossBarThread thread;

    public BossbarHandler() {
        thread = new BossBarThread();

        Bukkit.getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
                Player player = event.getPlayer();
                player.removeMetadata("spawned_bossbar", Spotify.getInstance());
            }

        }, Spotify.getInstance());
    }

    public void setAdapter(BossbarAdapter adapter) {
        Bukkit.getLogger().info("A change in a BossBar adapter has been made.");
        if (adapter == null) {
            Bukkit.getScheduler().runTaskAsynchronously(Spotify.getInstance(), () -> Bukkit.getOnlinePlayers().forEach(this::remove));
        }

        this.adapter = adapter;
        if (!thread.isAlive()) thread.start();
    }

    public void stop() {
        if (thread.isAlive()) thread.stop();
    }

    public void start() {
        thread.start();
    }

    public void display(Player player) {
        if (adapter.getIgnoredPlayers() != null && adapter.getIgnoredPlayers().contains(player)) return;

        String title = adapter.getTitle();
        double health = adapter.getHealth();
        Location location = player.getLocation();

        DataWatcher dataWatcher = new DataWatcher((Entity) null);
        dataWatcher.a(10, title);
        dataWatcher.a(2, title);
        dataWatcher.a(6, (float) health);
        dataWatcher.a(0, (byte) (0x20 | 1 << 5));

        PacketPlayOutSpawnEntityLiving spawn = new PacketPlayOutSpawnEntityLiving();
        spawn.a = adapter.getEntityId();
        spawn.b = EntityType.ENDER_DRAGON.getTypeId();
        spawn.c = (int) Math.floor(location.getBlockX() * 32.0D);
        spawn.d = (int) Math.floor((location.getBlockY() - 100) * 32.0D);
        spawn.e = (int) Math.floor(location.getBlockZ() * 32.0D);
        spawn.i = (byte) 0;
        spawn.j = (byte) 0;
        spawn.k = (byte) 0;
        spawn.l = dataWatcher;

        player.setMetadata("spawned_bossbar", new FixedMetadataValue(Spotify.getInstance(), true));
        Common.sendPacket(player, spawn);

        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(adapter.getEntityId(), dataWatcher, true);
        Common.sendPacket(player, metadata);
    }

    public void remove(Player player) {
        DataWatcher dataWatcher = new DataWatcher((Entity) null);
        dataWatcher.watch(2, " ");
        dataWatcher.watch(10, " ");
        dataWatcher.watch(0, (byte) 0x20 & ~(1 << 5));
        dataWatcher.watch(6, (float) 0);

        PacketPlayOutEntityMetadata metadata = new PacketPlayOutEntityMetadata(this.adapter.getEntityId(), dataWatcher.c(), false);
        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(this.adapter.getEntityId());

        Common.sendPacket(player, metadata);
        Common.sendPacket(player, destroy);

        player.removeMetadata("spawned_bossbar", Spotify.getInstance());
    }

}
