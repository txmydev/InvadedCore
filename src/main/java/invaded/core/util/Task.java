package invaded.core.util;

import invaded.core.Spotify;
import org.bukkit.Bukkit;

public class Task {

    public static void later(Runnable run, long time){
        Bukkit.getScheduler().runTaskLater(Spotify.getInstance(), run, time);
    }

    public static void run(Runnable run){
        Bukkit.getScheduler().runTask(Spotify.getInstance(), run);
    }

    public static void async(Runnable run){
        Bukkit.getScheduler().runTaskAsynchronously(Spotify.getInstance(), run);
    }

    public static void asyncLater(Runnable runnable, long time){
        Bukkit.getScheduler().runTaskLaterAsynchronously(Spotify.getInstance(), runnable, time);

    }
}
