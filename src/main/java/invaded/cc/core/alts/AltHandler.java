package invaded.cc.core.alts;

import com.google.common.collect.Maps;
import invaded.cc.common.library.gson.JsonArray;
import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.common.library.gson.JsonParser;
import invaded.cc.core.Spotify;
import invaded.cc.core.manager.RequestHandler;
import jodd.http.HttpRequest;
import jodd.http.HttpResponse;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AltHandler  {

    private final Spotify plugin;
    private final Map<String, List<UUID>> altsMap;

    public AltHandler(Spotify plugin){
        this.plugin = plugin;
        this.altsMap = new HashMap<>();

        this.load();

        plugin.registerListener(new Listener() {

            @EventHandler
            public void onAsyncLogin(AsyncPlayerPreLoginEvent event) {
                UUID id = event.getUniqueId();
                String address = event.getAddress().getHostAddress();

                loadPlayer(address, id);
            }

        });
    }

    public void load() {
        HttpResponse response = RequestHandler.get("/alts");
        try {
            JsonArray array = JsonParser.parseString(response.bodyText()).getAsJsonArray();

            AtomicInteger count = new AtomicInteger();
            array.forEach(element -> {
                JsonObject object = element.getAsJsonObject();
                String address = object.get("address").getAsString();
                JsonArray otherArray = object.get("uuids").getAsJsonArray();
                List<UUID> uuidList = new ArrayList<>();
                otherArray.forEach(id -> {
                    UUID uuid = UUID.fromString(id.getAsString());
                    if(!uuidList.contains(uuid)) uuidList.add(uuid);
                });

                if (altsMap.containsKey(address)) return;
                count.incrementAndGet();
                altsMap.put(address, uuidList);
            });

            response.close();
            System.out.println(" ");
            System.out.println("Loaded " + count.get() + " addresses.");
        }catch(IllegalStateException ignored) {}
    }

    public void loadPlayer(String address, UUID id) {
        // I may actually add a date to track when the ip was registered, and remove it after 30 days
       // checkForChange(address, id);

        if(altsMap.containsKey(address)) add(address, id);
        else altsMap.put(address, new ArrayList<>(Collections.singletonList(id)));
    }

    private void add(String ip, UUID id){
        List<UUID> list = this.altsMap.get(ip);
        if(!list.contains(id)) list.add(id);
    }

    public void checkForChange(String ip, UUID id){
        for (Map.Entry<String, List<UUID>> entry : altsMap.entrySet()) {
            if(entry.getValue().contains(id) && !entry.getKey().equalsIgnoreCase(ip)) entry.getValue().remove(id);
        }
    }

    public void save() {
        // TODO: MAKE SAVING ALL THE ARRAY AT ONCE BC IT'LL TAKE FOREVER IOF NOT
        /*altsMap.forEach((address, list) -> {
            Map<String, Object> query = Maps.newHashMap();
            query.put("address", address);

            Map<String, Object> body = Maps.newHashMap();
            body.put("address", address);
            body.put("uuids", list.stream().map(UUID::toString).collect(Collectors.toList()));

            HttpResponse response = RequestHandler.post("/alts", body, query);
       //     System.out.println("Saving " + address + " with " + list.size() + " uuid's. response code is " + response.statusCode());
            response.close();
        });*/
    }

    public String getIpAddress(OfflinePlayer offlinePlayer) {
        for (Map.Entry<String, List<UUID>> entry : this.altsMap.entrySet()) {
            for(UUID id : entry.getValue()) {
                if(id.equals(offlinePlayer.getUniqueId())){
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    public List<UUID> getAlts(Player player) {
        return altsMap.get(player.getAddress().getAddress().getHostAddress());
    }

    public boolean hasAlts(Player player) {
        return getAlts(player) != null;
    }

}
