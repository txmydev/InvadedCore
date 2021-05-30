package invaded.cc.rank;

import com.google.common.collect.Lists;
import invaded.cc.Spotify;
import invaded.cc.manager.RequestHandler;
import jodd.http.HttpResponse;
import lombok.Getter;
import net.minecraft.util.com.google.common.reflect.TypeToken;
import org.bukkit.Bukkit;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RankHandler {

    @Getter
    private List<Rank> ranks;
    // private final List<Rank> priorityOrdered;

    public static Comparator<Rank> PRIORITY_COMPARATOR = (o1, o2) -> o2.getPriority() - o1.getPriority();

    public RankHandler() {
        this.ranks = Lists.newArrayList();
        loadAll();

       // priorityOrdered = ranks.values().stream().sorted(PRIORITY_COMPARATOR).collect(Collectors.toList());
    }

    public void loadAll() {
        HttpResponse httpResponse = RequestHandler.get("/ranks");

        if(httpResponse.statusCode() != 200) {
            Bukkit.getLogger().severe("RankHandler - Couldn't get all the ranks due to a status code " + httpResponse.statusCode());
            httpResponse.close();
            return;
        }

        if(!this.ranks.isEmpty()) this.ranks.clear();

        this.ranks = Spotify.GSON.fromJson(httpResponse.bodyText(), new TypeToken<List<Rank>>() {}.getType());
        this.ranks.sort(PRIORITY_COMPARATOR);
        httpResponse.close();
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
            if(rank.getName().equals(name)) return rank;
        }

        return null;
    }


    public Rank getDefault() {
        for (Rank rank : ranks) {
            if(rank.isDefaultRank()) return rank;
        }

       return null;
    }
}
