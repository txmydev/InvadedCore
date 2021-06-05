package invaded.cc.core.database.redis;

import invaded.cc.core.Spotify;
import invaded.cc.core.database.redis.handlers.DataSubscriptionHandler;
import invaded.cc.core.database.redis.handlers.GlobalHandler;
import invaded.cc.core.database.redis.reader.JedisActionReader;
import invaded.cc.core.database.redis.reader.impl.*;
import invaded.cc.core.util.ConfigFile;
import invaded.cc.core.util.ConfigTracker;
import lombok.Getter;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@Getter
public class JedisManager {

    public static JedisActionReader BROADCAST = new JedisActionReader(JedisAction.BROADCAST, new ReaderBroadcast());
    // public static JedisActionReader COLOR_UPDATE = new JedisActionReader(JedisAction.UPDATE_COLOR ,new ReaderColorUpdate());
    public static JedisActionReader DISGUISE = new JedisActionReader(JedisAction.DISGUISE, new ReaderDisguise());
    public static JedisActionReader HELPOP = new JedisActionReader(JedisAction.HELPOP, new ReaderHelpop());
    //    public static JedisActionReader PUNISHMENT = new JedisActionReader(JedisAction.PUNISHMENT ,new ReaderPunishment());
    //  public static JedisActionReader RANK = new JedisActionReader(JedisAction.RANK ,new ReaderRank());
    //public static JedisActionReader REMOVE_PUNISHMENT = new JedisActionReader(JedisAction.REMOVE_PUNISHMENT ,new ReaderRemovePunishment());
    public static JedisActionReader REPORT = new JedisActionReader(JedisAction.REPORT, new ReaderReport());
    public static JedisActionReader STAFF_CHAT = new JedisActionReader(JedisAction.STAFF_CHAT, new ReaderStaffChat());
    public static JedisActionReader STAFF_JOIN = new JedisActionReader(JedisAction.STAFF_JOIN, new ReaderStaffJoin());
    public static JedisActionReader STAFF_LEAVE = new JedisActionReader(JedisAction.STAFF_LEAVE, new ReaderStaffLeave());
    public static JedisActionReader STAFF_SWITCH = new JedisActionReader(JedisAction.STAFF_SWITCH, new ReaderStaffSwitch());
    public static JedisActionReader UNDISGUISE = new JedisActionReader(JedisAction.UNDISGUISE, new ReaderUnDisguise());
    private static JedisPool pool;
    private JedisSubscriber globalSubscriber;
    private JedisSubscriber playerUpdateSubscriber;
    private JedisConfiguration config;
    private JedisPublisher globalPublisher;
    private JedisPublisher playerUpdatePublisher;

    public JedisManager() {
        ConfigFile configFile = Spotify.getInstance().getDatabaseConfig();
        ConfigTracker configTracker = new ConfigTracker(configFile, "redis");

        if (configTracker.getBoolean("authentication"))
            config = new JedisConfiguration(configTracker.getString("host"),
                    configTracker.getInt("port"),
                    configTracker.getString("username"),
                    configTracker.getString("password"),
                    true);
        else
            config = new JedisConfiguration(configTracker.getString("host"),
                    configTracker.getInt("port"));

        JedisSubscriber.setPool(new JedisPool(new JedisPoolConfig(), config.getHost(), config.getPort(), 4000, config.getPassword()));
        JedisPublisher.setPool(new JedisPool(new JedisPoolConfig(), config.getHost(), config.getPort(), 4000, config.getPassword()));

        this.globalSubscriber = new JedisSubscriber(config, "invaded-channel", new GlobalHandler());
        this.globalPublisher = new JedisPublisher(config, "invaded-channel");

        this.playerUpdatePublisher = new JedisPublisher(config, "player-channel");
        this.playerUpdateSubscriber = new JedisSubscriber(config, "player-channel", new DataSubscriptionHandler());


    }

/*    public void write(JedisAction jedisAction, Object data) {
        JsonChain jsonChain = new JsonChain();

        jsonChain.addProperty("server-id", Core.getInstance().getServerName())
                .addProperty("action", jedisAction.name());

        Profile profile = null;

        switch (jedisAction) {
            case RANK:
                GrantOld rankData = (GrantOld) data;
                profile = rankData.getProfile();

                jsonChain.addProperty("uuid", profile.getUuid().toString())
                        .addProperty("coloredName", profile.getColoredName())
                        .addProperty("rank", profile.getGrant().getRank().getName())
                        .addProperty("name", profile.getName())
                        .addProperty("granter", profile.getGrant().getGranter());
                break;
            case STAFF_JOIN:
            case STAFF_LEAVE:
                profile = (Profile) data;
                jsonChain.addProperty("player", profile.getName())
                        .addProperty("coloredName", profile.getColoredName());
                break;
            case PUNISHMENT:
                BanData banData = (BanData) data;
                jsonChain.addProperty("player", Profile.getByUuid(banData.getUuid()).getName())
                        .addProperty("coloredName", Profile.getByUuid(banData.getUuid())
                                .getColoredName());
                BanWrapper.wrapBan(jsonChain, banData);
                break;
            case MUTE:
                MuteData muteData = (MuteData) data;

                BanWrapper.wrapMute(jsonChain, muteData);
                break;
            case DISGUISE:
                DisguiseData disguiseData = (DisguiseData) data;

                jsonChain
                        .addProperty("uuid", disguiseData.getUuid().toString())
                        .addProperty("fakeName", disguiseData.getFakeName())
                        .addProperty("fakeRank", disguiseData.getFakeRank().getName())
                        .addProperty("texture", disguiseData.getSkin().getTexture())
                        .addProperty("signature", disguiseData.getSkin().getSignature());
                break;
            case UNDISGUISE:
                profile = (Profile) data;

                jsonChain.addProperty("uuid", profile.getUuid().toString());
                break;
            case BROADCAST:
                JsonObject jsonObject = (JsonObject) data;

                jsonChain.addProperty("message", jsonObject.get("message").getAsString())
                        .addProperty("clickable", jsonObject.get("clickable").getAsBoolean())
                        .addProperty("clickAction", jsonObject.get("clickAction").getAsString())
                        .addProperty("clickMessage", jsonObject.get("clickMessage").getAsString())
                        .addProperty("hover", jsonObject.get("hover").getAsBoolean())
                        .addProperty("hoverAction", jsonObject.get("hoverAction").getAsString())
                        .addProperty("hoverMessage", jsonObject.get("hoverMessage").getAsString());
                break;
            case ADMIN_CHAT:
            case STAFF_CHAT:
                jsonObject = (JsonObject) data;

                jsonChain.addProperty("coloredName", jsonObject.get("coloredName").getAsString())
                        .addProperty("player", jsonObject.get("player").getAsString())
                        .addProperty("message", jsonObject.get("message").getAsString());
                break;
            case HELPOP:
            case REPORT:
            case JOIN_SERVER:
            case UNBAN:
            case KICK_PLAYER:
            case UNMUTE:
            case STAFF_SWITCH:
                jsonObject = (JsonObject) data;

                jsonChain.addAll(jsonObject);
                break;
            case UPDATE_COLOR:
                profile = (Profile) data;

                jsonChain.addProperty("uuid", profile.getUuid().toString())
                        .addProperty("name", profile.getName())
                        .addProperty("color", profile.getChatColor() == null ? "none" : profile.getChatColor().name())
                        .addProperty("italic", profile.isItalic())
                        .addProperty("bold", profile.isBold());

                break;
        }

        sendBungee(jsonChain.get());
    }*/

    public void globalClose() {
        globalSubscriber.stop();
    }

}
