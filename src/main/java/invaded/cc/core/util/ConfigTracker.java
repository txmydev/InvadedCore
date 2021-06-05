package invaded.cc.core.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.Set;

public class ConfigTracker {

    private ConfigFile configFile;
    private FileConfiguration config;

    @Setter
    @Getter
    private String path;

    public ConfigTracker(ConfigFile configFile, String path) {
        this.configFile = configFile;
        this.config = configFile.get();
        this.path = path;
    }

    public ConfigTracker(FileConfiguration config, String path) {
        this.config = config;
        this.path = path;
        this.configFile = null;
    }

    public String getString(String path) {
        return Color.translate(config.getString(this.path + "." + path));
    }

    public int getInt(String path) {
        return config.getInt(this.path + "." + path);
    }

    public boolean getBoolean(String path) {
        return config.getBoolean(this.path + "." + path);
    }

    public Set<String> getKeys() {
        return config.getConfigurationSection(this.path).getKeys(false);
    }

    public Set<String> getKeys(String path) {
        return config.getConfigurationSection(this.path + "." + path).getKeys(false);
    }

    public Location getLocation(String path, boolean pitchYaw) {
        try {
            String[] splitted = getString(path).split(",");

            World world = Bukkit.getWorld(splitted[0]);
            double x = Double.parseDouble(splitted[1]);
            double y = Double.parseDouble(splitted[2]);
            double z = Double.parseDouble(splitted[3]);

            float pitch = 0.0f;
            float yaw = 0.0f;

            if (pitchYaw) {
                pitch = Float.parseFloat(splitted[4]);
                yaw = Float.parseFloat(splitted[5]);
            }

            return pitchYaw ? new Location(world, x, y, z, yaw, pitch) : new Location(world, x, y, z);
        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private boolean checkNull() {
        return config == null;
    }

    private boolean checkNull(String path) {
        return config.get(this.path + "." + path) == null;
    }

    public Material getMaterial(String material) {
        return Material.valueOf(config.getString(this.path + "." + material));
    }

    public boolean exists(String path) {
        return config.get(this.path + "." + path) != null;
    }

    public long getLong(String path) {
        return config.getLong(path);
    }

    public List<String> getStringList(String path) {
        return config.getStringList(this.path + "." + path);
    }

    public void set(String s, Object object) {
        if (configFile == null) return;

        config.set(this.path + "." + s, object);
        configFile.save();
    }

    public boolean contains(String s) {
        return config.get(this.path + "." + s) != null;
    }
}
