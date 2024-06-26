package invaded.cc.core.database.tags.impl;

import com.google.common.collect.Lists;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.tags.TagStorage;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.tags.Tag;
import jodd.http.HttpResponse;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpTagStorage  implements TagStorage {

    List<Tag> tags = new ArrayList<>();
    public void load() {
        HttpResponse response = RequestHandler.get("/tags");

        if (response.statusCode() != 200) this.tags = Lists.newArrayList();
        else this.tags = Spotify.GSON.fromJson(response.bodyText(), new TypeToken<List<Tag>>() {
        }.getType());

        response.close();

        tags.add(new Tag("@@symbol_heart", "&4&l❤", 150, true));
        tags.add(new Tag("@@symbol_star", "&e&l★", 150, true));
        tags.add(new Tag("@@symbol_kuso", "&a&lクソ", 150, true));
        tags.add(new Tag("@@symbol_lightning", "&6&l⚡", 150, true));
    }

    public void save(Tag tag) {
        if (tag.getId().startsWith("@@symbol_")) return;

        Map<String, Object> map = new HashMap<>();
        map.put("id", tag.getId());
        map.put("display", tag.getDisplay());
        map.put("price", tag.getPrice());
        map.put("suffix", tag.isSuffix());

        HttpResponse response = RequestHandler.post("/tags", map);
        response.close();
    }

    @Override
    public void saveAll() {
        this.tags.forEach(this::save);
    }

    public void remove(Tag tag) {
        tags.remove(tag);

        Map<String, Object> map = new HashMap<>();
        map.put("id", tag.getId());

        HttpResponse response = RequestHandler.delete("/tags", map);
        response.close();
    }

    public Tag getById(String id) {
        for (Tag tag : tags) {
            if (id.equals(tag.getId())) return tag;
        }

        return null;
    }

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
