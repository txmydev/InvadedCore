package invaded.cc.core.rank;

import invaded.cc.core.database.ranks.RankStorage;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.List;

@Getter
public class RankHandler {

    private final RankStorage storage;

    public RankHandler(RankStorage storage) {
        this.storage = storage;
    }


    public Rank getRank(String name) {
        return storage.getRank(name);
    }

    public void save(Rank rank) {
        storage.save(rank);
    }

    public Rank getHighestRank() {
        return storage.getHighestRank();
    }

    public Rank getDefault() {
        return storage.getDefault();
    }

    public void saveAll() {
        storage.saveAll();
    }

    public boolean isHighestRank(Player player) {
        return storage.isHighestRank(player);
    }

    public List<Rank> getRanks() {
        return storage.getRanks();
    }

    public void loadAll() {
        storage.loadAll();
    }
}
