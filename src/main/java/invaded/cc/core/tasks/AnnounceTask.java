package invaded.cc.core.tasks;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.ConfigFile;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.craftbukkit.libs.joptsimple.internal.Strings;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AnnounceTask extends BukkitRunnable {

    private List<List<String>> messages;
    private int index = 0;

    public AnnounceTask(Spotify plugin) {
        this.messages = new ArrayList<>();

        ConfigFile configFile = plugin.getAnnouncesConfig();
        ConfigurationSection section = configFile.get().getConfigurationSection("announces");
        section.getKeys(false).forEach(key -> messages.add(section.getStringList(key).stream().map(Color::translate).collect(Collectors.toList())));
    }

    @Override
    public void run() {
        checkReset();
        Bukkit.broadcastMessage(Strings.join(messages.get(index++), "\n" + CC.RESET));
    }

    private void checkReset() {
        if(index == messages.size()) index = 0;
    }
}
