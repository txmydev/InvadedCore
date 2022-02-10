package invaded.cc.core.tablist;

import invaded.cc.core.Spotify;
import org.bukkit.entity.Player;

public interface TabAdapter {

    Spotify plugin = Spotify.getInstance();

    void updateTab(Player player, Tablist tab);

    default long getInterval() {
        return 5L;
    }

}
