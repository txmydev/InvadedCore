package invaded.cc.core;

import invaded.cc.core.grant.GrantHandler;
import invaded.cc.core.listener.PlayerListener;
import invaded.cc.core.listener.SecurityListener;
import invaded.cc.core.listener.SignListener;
import invaded.cc.core.listener.TrailsListener;
import invaded.cc.core.manager.ChatHandler;
import invaded.cc.core.manager.CommandHandler;
import invaded.cc.core.manager.CosmeticsHandler;
import invaded.cc.core.manager.DisguiseHandler;
import invaded.cc.core.permission.PermissionHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.PunishmentHandler;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.rank.RankHandler;
import invaded.cc.core.tags.TagsHandler;
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

@Getter
public class Spotify extends JavaPlugin {

    public static Gson GSON = new GsonBuilder().disableHtmlEscaping().create();

    @Getter
    @Setter
    private static API API;

    @Getter
    private static Spotify instance;

    private ConfigFile mainConfig, databaseConfig;

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

    @Override
    public void onEnable() {
        instance = this;

        this.mainConfig = new ConfigFile("config.yml", null, false);
        this.databaseConfig = new ConfigFile("database.yml", null, false);

        this.setupHandlers();

        this.setupTasks();
        this.setupListeners();

        this.registerBungee();
        this.loadPlayers();

        this.setupWorlds();
        this.sendMessage();

        setAPI(new API(this));
    }

    private void sendMessage() {
        Bukkit.getConsoleSender().sendMessage(Color.translate("&7[&aSpotify&7] &eWe now playin' music round the server LOL"));
    }

    private void setupWorlds() {
        this.commandHandler.getFlyWorlds().add("world");
    }

    private void setupHandlers() {
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
    }

    private void setupTasks() {
        new MenuTask();
        new CosmeticsTask().runTaskTimerAsynchronously(this, 0L, 1L);
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
    }

    @Override
    public void onDisable() {
        this.savePlayers();
        this.savePrefixes();
        this.saveRanks();

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

    public String getServerName() {
        return mainConfig.getString("server-name");
    }

}
