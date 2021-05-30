package invaded.cc.tags;

import com.google.common.collect.Lists;
import invaded.cc.Basic;
import invaded.cc.manager.RequestHandler;
import jodd.http.HttpResponse;
import lombok.Getter;
import net.minecraft.util.com.google.common.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class TagsHandler {

    private List<Tag> tags;

    public TagsHandler() {
        this.load();
    }

    public void load() {
        HttpResponse response = RequestHandler.get("/tags");

        if(response.statusCode() != 200) this.tags = Lists.newArrayList();
        else this.tags = Basic.GSON.fromJson(response.bodyText(), new TypeToken<List<Tag>>() {}.getType());

        response.close();
    }

    public void save(Tag tag) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", tag.getId());
        map.put("display", tag.getDisplay());
        map.put("price", tag.getPrice());
        map.put("suffix", tag.isSuffix());

        HttpResponse response = RequestHandler.post("/tags", map);
        response.close();
    }


    public void remove(Tag tag) {
        tags.remove(tag);

        Map<String, Object> map = new HashMap<>();
        map.put("id", tag.getId());

        HttpResponse response = RequestHandler.delete("/tags", map);
        response.close();
    }

    public Tag getTag(String id) {
        for (Tag tag : tags) {
            if(id.equals(tag.getId())) return tag;
        }

        return null;
    }

    public Tag getTagByDisplay(String display) {
        for(Tag tag : tags)
            if(tag.getDisplay().equalsIgnoreCase(display)) return tag;
        return null;
    }
}
