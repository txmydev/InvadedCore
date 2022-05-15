package invaded.cc.core.database.ranks;

import com.google.common.collect.Lists;
import invaded.cc.common.library.gson.reflect.TypeToken;
import invaded.cc.core.Spotify;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.rank.Rank;
import jodd.http.HttpResponse;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRankStorage implements RankStorage{

    public static Comparator<Rank> PRIORITY_COMPARATOR = (o1, o2) -> o2.getPriority() - o1.getPriority();
    // private final List<Rank> priorityOrdered;
    @Getter
    private List<Rank> ranks;

    public HttpRankStorage() {
        this.ranks = Lists.newArrayList();
        this.loadAll();

        // priorityOrdered = ranks.values().stream().sorted(PRIORITY_COMPARATOR).collect(Collectors.toList());
    }

    public void loadAll() {
        HttpResponse httpResponse = RequestHandler.get("/ranks");

        if (httpResponse.statusCode() != 200) {
            Bukkit.getLogger().severe("RankHandler - Couldn't get all the ranks due to a status code " + httpResponse.statusCode());
            httpResponse.close();
            return;
        }

        if (!this.ranks.isEmpty()) this.ranks.clear();

        this.ranks = Spotify.GSON.fromJson(httpResponse.bodyText(), new TypeToken<List<Rank>>() {}.getType());

        this.ranks.sort(PRIORITY_COMPARATOR);

        this.inherit();

        httpResponse.close();
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

    public void save(Rank rank) {
        Map<String, Object> map = new HashMap<>();

        map.put("name", rank.getName());
        map.put("priority", rank.getPriority());
        map.put("prefix", rank.getPrefix());
        map.put("suffix", rank.getSuffix());
        map.put("defaultRank", rank.isDefaultRank());
        map.put("italic", rank.isItalic());
        map.put("bold", rank.isBold());
        map.put("color", rank.getColor().name());
        map.put("permissions", rank.getPermissions());

        HttpResponse response = RequestHandler.post("/ranks", map);
        response.close();
    }


    public Rank getRank(String name) {
        for (Rank rank : ranks) {
            if (rank.getName().equals(name)) return rank;
        }

        return null;
    }


    public Rank getDefault() {
        for (Rank rank : ranks) {
            if (rank.isDefaultRank()) return rank;
        }

        return null;
    }

    @Override
    public void saveAll() {
        ranks.forEach(this::save);
    }

    public Rank getHighestRank(){
        return this.ranks.get(0);
    }

    public boolean isHighestRank(Player player) {
        return Spotify.getInstance().getProfileHandler().getRank(player).getName()
                .equalsIgnoreCase(getHighestRank().getName());

    }

}
