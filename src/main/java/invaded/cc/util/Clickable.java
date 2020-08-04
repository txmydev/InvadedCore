package invaded.cc.util;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Clickable {

    private TextComponent textComponent;
    private String text;

    public Clickable(String text){
        this.textComponent = new TextComponent(Color.translate(text));
        this.text = text;
    }

    public Clickable hover(HoverEvent.Action action, String hover){
        this.textComponent.setHoverEvent(new HoverEvent(action,
                new ComponentBuilder(Color.translate(hover)).create()));

        return this;
    }

    public Clickable clickEvent(ClickEvent.Action action, String click){
        if(action == ClickEvent.Action.RUN_COMMAND && !click.substring(0, 1).equalsIgnoreCase("/")) click = "/" + click;
        this.textComponent.setClickEvent(new ClickEvent(action, click));

        return this;
    }

    public TextComponent get(){
        return textComponent;
    }

}
