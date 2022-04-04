package invaded.cc.core.database.grant.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import invaded.cc.common.library.gson.JsonArray;
import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.common.library.gson.JsonParser;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.grant.GrantStorage;
import invaded.cc.core.grant.Grant;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.rank.RankHandler;
import jodd.http.HttpResponse;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpGrantStorage implements GrantStorage {
    public void updateGrant(Grant grant) {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", grant.getProfile().getId().toString());
        map.put("name", grant.getProfile().getName());
        map.put("rank", grant.getRank());
        map.put("addedAt", grant.getAddedAt());
        map.put("addedBy", grant.getAddedBy());

        if (grant.getRemovedAt() > 0)
            map.put("removedAt", grant.getRemovedAt());

        if (grant.getRemovedBy() != null)
            map.put("removedBy", grant.getRemovedBy());

        HttpResponse response = RequestHandler.post("/grants", map);
        response.close();
    }

    public List<Grant> get(Profile profile) {
        List<Grant> list = new ArrayList<>();

        Map<String, Object> query = Maps.newHashMap();
        query.put("uuid", profile.getId().toString());

        HttpResponse response = RequestHandler.get("/grants", query);

        if (response.statusCode() != 200) {
            list.add(new Grant(profile, System.currentTimeMillis(), "Default", "CONSOLE"));
            return list;
        }

        JsonArray grants = JsonParser.parseString(response.bodyText()).getAsJsonArray();
        RankHandler rankHandler = Spotify.getInstance().getRankHandler();

        grants.iterator().forEachRemaining(element -> {
            JsonObject jsonObject = element.getAsJsonObject();

            Grant grant = new Grant(profile, jsonObject.get("addedAt").getAsLong(), jsonObject.get("rank").getAsString(), jsonObject.get("addedBy").getAsString());

            if (rankHandler.getRank(grant.getRank()) == null) {
                removeGrant(grant);
                return;
            }

            grant.setRemovedBy(jsonObject.get("removedBy").getAsString());
            grant.setRemovedAt(jsonObject.get("removedAt").getAsLong());

            if (grant.getRemovedAt() != 0) grant.setUse(false);
            list.add(grant);
        });

        List<Grant> inUse = list.stream().filter(Grant::isUse).collect(Collectors.toList());
        if (inUse.size() <= 0) list.add(new Grant(profile, System.currentTimeMillis(), "Default", "CONSOLE"));

        response.close();
        return list;
    }

    public void removeGrant(Grant grant) {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", grant.getProfile().getId().toString());
        map.put("rank", grant.getRank());
        map.put("addedAt", grant.getAddedAt());
        map.put("addedBy", grant.getAddedBy());

        if (grant.getRemovedAt() > 0)
            map.put("removedAt", grant.getRemovedAt());

        if (grant.getRemovedBy() != null)
            map.put("removedBy", grant.getRemovedBy());

        HttpResponse response = RequestHandler.delete("/grants", map);

        if (response.statusCode() == 404) {
            Bukkit.getLogger().info("Grant Handler - Couldn't remove grant of " + grant.getProfile().getName() + " with response " + response.bodyText());
        }
    }
}
