package invaded.cc.core.tablist;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.ExceptionCounter;
import org.bukkit.Bukkit;

public class TablistThread extends Thread {


    public TablistThread() {
        super("Spotify - Tablist Thread");
    }

    private final ExceptionCounter exCounter = new ExceptionCounter(5);

    @Override
    public void run() {
        while(true) {
            try{
                TablistHandler handler = Spotify.getInstance().getTablistHandler();
                TabAdapter adapter = handler.getAdapter();
                if(adapter == null) {
                    Thread.sleep(200L);
                } else {
                    handler.getPlayerTabs().forEach((id, tab) -> {
                        if (Bukkit.getPlayer(id) != null) {
                            handler.getAdapter().updateTab(Bukkit.getPlayer(id), tab);
                        }
                    });

                    Thread.sleep(handler.getAdapter().getInterval() * 50L);
                }
            }catch(Exception ex) {
                if(exCounter.hasFinished()) {
                    Bukkit.getLogger().info("Reached a limit of 5 exceptions in the tab thread, stopping it so it doesn't spam the console.");
                    break;
                }
                exCounter.add();
                ex.printStackTrace();
            }
        }
    }
}
