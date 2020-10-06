package invaded.cc.prefix;

import java.util.HashMap;
import java.util.Map;

public class PrefixHandler {

    private Map<String, Prefix> prefixes;

    public void load() {
        this.prefixes = new HashMap<>();

    }
}
