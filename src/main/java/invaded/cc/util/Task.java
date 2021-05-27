package invaded.cc.util;

import invaded.cc.Basic;
import org.bukkit.Bukkit;

public class Task {

    public static void later(Runnable run, long time){
        Bukkit.getScheduler().runTaskLater(Basic.getInstance(), run, time);
    }

    public static void run(Runnable run){
        Bukkit.getScheduler().runTask(Basic.getInstance(), run);
    }

    public static void async(Runnable run){
        Bukkit.getScheduler().runTaskAsynchronously(Basic.getInstance(), run);
    }

    public static void asyncLater(Runnable runnable, long time){
        Bukkit.getScheduler().runTaskLaterAsynchronously(Basic.getInstance(), runnable, time);

    }
}
