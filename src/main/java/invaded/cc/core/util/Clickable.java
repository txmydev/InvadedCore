package invaded.cc.core.util;

import com.avaje.ebean.TxCallable;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.List;

public class Clickable {

    private List<TextComponent> components = new ArrayList<>();

    public Clickable(String msg) {
        TextComponent message = new TextComponent(msg);

        this.components.add(message);
    }

    public Clickable(String msg, String hoverMsg, String clickString, ClickEvent.Action type) {
        this.add(msg, hoverMsg, clickString, type);
    }
    public Clickable(String msg, String hoverMsg, String clickString) {
        this.add(msg, hoverMsg, clickString, ClickEvent.Action.RUN_COMMAND);
    }

    public TextComponent add(String msg, String hoverMsg, String clickString, ClickEvent.Action type) {
        TextComponent message = new TextComponent(CC.toColor(msg));

        if (hoverMsg != null) {
            message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverMsg).create()));
        }

        if (clickString != null) {
            message.setClickEvent(new ClickEvent(type, clickString));
        }

        this.components.add(message);

        return message;
    }

    public void add(String message) {
        this.components.add(new TextComponent(message));
    }

    public void sendToPlayer(Player player) {
        player.sendMessage(this.asComponents());
    }

    public TextComponent[] asComponents() {
        return this.components.toArray(new TextComponent[0]);
    }

}
