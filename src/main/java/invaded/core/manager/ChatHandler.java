package invaded.core.manager;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
public class ChatHandler {

    private int slowTime;
    private int commandTime;

    private boolean chatValue;

    private Map<String, String> autoReply = new HashMap<>();

    public ChatHandler(){
        this.slowTime = 0;
        this.commandTime =5;

        this.chatValue = true;
    }

    public boolean isChatSlowed(){
        return slowTime != -1;
    }

    public boolean canBeAutoReplied(String message) {
        for (String s : autoReply.keySet()) {
            if(message.contains(s)) return true;
        }
        return false;
    }

    public String getReplyFor(String original) {
        for (Map.Entry<String, String> entry : autoReply.entrySet()) {
            if (original.contains(entry.getKey())) {
                return entry.getValue();
            }
        }

        return "Failed to get an auto-reply for " + original;
    }
}
