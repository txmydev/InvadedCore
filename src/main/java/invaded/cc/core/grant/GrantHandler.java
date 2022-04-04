package invaded.cc.core.grant;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.grant.GrantStorage;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.rank.RankHandler;
import jodd.http.HttpResponse;
import lombok.Getter;
import invaded.cc.common.library.gson.JsonArray;
import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.common.library.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter @RequiredArgsConstructor
public class GrantHandler {

    private final GrantStorage storage;

    public void updateGrant(Grant grant) {
        storage.updateGrant(grant);
    }

    public void setupPermissions(Profile profile) {
        List<Grant> list = profile.getGrants();

        list.forEach(grant -> {
            if (!grant.isUse()) return;
            if (profile.getPermissions() == null) profile.setPermissions(Sets.newHashSet());

            Rank rank = Spotify.getInstance().getRankHandler().getRank(grant.getRank());
            if(rank == null) rank = Spotify.getInstance().getRankHandler().getDefault();
            rank.getPermissions().forEach(profile.getPermissions()::add);
        });
    }

    public List<Grant> get(Profile profile) {
        return storage.get(profile);
    }

    public void removeGrant(Grant grant) {
        storage.removeGrant(grant);
    }

    public Rank getHighestGrant(List<Grant> grants) {
        // return grants.stream().filter(Grant::isUse).map(grant -> Optional.of(Core.getInstance().getRankHandler().getRank(grant.getRank())).orElse(Core.getInstance().getRankHandler().getDefault())).sorted((rank, rank1) -> rank1.getPriority() - rank.getPriority()).findFirst().orElse(Core.getInstance().getRankHandler().getDefault());
        RankHandler handler = Spotify.getInstance().getRankHandler();

        if(grants == null) return handler.getDefault();

        return grants.stream().filter(Grant::isUse)
                .filter(grant -> handler.getRank(grant.getRank()) != null)
                .map(grant -> handler.getRank(grant.getRank())).min((rank1, rank2) -> rank2.getPriority() - rank1.getPriority())
                .orElse(handler.getDefault());
    }
}
