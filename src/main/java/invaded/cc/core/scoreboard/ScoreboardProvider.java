package invaded.cc.core.scoreboard;

import invaded.cc.core.util.CC;
import org.bukkit.entity.Player;

import java.util.List;

public interface ScoreboardProvider {

    String STRAIGHT_SCOREBOARD_LINE = CC.SB_BAR;

    String getTitle(Player player);

    List<String> getLines(Player player);
}