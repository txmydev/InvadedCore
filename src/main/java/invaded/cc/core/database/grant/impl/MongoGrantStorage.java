package invaded.cc.core.database.grant.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.MongoDatabase;
import invaded.cc.core.database.grant.GrantStorage;
import invaded.cc.core.grant.Grant;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.rank.RankHandler;
import jodd.http.HttpResponse;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MongoGrantStorage implements GrantStorage {

    private final MongoCollection<Document> collection;

    public MongoGrantStorage(MongoDatabase database) {
        this.collection = database.getDatabase().getCollection("grants");
    }

    @Override
    public void updateGrant(Grant grant) {
        Document map = new Document();

        map.put("uuid", grant.getProfile().getId().toString());
        map.put("name", grant.getProfile().getName());
        map.put("rank", grant.getRank());
        map.put("addedAt", grant.getAddedAt());
        map.put("addedBy", grant.getAddedBy());

        if (grant.getRemovedAt() > 0)
            map.put("removedAt", grant.getRemovedAt());

        if (grant.getRemovedBy() != null)
            map.put("removedBy", grant.getRemovedBy());

        Document bsonFilter = new Document()
                                    .append("uuid", grant.getProfile().getId().toString())
                                    .append("rank", grant.getRank())
                                    .append("addedAt", grant.getAddedAt())
                                    .append("addedBy", grant.getAddedBy());

        collection.findOneAndDelete(bsonFilter);

        collection.insertOne(map);
    }

    @Override
    public List<Grant> get(Profile profile) {
        List<Grant> list = new ArrayList<>();

        Bson filter = Filters.eq("uuid", profile.getId().toString());

        if(collection.countDocuments(filter) <= 0) {
            list.add(new Grant(profile, System.currentTimeMillis(), "Default", "CONSOLE"));
            return list;
        }

        RankHandler rankHandler = Spotify.getInstance().getRankHandler();

        collection.find(filter).forEach((Consumer<Document>) document -> {
            Grant grant = new Grant(profile, document.getLong("addedAt"),
                    document.getString("rank"), document.getString("addedBy"));

            if (rankHandler.getRank(grant.getRank()) == null) {
                removeGrant(grant);
                return;
            }

            if(document.containsKey("removedBy")) grant.setRemovedBy(document.getString("removedBy"));
            if(document.containsKey("removedAt")) grant.setRemovedAt(document.getLong("removedAt"));

            if (grant.getRemovedAt() != 0) grant.setUse(false);
            list.add(grant);
        });

        return list;
    }

    @Override
    public void removeGrant(Grant grant) {
        Document map = new Document();

        map.put("uuid", grant.getProfile().getId().toString());
        map.put("rank", grant.getRank());
        map.put("addedAt", grant.getAddedAt());
        map.put("addedBy", grant.getAddedBy());

        if (grant.getRemovedAt() > 0)
            map.put("removedAt", grant.getRemovedAt());

        if (grant.getRemovedBy() != null)
            map.put("removedBy", grant.getRemovedBy());

        collection.findOneAndDelete(map);
    }
}
