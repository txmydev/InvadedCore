package invaded.cc.core.tablist;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.PluginDisableEvent;

public class TabListener implements Listener {

    private final invaded.cc.core.tablist.TabHandler handler;

    /**
	 * Constructor to make a new {@link TabUpdater}
	 *
	 * @param handler the handler to register it to
	 */
    public TabListener(TabHandler handler) {
		this.handler = handler;
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
    	final Player player = event.getPlayer();
    	this.handler.getAdapter().addFakePlayers(player);
    }
    
    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
    	final Player player = event.getPlayer();
    	this.handler.removeAdapter(player);
    }
    
    @EventHandler
    public void onDisable(PluginDisableEvent event) {
    	this.handler.getUpdater().cancelExecutors();
    }
}