package invaded.cc.util;

import invaded.cc.Core;
import lombok.Setter;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ConfigFile {

    private static final JavaPlugin plugin = Core.getInstance();

    private String fileName;
    private File file;
    private String filePath;
    private boolean useEspecifiedPath;

    private FileConfiguration configuration;

    public ConfigFile(String fileName, String path, boolean useEspecifiedPath) {
        this.fileName = fileName;
        this.filePath = path == null ? plugin.getDataFolder().getPath() : path;
        this.useEspecifiedPath = useEspecifiedPath;

        if (!useEspecifiedPath) {
            this.file = new File(plugin.getDataFolder(), fileName);
        } else {
            this.file = new File(filePath, fileName);
        }

        build();
    }

    private void build() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        if (!file.exists())
            plugin.saveResource(fileName, false);

        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration get(){
        return configuration;
    }

    public String getString(String path){
        return Color.translate(configuration.getString(path));
    }

    public int getInt(String path){
        return configuration.getInt(path);
    }

    public boolean getBoolean(String path){
        return configuration.getBoolean(path);
    }

    public long getLong(String path){
        return configuration.getLong(path);
    }

    public Object get(String path){
        return configuration.get(path);
    }

    public float getFloat(String path){
        return (float) configuration.getDouble(path);
    }

    public double getDouble(String path){
        return configuration.getDouble(path);
    }

    @SneakyThrows
    public void save() {
        configuration.save(file);
    }
}
