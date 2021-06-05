package invaded.cc.core.listener;

import invaded.cc.core.util.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class SignListener implements Listener {

    @EventHandler
    public void onSignCreate(SignChangeEvent event) {
        for(int i = 0; i < event.getLines().length; i++) {
            if(event.getLine(i) == null) continue;
            event.setLine(i, Color.translate(event.getLine(i)));
        }
    }

}
