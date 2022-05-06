package invaded.cc.core.tags;

import com.google.common.collect.Lists;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.tags.TagStorage;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.util.CC;
import jodd.http.HttpResponse;
import lombok.Getter;
import com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class TagsHandler {

    private TagStorage storage;
    private List<Tag> tags = new ArrayList<>();
    private final String lunarPrefix = CC.GRAY + "[" + CC.AQUA + "Lunar" + CC.GRAY + "] " + CC.RESET;

    public TagsHandler(TagStorage storage) {
        this.storage = storage;

        this.load();
    }

    public void load() {
        storage.load();
    }

    public void save(Tag tag) {
        storage.save(tag);
    }

    public void remove(Tag tag) {
        storage.remove(tag);
    }

    public Tag getTag(String id) {
       return storage.getById(id);
    }

    public Tag getTagByDisplay(String display) {
        return storage.getTagByDisplay(display);
    }
}
