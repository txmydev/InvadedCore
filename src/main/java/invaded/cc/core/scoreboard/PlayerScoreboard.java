package invaded.cc.core.scoreboard;

import invaded.cc.core.util.CC;
import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.*;

import java.util.List;

public final class PlayerScoreboard {

    private static final String[] ENTRIES = new String[15];

    static {
        for (int i = 0; i < 15; i++) {
            ENTRIES[i] = ChatColor.values()[i].toString();
        }
    }

    private final invaded.cc.core.scoreboard.ScoreboardProvider provider;

    private final Player player;

    private final Scoreboard scoreboard;
    private final Objective objective;
    private String lastTitle;

    private int lastSentEntries;

    public PlayerScoreboard(ScoreboardProvider provider, Player player) {
        this.provider = provider;

        this.player = player;

        Scoreboard scoreboard = player.getScoreboard();
        Server server = player.getServer();
        ScoreboardManager manager = server.getScoreboardManager();
        if (scoreboard == null || scoreboard == manager.getMainScoreboard()) {
            scoreboard = manager.getNewScoreboard();
        }

        Objective objective = (this.scoreboard = scoreboard).getObjective("PlayerScoreboard");
        if (objective == null) {
            objective = scoreboard.registerNewObjective("PlayerScoreboard", "dummy");
        }

        Objective name = scoreboard.registerNewObjective("name", "health");
        name.setDisplaySlot(DisplaySlot.BELOW_NAME);
        name.setDisplayName("§4❤");

        Objective tab = scoreboard.registerNewObjective("tabHealth", "health");
        tab.setDisplaySlot(DisplaySlot.PLAYER_LIST);
        tab.setDisplayName(CC.YELLOW);


        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName(lastTitle = provider.getTitle(player));

        this.objective = objective;

        player.setScoreboard(scoreboard);
    }

    public boolean isVisible() {
        return objective.getDisplaySlot() == DisplaySlot.SIDEBAR;
    }

    private boolean canUpdate() {
        return player != null && player.isOnline() && player.getScoreboard() == scoreboard && isVisible();
    }

    // TODO: Find a better alternative than ChatColor#getLastColors().
    public void update() {
        if (!canUpdate()) return;

        if(!lastTitle.equals(provider.getTitle(player))) objective.setDisplayName(lastTitle = provider.getTitle(player));

        int index;
        List<String> lines = provider.getLines(player);
        if (lines.size() > 14) {
            lines = lines.subList(0, 15);
        }

        for (index = 0; index < lines.size(); ++index) {
            String line = lines.get(index), entry = ENTRIES[index], prefix = line, suffix = "";
            if (line.length() > 16) {
                prefix = line.substring(0, 16);
                if (prefix.endsWith("§")) {
                    prefix = prefix.substring(0, prefix.length() - 1);
                    suffix += '§';
                }

                suffix = StringUtils.left(ChatColor.getLastColors(prefix) + suffix + line.substring(16), 16);
            }

            try {
                Team team = scoreboard.getTeam(entry);
                if (team == null) {
                    team = scoreboard.registerNewTeam(entry);
                }

                if (!team.hasEntry(entry)) {
                    team.addEntry(entry);
                }

                team.setPrefix(prefix);
                team.setSuffix(suffix);

                objective.getScore(team.getName()).setScore(lines.size() - index);
            } catch (Exception ignored) {
            }
        }

        index = lines.size();
        for (int i = 0; i < lastSentEntries - index; ++i) {
            scoreboard.resetScores(ENTRIES[index + i]);
        }
        lastSentEntries = lines.size();
    }

    public void unregister() {
        player.setScoreboard(player.getServer().getScoreboardManager().getMainScoreboard());
    }

    public void setVisible(boolean visible) {
        objective.setDisplaySlot(visible ? DisplaySlot.SIDEBAR : null);
    }
}