package invaded.cc.core.bossbar;

import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class TestBossbarAdapter implements BossbarAdapter {
    @Override
    public String getTitle() {
        return "Im dumb";
    }
    double hp = 20;

    @Override
    public double getHealth() {
        return hp;
    }

    @Override
    public List<Player> getIgnoredPlayers() {
        return Collections.emptyList();
    }
}
