package invaded.cc.core.util;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class Filter {

    public static final String PREFIX = "&c[Filtered]";
    public static final String SOCIAL_SPY_PREFIX = CC.GRAY + "\uD83D\uDC41" + "[Spy] " ;

    @Getter
    private static final List<String> filterWords = Arrays.asList("l", "kys", "suicidate", "cancer"
            , "malo", "enfermo", "autista", "pelotudito", "mogo", "mogolico"
            , "morite", "feo", "down");
    @Getter
    private static final List<String> commandsBlocked = Arrays.asList("//calc", "//calculate"
            , "//eval", "/me", "/pl");

    public static boolean needFilter(String s) {
        return filterWords.contains(s.toLowerCase()) || commandsBlocked.contains(s.toLowerCase());
    }

    public static boolean isBlocked(String message) {
        return commandsBlocked.contains(message);
    }
}
