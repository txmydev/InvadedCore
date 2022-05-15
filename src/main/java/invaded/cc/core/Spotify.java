package invaded.cc.core;

import invaded.cc.common.library.gson.Gson;
import invaded.cc.common.library.gson.GsonBuilder;
import invaded.cc.core.alts.AltHandler;
import invaded.cc.core.assemble.Assemble;
import invaded.cc.core.assemble.AssembleAdapter;
import invaded.cc.core.bossbar.BossbarHandler;
import invaded.cc.core.database.MongoDatabase;
import invaded.cc.core.database.MongoSettings;
import invaded.cc.core.database.RedisDatabase;
import invaded.cc.core.database.grant.GrantStorage;
import invaded.cc.core.database.grant.impl.HttpGrantStorage;
import invaded.cc.core.database.grant.impl.MongoGrantStorage;
import invaded.cc.core.database.player.PlayerStorage;
import invaded.cc.core.database.player.impl.HttpPlayerStorage;
import invaded.cc.core.database.player.impl.MongoPlayerStorage;
import invaded.cc.core.database.punishments.PunishmentStorage;
import invaded.cc.core.database.punishments.impl.HttpPunishmentStorage;
import invaded.cc.core.database.punishments.impl.MongoPunishmentStorage;
import invaded.cc.core.database.ranks.HttpRankStorage;
import invaded.cc.core.database.ranks.MongoRankStorage;
import invaded.cc.core.database.ranks.RankStorage;
import invaded.cc.core.database.tags.TagStorage;
import invaded.cc.core.database.tags.impl.HttpTagStorage;
import invaded.cc.core.database.tags.impl.MongoTagStorage;
import invaded.cc.core.freeze.FreezeHandler;
import invaded.cc.core.grant.GrantHandler;
import invaded.cc.core.listener.*;
import invaded.cc.core.lunarapi.LunarAPIHandler;
import invaded.cc.core.manager.*;
import invaded.cc.core.nametag.NametagManager;
import invaded.cc.core.network.NetworkHandler;
import invaded.cc.core.network.server.ServerHandler;
import invaded.cc.core.permission.PermissionHandler;
import invaded.cc.core.poll.PollHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.PunishmentHandler;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.rank.RankHandler;
import invaded.cc.core.settings.SettingsHandler;
import invaded.cc.core.tablist.TablistManager;
import invaded.cc.core.tags.Tag;
import invaded.cc.core.tags.TagsHandler;
import invaded.cc.core.tasks.AnnounceTask;
import invaded.cc.core.tasks.MenuTask;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ConfigFile;
import invaded.cc.core.util.ConfigTracker;
import invaded.cc.core.util.menu.MenuListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Spotify extends JavaPlugin {

    public static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    public static String SERVER_NAME;

    @Getter
    @Setter
    private static API API;

    @Getter
    private static Spotify instance;

    private ConfigFile mainConfig, databaseConfig, announcesConfig, hologramConfig;

    private RedisDatabase redisDatabase;

    private CommandHandler commandHandler;
    private ChatHandler chatHandler;
    private DisguiseHandler disguiseHandler;
    private PunishmentHandler punishmentHandler;
    private PermissionHandler permissionHandler;
    private ProfileHandler profileHandler;
    private GrantHandler grantHandler;
    private RankHandler rankHandler;
    private TagsHandler tagsHandler;
    private CosmeticsHandler cosmeticsHandler;
    private BossbarHandler bossbarHandler;
    private NetworkHandler networkHandler;
    private ServerHandler serverHandler;
    private SocialSpyHandler socialSpyHandler;
    private NametagManager nametagHandler;
    private AltHandler altHandler;
    private TablistManager tablistHandler;
    private LunarAPIHandler lunarHandler;
    private Assemble scoreboardManager;
    private SettingsHandler settingsHandler;
    private PollHandler pollHandler;
    private FreezeHandler freezeHandler;

    private MongoDatabase mongoDatabase;

    @Override
    public void onEnable() {
        instance = this;

        this.mainConfig = new ConfigFile("config.yml", null, false);
        this.databaseConfig = new ConfigFile("database.yml", null, false);
        this.announcesConfig = new ConfigFile("announces.yml", null, false);
        this.hologramConfig = new ConfigFile("holograms.yml", null, false);

        this.setServerName();

        this.setupDatabases();
        this.setupHandlers();

        this.setupTasks();
        this.setupListeners();

        this.registerBungee();
        this.loadPlayers();

        this.setupWorlds();
        this.sendMessage();


        setAPI(new API(this));
    }

    private void setupDatabases() {
        this.redisDatabase = new RedisDatabase();

        this.connectMongo();
    }

    private void setServerName() {
        SERVER_NAME = mainConfig.getString("server-name");
    }

    private void sendMessage() {
        //Bukkit.getConsoleSender().sendMessage(Color.translate("&7[&aSpotify&7] &eWe now playin' music round the server LOL"));
    }

    private void setupWorlds() {
        this.commandHandler.getFlyWorlds().add("Lobby");
    }

    private void setupHandlers() {
        // TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

        commandHandler = new CommandHandler();
        chatHandler = new ChatHandler();
        disguiseHandler = new DisguiseHandler();
        permissionHandler = new PermissionHandler();

        this.loadGrantHandler();
        this.loadProfileHandler();
        this.loadPunishmentHandler();
        this.loadTagHandler();
        this.loadRankHandler();


        cosmeticsHandler = new CosmeticsHandler();
        networkHandler = new NetworkHandler(this);
        serverHandler = new ServerHandler();
        socialSpyHandler = new SocialSpyHandler(this);
        bossbarHandler = new BossbarHandler();
        nametagHandler = new NametagManager(this);
        tablistHandler = new TablistManager();
        // altHandler = new AltHandler(this);
        lunarHandler = new LunarAPIHandler(this);
        settingsHandler = new SettingsHandler();
        pollHandler = new PollHandler(this);
        freezeHandler = new FreezeHandler();
    }



    public void setScoreboardProvider(AssembleAdapter adapter) {
        if (this.scoreboardManager == null) {
            this.scoreboardManager = new Assemble(this, adapter);
            this.scoreboardManager.setTicks(2L);
        }
        else this.scoreboardManager.setAdapter(adapter);
    }

    public void setScoreboardTicks(long ticks) {
        if (this.scoreboardManager == null) return;
        this.scoreboardManager.setTicks(ticks);
    }

    private void loadRankHandler() {
        RankStorage storage;

        if (!isMongo()) storage = new HttpRankStorage();
        else storage = new MongoRankStorage(mongoDatabase);

        rankHandler = new RankHandler(storage);
    }

    private void loadPunishmentHandler() {
        PunishmentStorage storage;

        if (!isMongo()) storage = new HttpPunishmentStorage();
        else storage = new MongoPunishmentStorage(mongoDatabase);

        punishmentHandler = new PunishmentHandler(storage);
    }

    private void loadGrantHandler() {
        GrantStorage storage;

        if (!isMongo()) storage = new HttpGrantStorage();
        else storage = new MongoGrantStorage(mongoDatabase);

        grantHandler = new GrantHandler(storage);
    }

    private void loadTagHandler() {
        TagStorage storage;

        if (!isMongo()) storage = new HttpTagStorage();
        else storage = new MongoTagStorage(mongoDatabase);

        tagsHandler = new TagsHandler(storage);
    }


    public boolean isMongo() {
        return databaseConfig.getBoolean("mongo.enabled");
    }

    private void loadProfileHandler() {
        PlayerStorage storage;

        if (!isMongo()) storage = new HttpPlayerStorage();
        else storage = new MongoPlayerStorage(mongoDatabase);

        profileHandler = new ProfileHandler(storage);
    }

    public void connectMongo() {
        if (!isMongo()) return;

        ConfigTracker configTracker = new ConfigTracker(databaseConfig, "mongo");
        MongoSettings mongoSettings = new MongoSettings(
                configTracker.getString("host"),
                configTracker.getInt("port"),
                configTracker.getBoolean("auth.enabled"),
                configTracker.getString("auth.username"),
                configTracker.getString("database"),
                configTracker.getString("auth.password"),
                configTracker.getString("auth.loginDatabase"));

        mongoDatabase = new MongoDatabase(mongoSettings);
    }

    private void setupTasks() {
        new MenuTask();
        //    new CosmeticsTask().runTaskTimerAsynchronously(this, 0L, 1L);
        new AnnounceTask(this).runTaskTimerAsynchronously(this, 100L, announcesConfig.getInt("period-time") * 60L * 20L);

        bossbarHandler.start();
    }

    private void loadPlayers() {
        Common.getOnlinePlayers().forEach(player -> profileHandler.load(player.getUniqueId(), player.getName()).updatePermissions(player));
    }

    private void registerBungee() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
    }

    private void setupListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new AddressListener(), this);
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new SecurityListener(), this);
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new TrailsListener(), this);
        pm.registerEvents(new SignListener(), this);
        pm.registerEvents(new MotdListener(), this);
        pm.registerEvents(new ServerListener(this), this);
        pm.registerEvents(new FreezeListener(), this);

        if (pm.isPluginEnabled("Log4JExploitFix")) {
            pm.registerEvents(new ExploitListener(), this);
        }
    }

    @Override
    public void onDisable() {

        this.savePlayers();
        this.savePrefixes();
        this.saveRanks();
        // this.saveAlts();

        this.pollHandler.shutdown();
        this.networkHandler.shutdown();
        this.redisDatabase.shutdown();
        this.bossbarHandler.stop();
        this.serverHandler.shutdown();

        if (!Spotify.SERVER_NAME.contains("hub")) {
            Bukkit.getOnlinePlayers().forEach(player -> Common.joinServer(player, "na-hub-01"));
        }

        instance = null;
    }

    private void saveAlts() {
        altHandler.save();
    }

    private void savePlayers() {
        Common.getOnlinePlayers().forEach(player -> {
            Profile profile = profileHandler.getProfiles().get(player.getUniqueId());
            if (profile.isDisguised()) {
                profile.unDisguise(true);
            }
            profileHandler.save(profile);
        });
    }

    private void saveRanks() {
        // When changing to network should add redis networking so it doesn't get overrided by other server's save.
        rankHandler.getRanks().stream().filter(Rank::isChanged).forEach(rankHandler::save);
    }

    private void savePrefixes() {
        tagsHandler.getTags().stream().filter(Tag::isModified).forEach(tagsHandler::save);
    }

    private String getServerName() {
        return SERVER_NAME;
    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
