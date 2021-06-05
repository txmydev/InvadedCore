package invaded.core.util;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

public class Filter {

    @Getter
    private static final List<String> filterWords = Arrays.asList("l", "kys", "suicidate", "cancer"
    , "malo", "enfermo", "autista", "pelotudito", "mogo", "mogolico"
    ,"morite", "feo");

    @Getter
    private static final List<String> commandsBlocked = Arrays.asList("//calc", "//calculate"
    , "//eval", "/me", "/pl");

    public static boolean needFilter(String s){
        return filterWords.contains(s.toLowerCase()) || commandsBlocked.contains(s.toLowerCase());
    }

    public static final String PREFIX = "&c[Filtered]";

    public static boolean isBlocked(String message) {
        return commandsBlocked.contains(message);
    }
}
