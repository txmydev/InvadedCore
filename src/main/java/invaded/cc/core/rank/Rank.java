package invaded.cc.core.rank;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Rank {

    private final String name;
    private String prefix = "", suffix = "";
    private List<String> permissions = new ArrayList<>();
    private ChatColor color = ChatColor.WHITE;
    private boolean defaultRank;
    private boolean italic = false, bold = false;
    private int priority;

    private boolean changed = false;

    public Rank(String name) {
        this.name = name;
    }

    public Document toDocument() {
        return new org.bson.Document("name", name)
                .append("priority", priority)
                .append("defaultRank", defaultRank)
                .append("prefix", prefix)
                .append("suffix", suffix)
                .append("permissions", permissions)
                .append("color", color.name())
                .append("italic", italic)
                .append("bold", bold);
    }

    public String getColoredName() {
        return color + (italic ? "" + ChatColor.ITALIC : "") + (bold ? "" + ChatColor.BOLD : "") + name;
    }

    public String getColors() {
        return color + (italic ? "" + ChatColor.ITALIC : "") + (bold ? "" + ChatColor.BOLD : "");
    }

    public boolean isMedia() {
        return name.equalsIgnoreCase("Twitch") || name.equalsIgnoreCase("Youtube")
                || name.equalsIgnoreCase("Partner");
    }


}
