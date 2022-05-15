package invaded.cc.core.database.ranks;

import invaded.cc.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.List;

public interface RankStorage {



    Rank getRank(String name);

    void save(Rank rank);

    Rank getHighestRank();

    Rank getDefault();

    void saveAll();

    boolean isHighestRank(Player player);

    List<Rank> getRanks();

    void loadAll();
}
