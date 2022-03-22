package invaded.cc.core.nametag;

import com.google.common.base.Strings;
import invaded.cc.core.Spotify;
import invaded.cc.core.event.PlayerDisguiseEvent;
import invaded.cc.core.event.PlayerUnDisguiseEvent;
import invaded.cc.core.util.Common;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerHealthChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static invaded.cc.core.nametag.NametagProvider.DEFAULT_NAMETAG_PROVIDER;

public final class NametagManager extends Thread implements Listener {

    private final Spotify plugin;

    @Getter
    @Setter
    private NametagProvider provider;

    private NametagUpdateThread updateThread;

    private final Map<Player, Set<String>> registeredTeams = new HashMap<>();

    private final Map<NametagRefresh, Boolean> updateQueue = new ConcurrentHashMap<>();

    public NametagManager(Spotify plugin) {
        super("Nightmare - Nametag Update Thread");

        setDaemon(true);

        this.plugin = plugin;

        provider = DEFAULT_NAMETAG_PROVIDER;
        updateThread = new NametagUpdateThread(this);
        updateThread.start();

        start();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private boolean isRegistered(Player player, String name) {
        Set<String> teams = registeredTeams.get(player);
        return teams != null && teams.contains(name);
    }

    private void registerTeam(Player player, String name) {
        Set<String> teams;

        if (!registeredTeams.containsKey(player)) {
            registeredTeams.put(player, teams = new HashSet<>());
        } else {
            teams = registeredTeams.get(player);
        }

        teams.add(name);
    }

    private PacketPlayOutScoreboardScore getHealthPacket(Player player, CraftPlayer craftPlayer) {
        PacketPlayOutScoreboardScore packet = new PacketPlayOutScoreboardScore();
        packet.a = player.getName();
        packet.b = "PlayerHealth";
        packet.c = ((int) Math.ceil(player.getHealth() + craftPlayer.getHandle().getAbsorptionHearts()));
        packet.d = 0;
        return packet;
    }

    @EventHandler
    public void onPlayerHealthChange(PlayerHealthChangeEvent event) {
        Player player = event.getPlayer();
        if (!player.isOnline()) return;

        CraftPlayer craftPlayer = (CraftPlayer) player;
        player.sendPacket(getHealthPacket(player, craftPlayer));
        EntityPlayer entity = craftPlayer.getHandle();
        EntityTracker tracker = ((WorldServer) entity.world).tracker;
        EntityTrackerEntry entry = (EntityTrackerEntry) tracker.trackedEntities.get(entity.getId());
        if (entry == null) return;

        for (Object tracked : entry.trackedPlayers) {
            ((EntityPlayer)tracked).playerConnection.sendPacket(getHealthPacket(player, craftPlayer));
        }
    }



    @Override
    public void run() {
        try {
            while (true) {
                updateQueue.forEach((update, ignored) -> {
                    Player player = update.getPlayer(), target = update.getTarget();
                    if (player == null || target == null) return;

                    Nametag nametag = provider.getNametag(player, target);
                    String team = nametag.getTeam();
                    if (!isRegistered(player, team)) {
                        registerTeam(player, team);
                        player.sendPacket(nametag.createTeamPacket());
                    }

                    player.sendPacket(nametag.addPlayerPacket(target));

                    CraftPlayer craftTarget = (CraftPlayer) target;

                    // Checking if there use display names
                    if (plugin.getConfig().getBoolean("use-display-names", false)) {
                        String name = nametag.getTablistName();
                        if (!Strings.isNullOrEmpty(name)) {
                            if (nametag.getPrefix().equals(name)) {
                                name += target.getName();
                            }

                            PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.updateDisplayName(craftTarget.getHandle());
                            packet.username = ChatColor.translateAlternateColorCodes('&', name + nametag.getSuffix());
                            //packet.getData().add(new PacketPlayOutPlayerInfo.PlayerInfoData(craftTarget.getProfile(), 1, WorldSettings.EnumGamemode.SURVIVAL, new ChatMessage(ChatColor.translateAlternateColorCodes('&', name + nametag.getSuffix()))));
                            player.sendPacket(packet);
                        }
                    }

                    // Health shown
                    if (true) {
                        player.sendPacket(getHealthPacket(target, craftTarget));
                    }

                    updateQueue.remove(update);
                });

                try {
                    Thread.sleep(provider.getUpdateInterval() * 50L);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void update(Player player, Player target) {
        updateQueue.put(new NametagRefresh(player, target), true);
    }

    public void updateAll(Player target) {
        update(target, target);

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (target.equals(player)) continue;

            update(player, target);
        }
    }

    public void updateAll() {
        plugin.getServer().getOnlinePlayers().forEach(this::updateAll);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        //
        if (true) {
            Scoreboard scoreboard = player.getScoreboard();
            if (scoreboard.getObjective(DisplaySlot.BELOW_NAME) == null) {
                Objective objective = scoreboard.registerNewObjective("PlayerHealth", "health");
                objective.setDisplayName(ChatColor.RED + "❤");
                objective.setDisplaySlot(DisplaySlot.BELOW_NAME);
            }
        }

        update(player, player);
        for (Player online : player.getServer().getOnlinePlayers()) {
            if (player.equals(online)) continue;

            update(player, online);
            update(online, player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDisguise(PlayerDisguiseEvent event) {
        updateAll(event.getPlayer());
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerUnDisguise(PlayerUnDisguiseEvent event) {
        updateAll(Common.getPlayer(event.getProfile()));
    }

    private void handleDisconnect(Player player) {
        registeredTeams.remove(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        handleDisconnect(event.getPlayer());
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        handleDisconnect(event.getPlayer());
    }
}