package invaded.cc.core.bossbar;

import org.bukkit.entity.Player;

import java.util.List;

public interface BossbarAdapter {

    String getTitle();
    double getHealth();

    List<Player> getIgnoredPlayers();

}
