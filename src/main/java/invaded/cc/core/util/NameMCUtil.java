package invaded.cc.core.util;

import invaded.cc.core.Spotify;
import org.bukkit.entity.Player;

import java.net.URL;
import java.util.Scanner;
import java.util.function.Consumer;

public class NameMCUtil {

    public static void isVerified(Player player, Consumer<Boolean> verified) {
        Task.async(() -> {
            try {
                verified.accept(new Scanner(new URL("https://api.namemc.com/server/play.ploveruhc.com/likes?profile=" + player.getUniqueId().toString()).openStream()).nextBoolean());
            }catch(Exception ex) {
                ex.printStackTrace();
            }
        });
    }

}
