package invaded.cc.core.grant;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.Spotify;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.rank.RankHandler;
import jodd.http.HttpResponse;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.stream.Collectors;

@Getter
public class GrantHandler {

    public void updateGrant(Grant grant) {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", grant.getProfile().getId().toString());
        map.put("name", grant.getProfile().getName());
        map.put("rank", grant.getRank());
        map.put("addedAt", grant.getAddedAt());
        map.put("addedBy", grant.getAddedBy());

        if(grant.getRemovedAt() > 0)
            map.put("removedAt", grant.getRemovedAt());

        if(grant.getRemovedBy() != null)
            map.put("removedBy", grant.getRemovedBy());

        HttpResponse response = RequestHandler.post("/grants", map);
        response.close();
       /* Document document = new Document("holderUuid", grant.getProfile().getId().toString());
        boolean exists = false;
        Document found = null;

        document.append("rank", grant.getRank())
                .append("addedAt", grant.getAddedAt())
                .append("addedBy", grant.getAddedBy());

        if (collection.find(document).first() != null) {
            found = collection.find(document).first();
            exists = true;
        }

        if (grant.getRemovedBy() != null) document.append("removedBy", grant.getRemovedBy())
                .append("removedAt", grant.getRemovedAt());

        if (exists && found != null) collection.replaceOne(found, document);
        else collection.insertOne(document);*/
    }

    public void setupPermissions(Profile profile) {
        List<Grant> list = profile.getGrants();

        list.forEach(grant -> {
            if(!grant.isUse()) return;
            if (profile.getPermissions() == null) profile.setPermissions(Sets.newHashSet());

            Rank rank = Spotify.getInstance().getRankHandler().getRank(grant.getRank());
            rank.getPermissions().forEach(profile.getPermissions()::add);
        });
    }

    public List<Grant> get(Profile profile) {
        List<Grant> list = new ArrayList<>();

        Map<String, Object> query = Maps.newHashMap();
        query.put("uuid", profile.getId().toString());

        HttpResponse response = RequestHandler.get("/grants", query);

        if(response.statusCode() != 200) {
            list.add(new Grant(profile, System.currentTimeMillis(), "Default", "CONSOLE"));
            return list;
        }

        JsonArray grants = new JsonParser().parse(response.bodyText()).getAsJsonArray();
        RankHandler rankHandler = Spotify.getInstance().getRankHandler();

        grants.iterator().forEachRemaining(element -> {
            JsonObject jsonObject = element.getAsJsonObject();

            Grant grant = new Grant(profile, jsonObject.get("addedAt").getAsLong(), jsonObject.get("rank").getAsString(), jsonObject.get("addedBy").getAsString());

            if(rankHandler.getRank(grant.getRank()) == null){
                removeGrant(grant);
                return;
            }

            grant.setRemovedBy(jsonObject.get("removedBy").getAsString());
            grant.setRemovedAt(jsonObject.get("removedAt").getAsLong());

            if(grant.getRemovedAt() != 0) grant.setUse(false);
            list.add(grant);
        });

        List<Grant> inUse = list.stream().filter(Grant::isUse).collect(Collectors.toList());
        if(inUse.size() <= 0) list.add(new Grant(profile, System.currentTimeMillis(), "Default", "CONSOLE"));

        response.close();
        return list;
    }

    public void removeGrant(Grant grant) {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", grant.getProfile().getId().toString());
        map.put("name", grant.getName());
        map.put("rank", grant.getRank());
        map.put("addedAt", grant.getAddedAt());
        map.put("addedBy", grant.getAddedBy());

        if(grant.getRemovedAt() > 0)
            map.put("removedAt", grant.getRemovedAt());

        if(grant.getRemovedBy() != null)
            map.put("removedBy", grant.getRemovedBy());

        HttpResponse response = RequestHandler.delete("/grants", map);

        if(response.statusCode() == 404) {
            Bukkit.getLogger().info("Grant Handler - Couldn't remove grant of " + grant.getProfile().getName() + " with response " + response.bodyText());
        }
    }

    public Rank getHighestGrant(List<Grant> grants) {
       // return grants.stream().filter(Grant::isUse).map(grant -> Optional.of(Core.getInstance().getRankHandler().getRank(grant.getRank())).orElse(Core.getInstance().getRankHandler().getDefault())).sorted((rank, rank1) -> rank1.getPriority() - rank.getPriority()).findFirst().orElse(Core.getInstance().getRankHandler().getDefault());
        RankHandler handler = Spotify.getInstance().getRankHandler();

        return grants.stream().filter(Grant::isUse)
                .map(grant -> handler.getRank(grant.getRank())).min((rank1, rank2) -> rank2.getPriority() - rank1.getPriority())
                .orElse(handler.getDefault());
    }
}
