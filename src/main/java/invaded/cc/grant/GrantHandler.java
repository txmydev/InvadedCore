package invaded.cc.grant;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import invaded.cc.Core;
import invaded.cc.database.Database;
import invaded.cc.manager.RequestHandler;
import invaded.cc.profile.Profile;
import invaded.cc.rank.Rank;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import lombok.Getter;
import org.bson.Document;
import org.bukkit.Bukkit;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class GrantHandler {

    public void updateGrant(Grant grant) {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", grant.getProfile().getId().toString());
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

            Rank rank = Core.getInstance().getRankHandler().getRank(grant.getRank());
            rank.getPermissions().forEach(profile.getPermissions()::add);
        });
    }

    public List<Grant> get(Profile profile) {
        List<Grant> list = new ArrayList<>();

        HttpResponse response = RequestHandler.get("/grants/uuid/" + profile.getId().toString());

        if(response.statusCode() == 404) {
            list.add(new Grant(profile, System.currentTimeMillis(), "Default", "CONSOLE"));
            return list;
        }

        JsonArray grants = new JsonParser().parse(response.body()).getAsJsonArray();

        grants.iterator().forEachRemaining(element -> {
            JsonObject jsonObject = element.getAsJsonObject();

            Grant grant = new Grant(profile, jsonObject.get("addedAt").getAsLong(), jsonObject.get("rank").getAsString(), jsonObject.get("addedBy").getAsString());
            grant.setRemovedBy(jsonObject.get("removedBy").getAsString());
            grant.setRemovedAt(jsonObject.get("removedAt").getAsLong());

            if(grant.getRemovedAt() != 0) grant.setUse(false);
            list.add(grant);
        });

        List<Grant> inUse = list.stream().filter(Grant::isUse).collect(Collectors.toList());
        if(inUse.size() <= 0) list.add(new Grant(profile, System.currentTimeMillis(), "Default", "CONSOLE"));

        response.close();
        return list;
//        List<Grant> grants = Lists.newArrayList();
//        FindIterable<Document> finds = collection.find(Filters.eq("holderUuid", profile.getId().toString()));
//
//        finds.forEach((Consumer<? super Document>) doc -> {
//            Grant grant = new Grant(profile, doc.getLong("addedAt"),
//                    doc.getString("rank"), doc.getString("addedBy"));
//
//            if (doc.containsKey("removedBy")) {
//                grant.setRemovedBy(doc.getString("removedBy"));
//                grant.setUse(false);
//            }
//
//            if(doc.containsKey("removedAt")) {
//                grant.setRemovedAt(doc.getLong("removedAt"));
//                grant.setUse(false);
//            }
//
//            grants.add(grant);
//        });
//
//        if(grants.size() == 0) grants.add(
//                new Grant(profile, System.currentTimeMillis(), "Default", "console")
//        );
//
//        return grants;
    }

    public void removeGrant(Grant grant) {
        Map<String, Object> map = new HashMap<>();

        map.put("uuid", grant.getProfile().getId().toString());
        map.put("rank", grant.getRank());
        map.put("addedAt", grant.getAddedAt());
        map.put("addedBy", grant.getAddedBy());

        if(grant.getRemovedAt() > 0)
            map.put("removedAt", grant.getRemovedAt());

        if(grant.getRemovedBy() != null)
            map.put("removedBy", grant.getRemovedBy());

        HttpResponse response = RequestHandler.delete("/grants", map);

        if(response.statusCode() == 404) {
            Bukkit.getLogger().info("Grant Handler - Couldn't remove grant of " + grant.getProfile().getName());
        }
    }

    public Rank getHighestGrant(List<Grant> grants) {
        return grants.stream().filter(Grant::isUse).map(grant -> Optional.of(Core.getInstance().getRankHandler().getRank(grant.getRank())).orElse(Core.getInstance().getRankHandler().getDefault())).sorted((rank, rank1) -> rank1.getPriority() - rank.getPriority()).findFirst().orElse(Core.getInstance().getRankHandler().getDefault());
    }
}
