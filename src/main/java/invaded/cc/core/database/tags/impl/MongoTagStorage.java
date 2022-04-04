package invaded.cc.core.database.tags.impl;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.MongoDatabase;
import invaded.cc.core.database.tags.TagStorage;
import invaded.cc.core.tags.Tag;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MongoTagStorage implements TagStorage {

    private final MongoCollection<Document> collection;
    private final List<Tag> tags = new ArrayList<>();

    public MongoTagStorage(MongoDatabase database) {
        this.collection = database.getDatabase().getCollection("tags");
    }

    @Override
    public void load() {
        collection.find().forEach((Consumer<Document>) document -> {
            tags.add(Spotify.GSON.fromJson(document.toJson(), Tag.class));
        });

        tags.add(new Tag("@@symbol_heart", "&4&l❤", 150, true));
        tags.add(new Tag("@@symbol_star", "&e&l★", 150, true));
        tags.add(new Tag("@@symbol_kuso", "&a&lクソ", 150, true));
        tags.add(new Tag("@@symbol_lightning", "&6&l⚡", 150, true));
    }

    @Override
    public void save(Tag tag) {
        if (tag.getId().startsWith("@@symbol_")) return;

        Document map = new Document();

        map.put("id", tag.getId());
        map.put("display", tag.getDisplay());
        map.put("price", tag.getPrice());
        map.put("suffix", tag.isSuffix());

        collection.insertOne(map);
    }

    @Override
    public void saveAll() {
        tags.forEach(this::save);
    }


    @Override
    public void remove(Tag tag) {
        tags.remove(tag);

        collection.findOneAndDelete(Filters.eq("id", tag.getId()));
    }

    @Override
    public Tag getById(String id) {
        for (Tag tag : tags) {
            if (id.equals(tag.getId())) return tag;
        }

        return null;
    }

    @Override
    public Tag getTagByDisplay(String display) {
        for (Tag tag : tags)
            if (tag.getDisplay().equalsIgnoreCase(display)) return tag;
        return null;
    }

    @Override
    public List<Tag> getTags() {
        return tags;
    }
}
