package invaded.cc.core.database.redis.reader;

import invaded.cc.core.Spotify;
import invaded.cc.core.database.redis.JedisAction;
import invaded.cc.core.database.redis.JedisConfiguration;
import invaded.cc.core.database.redis.reader.impl.*;
import invaded.cc.core.util.ConfigFile;
import invaded.cc.core.util.ConfigTracker;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

@Getter
public class JedisActionReader {

    public static final Map<JedisAction, JedisActionReader> readers = new HashMap<>();
    private static final JedisConfiguration CONF;
    public static JedisActionReader BROADCAST = new JedisActionReader(JedisAction.BROADCAST, new ReaderBroadcast());
    public static JedisActionReader COLOR_UPDATE = new JedisActionReader(JedisAction.UPDATE_COLOR, new ReaderColorUpdate());
    public static JedisActionReader DISGUISE = new JedisActionReader(JedisAction.DISGUISE, new ReaderDisguise());
    public static JedisActionReader HELPOP = new JedisActionReader(JedisAction.HELPOP, new ReaderHelpop());
    public static JedisActionReader PUNISHMENT = new JedisActionReader(JedisAction.PUNISHMENT, new ReaderPunishment());
    public static JedisActionReader RANK = new JedisActionReader(JedisAction.RANK, new ReaderRank());
    public static JedisActionReader REMOVE_PUNISHMENT = new JedisActionReader(JedisAction.REMOVE_PUNISHMENT, new ReaderRemovePunishment());
    public static JedisActionReader REPORT = new JedisActionReader(JedisAction.REPORT, new ReaderReport());
    public static JedisActionReader STAFF_CHAT = new JedisActionReader(JedisAction.STAFF_CHAT, new ReaderStaffChat());
    public static JedisActionReader STAFF_JOIN = new JedisActionReader(JedisAction.STAFF_JOIN, new ReaderStaffJoin());
    public static JedisActionReader STAFF_LEAVE = new JedisActionReader(JedisAction.STAFF_LEAVE, new ReaderStaffLeave());
    public static JedisActionReader STAFF_SWITCH = new JedisActionReader(JedisAction.STAFF_SWITCH, new ReaderStaffSwitch());
    public static JedisActionReader UNDISGUISE = new JedisActionReader(JedisAction.UNDISGUISE, new ReaderUnDisguise());

    static {
        ConfigFile configFile = Spotify.getInstance().getDatabaseConfig();
        ConfigTracker configTracker = new ConfigTracker(configFile, "redis");

        CONF = new JedisConfiguration(configTracker.getString("host"),
                configTracker.getInt("port"),
                configTracker.getString("username"),
                configTracker.getString("password"),
                true);
    }

    private final JedisAction action;
    private final Callback<JsonObject> callback;

    public JedisActionReader(JedisAction action, Callback<JsonObject> callback) {
        this.action = action;
        this.callback = callback;

        if (!readers.containsKey(action)) readers.put(action, this);
    }
}