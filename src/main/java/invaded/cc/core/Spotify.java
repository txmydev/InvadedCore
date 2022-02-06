package invaded.cc.core;

import invaded.cc.core.bossbar.BossbarHandler;
import invaded.cc.core.database.RedisDatabase;
import invaded.cc.core.grant.GrantHandler;
import invaded.cc.core.listener.*;
import invaded.cc.core.manager.*;
import invaded.cc.core.network.NetworkHandler;
import invaded.cc.core.network.server.ServerHandler;
import invaded.cc.core.permission.PermissionHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.PunishmentHandler;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.rank.RankHandler;
import invaded.cc.core.tags.TagsHandler;
import invaded.cc.core.tasks.AnnounceTask;
import invaded.cc.core.tasks.BossBarThread;
import invaded.cc.core.tasks.CosmeticsTask;
import invaded.cc.core.tasks.MenuTask;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.ConfigFile;
import invaded.cc.core.util.menu.MenuListener;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.TimeZone;

@Getter
public class Spotify extends JavaPlugin {

    public static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    public static String SERVER_NAME;

    @Getter
    @Setter
    private static API API;

    @Getter
    private static Spotify instance;

    private ConfigFile mainConfig, databaseConfig, announcesConfig;

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

    @Override
    public void onEnable() {
        instance = this;

        this.mainConfig = new ConfigFile("config.yml", null, false);
        this.databaseConfig = new ConfigFile("database.yml", null, false);
        this.announcesConfig = new ConfigFile("announces.yml", null, false);

        this.setServerName();

        this.setupRedis();
        this.setupHandlers();

        this.setupTasks();
        this.setupListeners();

        this.registerBungee();
        this.loadPlayers();

        this.setupWorlds();
        this.sendMessage();

        setAPI(new API(this));
    }

    private void setupRedis() {
        this.redisDatabase = new RedisDatabase();
    }

    private void setServerName() {
        SERVER_NAME = mainConfig.getString("server-name");
    }

    private void sendMessage() {
        //Bukkit.getConsoleSender().sendMessage(Color.translate("&7[&aSpotify&7] &eWe now playin' music round the server LOL"));
    }

    private void setupWorlds() {
        this.commandHandler.getFlyWorlds().add("world");
    }

    private void setupHandlers() {
        // TimeZone.setDefault(TimeZone.getTimeZone("America/New_York"));

        commandHandler = new CommandHandler();
        chatHandler = new ChatHandler();
        disguiseHandler = new DisguiseHandler();
        permissionHandler = new PermissionHandler();
        grantHandler = new GrantHandler();
        profileHandler = new ProfileHandler();
        punishmentHandler = new PunishmentHandler();
        rankHandler = new RankHandler();
        tagsHandler = new TagsHandler();
        cosmeticsHandler = new CosmeticsHandler();
        networkHandler = new NetworkHandler(this);
        serverHandler = new ServerHandler();
        socialSpyHandler = new SocialSpyHandler(this);
        bossbarHandler = new BossbarHandler();
    }

    private void setupTasks() {
        new MenuTask();
        new CosmeticsTask().runTaskTimerAsynchronously(this, 0L, 1L);
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

        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new SecurityListener(), this);
        pm.registerEvents(new MenuListener(), this);
        pm.registerEvents(new TrailsListener(), this);
        pm.registerEvents(new SignListener(), this);
        pm.registerEvents(new MotdListener(), this);
    }

    @Override
    public void onDisable() {
        this.savePlayers();
        this.savePrefixes();
        this.saveRanks();
        this.networkHandler.shutdown();
        this.redisDatabase.shutdown();
        this.bossbarHandler.stop();

        instance = null;
    }

    private void savePlayers() {
        Common.getOnlinePlayers().forEach(player -> {
            Profile profile = profileHandler.getProfiles().get(player.getUniqueId());
            if (profile.isDisguised()) {
                profile.unDisguise();
            }
            profileHandler.save(profile);
        });
    }

    private void saveRanks() {
        // When changing to network should add redis networking so it doesn't get overrided by other server's save.
        rankHandler.getRanks().stream().filter(Rank::isChanged).forEach(rankHandler::save);
    }

    private void savePrefixes() {
        tagsHandler.getTags().forEach(tagsHandler::save);
    }

    private String getServerName() {
        return SERVER_NAME;
    }

}
