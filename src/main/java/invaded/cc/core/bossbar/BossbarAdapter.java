package invaded.cc.core.bossbar;

import org.bukkit.entity.Player;

import java.util.List;

public interface BossbarAdapter {

    default int getEntityId(){
        return 1392;
    }

    long getInterval();

    String getTitle();
    double getHealth();

    List<Player> getIgnoredPlayers();

}
