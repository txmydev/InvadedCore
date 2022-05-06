package invaded.cc.core.listener;

import invaded.cc.core.Spotify;
import invaded.cc.core.freeze.FreezeHandler;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.perms.PermLevel;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class FreezeListener implements Listener {

    @EventHandler
    public void onDamageFreezed(EntityDamageEvent event) {
        if(!(event.getEntity() instanceof Player)) return;

        FreezeHandler freezeHandler = Spotify.getInstance().getFreezeHandler();
        if(freezeHandler.isFreezed((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        String name = player.getDisplayName();
        FreezeHandler freezeHandler = Spotify.getInstance().getFreezeHandler();

        if (freezeHandler.isFreezed(player)) {
            Common.broadcastMessage(PermLevel.STAFF, CC.CHAT_BAR);
            Common.broadcastMessage(PermLevel.STAFF, name + CC.B_RED + " disconnected while frozen!");
            Common.broadcastMessage(PermLevel.STAFF, new ComponentBuilder(CC.B_YELLOW + "Click to ban him!")
                    .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/ban " + name + " disconnected while frozen")));
            Common.broadcastMessage(PermLevel.STAFF,  CC.CHAT_BAR);
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        FreezeHandler freezeHandler = Spotify.getInstance().getFreezeHandler();
        Player player = event.getPlayer();

        if (freezeHandler.isFreezed(player)) {
            freezeHandler.freeze(player);
        }
    }

}
