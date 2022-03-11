package invaded.cc.core.tasks;

import invaded.cc.core.Spotify;
import invaded.cc.core.manager.SkinHandler;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Cooldown;
import invaded.cc.core.util.Skin;
import invaded.cc.core.util.SkinFetch;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SkinFetcherTask extends BukkitRunnable {

    private static final Map<UUID, SkinFetch> skinRequests = new HashMap<>();

    private SkinFetch fetch;
    private Cooldown cooldown;

    public static void startRequest(Player player, String target) {
        SkinFetch fetch = new SkinFetch(player, target, false, null);
        skinRequests.put(player.getUniqueId(), fetch);

        Bukkit.getScheduler().runTaskAsynchronously(Spotify.getInstance(), () ->new SkinFetcherTask(fetch).run() );
    }

    public static Skin inmeadiateRequest(Player player, String target){
        SkinFetch fetch = new SkinFetch(player, target, false, null);
        new SkinFetcherTask(fetch).run();
        return fetch.getSkin();
    }

    public static boolean hasRequestPending(Player player) {
        return hasRequestPending(player.getUniqueId());
    }

    public static boolean hasRequestPending(UUID uniqueId) {
        return skinRequests.containsKey(uniqueId);
    }

    public static SkinFetch getPendingFetch(UUID uniqueId) {
        return skinRequests.get(uniqueId);
    }

    public static SkinFetch getPendingFetch(Player player) {
        return getPendingFetch(player.getUniqueId());
    }

    public SkinFetcherTask(SkinFetch fetch) {
        this.fetch = fetch;
    }

    public static void remove(Player player) {
        skinRequests.remove(player.getUniqueId());
    }

    public void finish(Skin skin) {
        fetch.setReady(true);
        fetch.setSkin(skin);
    }

    @Override
    public void run() {
        String name = fetch.getTarget();
        SkinHandler skinHandler = Spotify.getInstance().getDisguiseHandler().getSkinManager();
        try {
            Skin skin = skinHandler.fetchSkinRaw(name);
            finish(skin);

            System.out.println("AYOO IVE FINISHED");
        }catch(IllegalStateException ex){
            ex.printStackTrace();
            if(fetch.getRequester() != null) {
                fetch.getRequester().sendMessage(CC.RED + "There was an error and we couldn't retrieve " + name + "'s skin, he isn't a premium user.");
            }
            fetch.setFailed(true);
            skinRequests.remove(fetch.getRequesterId());
        } catch (IOException e) {
            e.printStackTrace();
            fetch.setFailed(true);
            skinRequests.remove(fetch.getRequesterId());
           // if(fetch.getRequester() != null) fetch.getRequester().sendMessage(CC.RED + "There was an error and we couldn't retrieve " + name + "'s skin, you may need to choose between the ones provided.");
        }
    }

}
