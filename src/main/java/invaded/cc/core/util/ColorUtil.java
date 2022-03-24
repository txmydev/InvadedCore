package invaded.cc.core.util;

import net.minecraft.util.org.apache.commons.lang3.StringUtils;
import org.bukkit.*;

import java.util.*;

public class ColorUtil {

    public static Map<ChatColor, String> colorValues = new HashMap<>();
    private static Map<ChatColor, ColorSet<Integer, Integer, Integer>> colorMap = new HashMap<>();

    static {
        colorValues.put(ChatColor.BLACK, "BLACK");
        colorValues.put(ChatColor.DARK_BLUE, "DARK_BLUE");
        colorValues.put(ChatColor.DARK_GREEN, "DARK_GREEN");
        colorValues.put(ChatColor.DARK_AQUA, "DARK_AQUA");
        colorValues.put(ChatColor.DARK_RED, "DARK_RED");
        colorValues.put(ChatColor.DARK_PURPLE, "DARK_PURPLE");
        colorValues.put(ChatColor.GOLD, "GOLD");
        colorValues.put(ChatColor.GRAY, "GRAY");
        colorValues.put(ChatColor.DARK_GRAY, "DARK_GRAY");
        colorValues.put(ChatColor.BLUE, "BLUE");
        colorValues.put(ChatColor.GREEN, "GREEN");
        colorValues.put(ChatColor.AQUA, "AQUA");
        colorValues.put(ChatColor.RED, "RED");
        colorValues.put(ChatColor.LIGHT_PURPLE, "LIGHT_PURPLE");
        colorValues.put(ChatColor.YELLOW, "YELLOW");
        colorValues.put(ChatColor.WHITE, "WHITE");
        colorValues.put(ChatColor.RESET, "RESET");
        colorValues.put(ChatColor.ITALIC, "ITALIC");
        colorValues.put(ChatColor.UNDERLINE, "UNDERLINE");
        colorValues.put(ChatColor.STRIKETHROUGH, "STRIKETHROUGH");
        colorValues.put(ChatColor.MAGIC, "MAGIC");
        colorValues.put(ChatColor.BOLD, "BOLD");

        colorMap.put(ChatColor.BLACK, new ColorSet<>(0, 0, 0));
        colorMap.put(ChatColor.DARK_BLUE, new ColorSet<>(0, 0, 170));
        colorMap.put(ChatColor.DARK_GREEN, new ColorSet<>(0, 170, 0));
        colorMap.put(ChatColor.DARK_AQUA, new ColorSet<>(0, 170, 170));
        colorMap.put(ChatColor.DARK_RED, new ColorSet<>(170, 0, 0));
        colorMap.put(ChatColor.DARK_PURPLE, new ColorSet<>(170, 0, 170));
        colorMap.put(ChatColor.GOLD, new ColorSet<>(255, 170, 0));
        colorMap.put(ChatColor.GRAY, new ColorSet<>(170, 170, 170));
        colorMap.put(ChatColor.DARK_GRAY, new ColorSet<>(85, 85, 85));
        colorMap.put(ChatColor.BLUE, new ColorSet<>(85, 85, 255));
        colorMap.put(ChatColor.GREEN, new ColorSet<>(85, 255, 85));
        colorMap.put(ChatColor.AQUA, new ColorSet<>(85, 255, 255));
        colorMap.put(ChatColor.RED, new ColorSet<>(255, 85, 85));
        colorMap.put(ChatColor.LIGHT_PURPLE, new ColorSet<>(255, 85, 255));
        colorMap.put(ChatColor.YELLOW, new ColorSet<>(255, 255, 85));
        colorMap.put(ChatColor.WHITE, new ColorSet<>(255, 255, 255));
    }

    public static String convertChatColor(ChatColor color) {
        return colorValues.get(color);
    }

    public static ChatColor getOr(String source, ChatColor or) {
        try {
            return ChatColor.valueOf(source);
        } catch (Exception e) {
            return or;
        }
    }

    public static ChatColor fromHex(int hex) {
        int r = (hex & 0xFF0000) >> 16;
        int g = (hex & 0xFF00) >> 8;
        int b = (hex & 0xFF);
        return fromRGB(r, g, b);
    }

    public static ChatColor fromRGB(int r, int g, int b) {
        TreeMap<Integer, ChatColor> closest = new TreeMap<>();
        colorMap.forEach((color, set) -> {
            int red = Math.abs(r - set.getRed());
            int green = Math.abs(g - set.getGreen());
            int blue = Math.abs(b - set.getBlue());
            closest.put(red + green + blue, color);
        });

        return closest.firstEntry().getValue();
    }

    private static class ColorSet<R, G, B> {
        R red = null;
        G green = null;
        B blue = null;

        ColorSet(R red, G green, B blue) {
            this.red = red;
            this.green = green;
            this.blue = blue;
        }

        public R getRed() {
            return red;
        }

        public G getGreen() {
            return green;
        }

        public B getBlue() {
            return blue;
        }

    }
}