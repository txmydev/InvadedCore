package invaded.cc.util;

import org.bukkit.ChatColor;

public class Color {

    public static String translate(String text){
        return ChatColor.translateAlternateColorCodes('&', text);
    }

}
