package invaded.cc.rank;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.List;

@Getter
@Setter
public class Rank {

    private final String name;
    private String prefix = "", suffix = "";
    private List<String> permissions;
    private ChatColor color = ChatColor.WHITE;
    private boolean defaultRank;
    private boolean italic = false, bold = false;
    private int weight;

    public Rank(String name) {
        this.name = name;
    }


    public String getColoredName() {
        return color + (italic? "" + ChatColor.ITALIC : "") + (bold ? "" + ChatColor.BOLD : "") + name;
    }

    public String getColors() {
        return color + (italic ? "" + ChatColor.ITALIC : "") + (bold ? "" + ChatColor.BOLD : "");
    }
}
