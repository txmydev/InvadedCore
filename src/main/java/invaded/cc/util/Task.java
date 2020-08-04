package invaded.cc.util;

import invaded.cc.Core;
import org.bukkit.Bukkit;

public class Task {

    public static void later(Runnable run, long time){
        Bukkit.getScheduler().runTaskLater(Core.getInstance(), run, time);
    }

    public static void run(Runnable run){
        Bukkit.getScheduler().runTask(Core.getInstance(), run);
    }

    public static void async(Runnable run){
        Bukkit.getScheduler().runTaskAsynchronously(Core.getInstance(), run);
    }

    public static void asyncLater(Runnable runnable, long time){
        Bukkit.getScheduler().runTaskLaterAsynchronously(Core.getInstance(), runnable, time);

    }
}
