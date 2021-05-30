package invaded.cc.tasks;

import invaded.cc.Spotify;
import invaded.cc.util.menu.Menu;
import org.bukkit.scheduler.BukkitRunnable;

public class MenuTask extends BukkitRunnable {

    public MenuTask(){
        runTaskTimerAsynchronously(Spotify.getInstance(), 20L, 5L);
    }

    @Override
    public void run() {
        try{
            for(Menu menu : Menu.menus){
                if(menu.isUpdate()) menu.update();
            }
        }catch(Exception ignored){ }
    }
}
