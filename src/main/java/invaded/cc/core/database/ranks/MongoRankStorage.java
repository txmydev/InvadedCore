package invaded.cc.core.database.ranks;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.MongoDatabase;
import invaded.cc.core.rank.Rank;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MongoRankStorage implements RankStorage {

    public static Comparator<Rank> PRIORITY_COMPARATOR = (o1, o2) -> o2.getPriority() - o1.getPriority();
    // private final List<Rank> priorityOrdered;
    private final Spotify plugin;
    private final MongoCollection<Document> collection;
    private Rank defaultRank;

    private final List<Rank> ranks = new ArrayList<>();

    public MongoRankStorage(MongoDatabase database) {
        this.plugin = Spotify.getInstance();
        this.collection = database.getDatabase().getCollection("ranks");

        this.loadAll();
    }

    public void loadAll() {
        collection.find()
                .forEach((Consumer<? super Document>)
                        document -> {
                            Rank rank = new Rank(document.getString("name"));
                            rank.setPriority(document.getInteger("priority"));
                            rank.setDefaultRank(document.getBoolean("defaultRank"));
                            rank.setPrefix(document.getString("prefix"));
                            rank.setSuffix(document.getString("suffix"));
                            rank.setPermissions(document.getList("permissions", String.class));
                            try {
                                rank.setColor(ChatColor.valueOf(document.getString("color")));
                            }catch(Exception ex) {
                                ex.printStackTrace();
                                rank.setColor(ChatColor.WHITE);
                            }

                            rank.setItalic(document.getBoolean("italic"));
                            rank.setBold(document.getBoolean("bold"));

                            ranks.add(rank);
                            plugin.getLogger().info("Gotten rank " + rank.getName() + " from json "
                                    + document.toJson());
                        }
                );

        ranks.sort(PRIORITY_COMPARATOR);

        defaultRank = ranks.stream().filter(rank -> rank.isDefaultRank()).findFirst().orElse(new Rank("Default"));
        defaultRank.setDefaultRank(true);

        if(!ranks.contains(defaultRank)) ranks.add(defaultRank);

        this.inherit();
    }

    private void inherit() {
        this.ranks.forEach(rank -> {
            this.ranks.forEach(other -> {
                if(other.getPriority() < rank.getPriority()) {
                    rank.getPermissions().addAll(other.getPermissions().stream().filter(perm -> !rank.getPermissions().contains(perm)).collect(Collectors.toList()));
                }
            });
        });
    }

    @Override
    public Rank getRank(String name) {
        for (Rank rank : ranks) {
            if(rank.getName().equalsIgnoreCase(name)) return rank;
        }

        return null;
    }

    private final ReplaceOptions options = new ReplaceOptions().upsert(true);

    @Override
    public void save(Rank rank) {
        collection.replaceOne(Filters.eq("name", rank.getName()), rank.toDocument(), options);
    }

    @Override
    public Rank getHighestRank() {
        return ranks.get(0);
    }

    @Override
    public Rank getDefault() {
        return defaultRank;
    }

    @Override
    public void saveAll() {
        ranks.forEach(this::save);
    }

    @Override
    public boolean isHighestRank(Player player) {
        return plugin.getProfileHandler().getProfile(player).getHighestRank().getPriority() == ranks.get(0).getPriority();
    }

    @Override
    public List<Rank> getRanks() {
        return ranks;
    }
}
