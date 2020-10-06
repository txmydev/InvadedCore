package invaded.cc;

import invaded.cc.database.Database;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.grant.GrantHandler;
import invaded.cc.listener.PlayerListener;
import invaded.cc.listener.SecurityListener;
import invaded.cc.manager.ChatHandler;
import invaded.cc.manager.CommandHandler;
import invaded.cc.manager.DisguiseHandler;
import invaded.cc.manager.ServerHandler;
import invaded.cc.permission.PermissionHandler;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.PunishmentHandler;
import invaded.cc.rank.RankHandler;
import invaded.cc.tasks.GlobalUpdateTask;
import invaded.cc.tasks.MenuTask;
import invaded.cc.tasks.ServerUpdateTask;
import invaded.cc.util.Common;
import invaded.cc.util.ConfigFile;
import invaded.cc.util.menu.MenuListener;
import lombok.Getter;
import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.com.google.gson.GsonBuilder;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class Core extends JavaPlugin {

    public static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Getter
    private static Core instance;

    private ConfigFile mainConfig, databaseConfig, ranksConfig;
    private Database db;

    private CommandHandler commandHandler;
    private ChatHandler chatHandler;
    private ServerHandler serverHandler;
    private DisguiseHandler disguiseHandler;
    private PunishmentHandler punishmentHandler;
    private PermissionHandler permissionHandler;
    private ProfileHandler profileHandler;
    private GrantHandler grantHandler;
    private RankHandler rankHandler;

    @Override
    public void onEnable(){
        instance = this;

        this.mainConfig = new ConfigFile("config.yml", null ,false);
        this.databaseConfig = new ConfigFile("database.yml", null, false);
        this.ranksConfig = new ConfigFile("ranks.yml", null, false);

        db = new Database();

        serverHandler = new ServerHandler();
        commandHandler = new CommandHandler();
        chatHandler = new ChatHandler();
        disguiseHandler = new DisguiseHandler();
        permissionHandler = new PermissionHandler();
        grantHandler = new GrantHandler();
        profileHandler = new ProfileHandler();
        punishmentHandler = new PunishmentHandler();
        rankHandler = new RankHandler();

        new ServerUpdateTask();
        new GlobalUpdateTask().runTaskTimerAsynchronously(this, 0L,15L);
        new MenuTask();

        setupListeners();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        serverHandler.setJoineable(true);

        Common.getOnlinePlayers().forEach(player -> profileHandler.load(player.getUniqueId(), player.getName()).updatePermissions(player));
    }

    private void setupListeners() {
        PluginManager pm = Bukkit.getPluginManager();

        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new SecurityListener(), this);
        pm.registerEvents(new MenuListener(), this);
    }

    @Override
    public void onDisable(){
        Common.getOnlinePlayers().forEach(player -> {
            Profile profile = profileHandler.getProfiles().get(player.getUniqueId());

            if(profile.isDisguised())
                new JedisPoster(JedisAction.UNDISGUISE)
                    .addInfo("profileId", profile.getId().toString())
                    .post();

            profileHandler.save(profile);
        });

        rankHandler.getRanks().forEach(rankHandler::save);
        instance = null;
    }

    public String getServerName(){
        return mainConfig.getString("server-name");
    }

}
