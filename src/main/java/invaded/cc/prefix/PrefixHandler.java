package invaded.cc.prefix;

import com.google.common.collect.Lists;
import invaded.cc.Core;
import invaded.cc.manager.RequestHandler;
import jodd.http.HttpResponse;
import lombok.Getter;
import net.minecraft.util.com.google.common.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class PrefixHandler {

    private List<Prefix> prefixes;

    public PrefixHandler() {
        this.load();
    }

    public void load() {
        HttpResponse response = RequestHandler.get("/prefixs");

        if(response.statusCode() != 200) this.prefixes = Lists.newArrayList();
        else this.prefixes = Core.GSON.fromJson(response.bodyText(), new TypeToken<List<Prefix>>() {}.getType());

        response.close();
    }

    public void save(Prefix prefix) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", prefix.getId());
        map.put("display", prefix.getDisplay());

        HttpResponse response = RequestHandler.post("/prefixs", map);
        response.close();
    }

    public Prefix getPrefix(String id){
        for (Prefix prefix : prefixes) {
            if(id.equals(prefix.getId())) return prefix;
         }

        return null;
    }

    public void remove(Prefix prefix) {
        prefixes.remove(prefix);

        Map<String, Object> map = new HashMap<>();
        map.put("id", prefix.getId());

        HttpResponse response = RequestHandler.delete("/prefixs", map);
        response.close();
    }
}
