package invaded.cc.core.util;

public class BooleanUtils {

    public static String getValueWithSymbols(boolean colorized, boolean value) {
        return value ? (colorized ? CC.GREEN : "") + "✔" : (colorized ? CC.RED : "") + "✘";
    }

    public static String getValueWithSymbols(boolean value) {
        return getValueWithSymbols(true, value);
    }

}
