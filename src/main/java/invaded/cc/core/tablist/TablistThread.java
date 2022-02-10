package invaded.cc.core.tablist;

import invaded.cc.core.util.ExceptionCounter;
import org.bukkit.Bukkit;

public class TablistThread extends Thread {

    private TablistHandler handler;

    public TablistThread(TablistHandler handler) {
        super("Spotify - Tablist Thread");
        this.handler = handler;
    }

    private final ExceptionCounter exCounter = new ExceptionCounter(5);

    @Override
    public void run() {
        while(true) {
            try{
                if(handler.getAdapter() == null) return;

                handler.getPlayerTabs().forEach((id, tab) -> {
                    if(Bukkit.getPlayer(id) == null) return;
                    handler.getAdapter().updateTab(Bukkit.getPlayer(id), tab);
                });

                Thread.sleep(handler.getAdapter().getInterval() * 50L);

            }catch(Exception ex) {
                if(exCounter.hasFinished()) {
                    Bukkit.getLogger().info("Reached a limit of 5 exceptions in the tab thread, stopping it so it doesn't spam the console.");
                    break;
                }
                exCounter.add();

            }
        }
    }
}
