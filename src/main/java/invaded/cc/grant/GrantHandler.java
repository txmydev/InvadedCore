package invaded.cc.grant;

import com.google.common.collect.Lists;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import invaded.cc.Core;
import invaded.cc.database.Database;
import invaded.cc.profile.Profile;
import invaded.cc.rank.Rank;
import lombok.Getter;
import org.bson.Document;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

@Getter
public class GrantHandler {

    private final MongoCollection<Document> collection;

    public GrantHandler() {
        Database database = Core.getInstance().getDb();
        collection = database.getCollection("grants");
    }

    public void updateGrant(Grant grant) {
        Document document = new Document("holderUuid", grant.getProfile().getId().toString());
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
        else collection.insertOne(document);
    }

    public List<Grant> get(Profile profile) {
        List<Grant> grants = Lists.newArrayList();
        FindIterable<Document> finds = collection.find(Filters.eq("holderUuid", profile.getId().toString()));

        finds.forEach((Consumer<? super Document>) doc -> {
            Grant grant = new Grant(profile, doc.getLong("addedAt"),
                    doc.getString("rank"), doc.getString("addedBy"));

            if (doc.containsKey("removedBy")) {
                grant.setRemovedBy(doc.getString("removedBy"));
                grant.setUse(false);
            }

            if(doc.containsKey("removedAt")) {
                grant.setRemovedAt(doc.getLong("removedAt"));
                grant.setUse(false);
            }

            grants.add(grant);
        });

        if(grants.size() == 0) grants.add(
                new Grant(profile, System.currentTimeMillis(), "Default", "console")
        );

        return grants;
    }

    public Rank getHighestGrant(List<Grant> grants) {
        return grants.stream().filter(Grant::isUse).map(grant -> Optional.of(Core.getInstance().getRankHandler().getRank(grant.getRank())).orElse(Core.getInstance().getRankHandler().getDefault())).sorted((rank, rank1) -> rank1.getPriority() - rank.getPriority()).findFirst().orElse(Core.getInstance().getRankHandler().getDefault());
    }
}
