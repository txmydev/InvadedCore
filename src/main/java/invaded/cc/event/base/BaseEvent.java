package invaded.cc.event.base;

import invaded.cc.util.Task;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BaseEvent extends Event {

    @Getter
    private static final HandlerList handlerList = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    public void call() {
        Task.run(() -> {
        Bukkit.getPluginManager().callEvent(this);
        });
    }

}