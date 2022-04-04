package invaded.cc.core.util;

import com.google.common.base.Strings;
import invaded.cc.core.Spotify;
import invaded.cc.core.network.packet.PacketJoinServer;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.punishment.Punishment;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_7_R4.Packet;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;

public class Common {

    public static Collection<? extends Player> getOnlinePlayers() {
        return Bukkit.getServer().getOnlinePlayers();
    }

    public static List<Player> getPlayersWithFilter(Predicate<Player>... predicate) {
        List<Player> list = new ArrayList<>(getOnlinePlayers());

        for (Predicate<Player> playerPredicate : predicate) {
            for (Player player : list) {
                if (!playerPredicate.test(player)) continue;

                list.add(player);
            }
        }

        return list;
    }

    public static ChatColor getRandomColor() {
        ChatColor[] colors = Arrays.asList(ChatColor.AQUA, ChatColor.BLUE, ChatColor.RED, ChatColor.YELLOW, ChatColor.LIGHT_PURPLE).toArray(new ChatColor[0]);

        return colors[ThreadLocalRandom.current().nextInt(colors.length - 1)];
    }

    public static String getDisallowedReason(Punishment punishment) {
        if (punishment.getType().name().contains("MUTE")
                || punishment.getType().name().contains("WARN")) return "?";

        List<String> info = new ArrayList<>();

        if (punishment.getType() == Punishment.Type.TEMPORARY_BAN) {
            long left = punishment.getExpire() - System.currentTimeMillis();

            info.add("&cYour account has been temporary suspended from SkullUHC");
            info.add("&e");
            info.add("&cThis punishment will be removed in " + DateUtils.formatTime(left));
            if(!punishment.getReason().equals("")) info.add("&cYou have been banned for " + punishment.getReason());
            info.add("&2");
            info.add("&7You can appeal by going to our teamspeak &bts.skulluhc.club&7.");

            return StringUtils.join(formatList(info), "\n");
        } else if (punishment.getType() == Punishment.Type.BAN) {
            info.add("&cYour account has been permanently suspended from SkullUHC.");
            info.add("&e");
            if(!punishment.getReason().equals("")) info.add("&cYou have been banned for " + punishment.getReason());
            info.add("&7");
            info.add("&7You can appeal by going to our teamspeak &bts.skulluhc.club&7.");

            return StringUtils.join(formatList(info), "\n");
        } else if (punishment.getType() == Punishment.Type.BLACKLIST) {
            info.add("&cYour account has been blacklisted from SkullUHC.");
            info.add("&e");
            info.add("&7You cannot appeal this punishment.");

            return StringUtils.join(formatList(info), "\n");
        }

        return "Couldn't fetch information to get this.";
    }

    public static void broadcastIf(PermLevel level, String message, Predicate<Profile> predicate) {
        Spotify.getInstance().getProfileHandler().getProfiles()
                .values()
                .stream()
                .filter(profile -> profile.isOnline())
                .filter(predicate)
                .filter(profile -> Permission.test(profile, level))
                .forEach(profile -> profile.sendMessage(message));
    }

    public static void broadcastMessage(PermLevel permLevel, String message) {
        getOnlinePlayers().forEach(player -> {
            if (!Permission.test(player, permLevel)) return;

            player.sendMessage(Color.translate(message));
        });

        Bukkit.getConsoleSender().sendMessage(Color.translate(message));
    }


    public static void broadcastMessage(PermLevel permLevel, TextComponent message) {
        getOnlinePlayers().forEach(player -> {
            if (!Permission.test(player, permLevel)) return;

            player.spigot().sendMessage((message));
        });

        Bukkit.getConsoleSender().sendMessage(Color.translate(message.getText()));
    }

    public static void broadcastMessage(PermLevel permLevel, Predicate<Player> predicate, String message) {
        broadcastMessage(permLevel, predicate, new TextComponent(Color.translate(message)));
    }

    public static void broadcastMessage(PermLevel permLevel, Predicate<Player> predicate, TextComponent message) {
        getOnlinePlayers().stream().filter(predicate).forEach(player -> {
            if (!Permission.test(player, permLevel)) return;

            player.spigot().sendMessage(message);
        });

        Bukkit.getConsoleSender().sendMessage(Color.translate(message.getText()));
    }

    @SneakyThrows
    public static void joinServer(Player player, String server) {
       // Spotify.getInstance().getNetworkHandler().sendPacket(new PacketJoinServer(player.getName(), server));

        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(b);

        data.writeUTF("Connect");
        data.writeUTF(server);

        data.close();
        b.close();

        player.sendPluginMessage(Spotify.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static List<String> formatList(List<String> asList) {
        List<String> list = new ArrayList<>();

        asList.forEach(str -> list.add(Color.translate(str)));

        return list;
    }

    public static String wrapList(List<String> list) {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;

        for (String s : list) {
            if (index == list.size() - 1 || index == list.size()) stringBuilder.append(s);
            else stringBuilder.append(s).append(",");

            index++;
        }

        return stringBuilder.toString();
    }

    public static List<String> wrapStringToList(String s) {
        String[] split = s.split(",");

        return new ArrayList<>(Arrays.asList(split));
    }

    public static boolean validDisguise(String arg) {
        if (arg.length() >= 16 || arg.length() < 3) return false;

        List<String> list = new ArrayList<>(Arrays.asList("$", "!", "#", "$", "%", "&", "/", "(", ")", "=", "'", "¿"
                , "?", "¡", "|", "°", "´", "+", "¨", "*", "{", "}", "[", "]", "^", "`", ".", ","
                , ";", ":", "-", "@", "\n"));


        for (String s : list) {
            if (arg.contains(s)) return false;
        }

        return true;
    }

    public static void sendPacket(Player other, Packet packet) {
        ((CraftPlayer) other).getHandle().playerConnection.sendPacket(packet);
    }

    @SneakyThrows
    public static void modifyField(String fieldName, Object object, Object value, boolean superclazz) {
        Field field = superclazz ? object.getClass().getSuperclass().getDeclaredField(fieldName) : object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    public static Map<String, Boolean> convertListToMap(Set<String> list) {
        if (list == null) return new HashMap<>();

        Map<String, Boolean> map = new HashMap<>();
        list.forEach(string -> map.putIfAbsent(string, true));

        return map;
    }

    public static String getLine(int times) {
        return ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + Strings.repeat("-", times);
    }

    public static String getName(String arg) {
        for (Profile profile : Spotify.getInstance().getProfileHandler().getProfiles().values()) {
            if (profile.getName().equals(arg) || (profile.isDisguised() && profile.getFakeName().equals(arg))) return profile.getName();
        }

        return arg;
    }

    public static Player getPlayer(Profile profile) {
        return Bukkit.getPlayer(profile.getId());
    }

    public static int getPing(Profile profile) {
        return ((CraftPlayer)(Bukkit.getPlayer(profile.getId()))).getHandle().ping;
    }


    public static String imgToBase64String(RenderedImage img, final String formatName) {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        try {
            ImageIO.write(img, formatName, os);
            return Base64.getEncoder().encodeToString(os.toByteArray());
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public static BufferedImage base64StringToImg(final String base64String) {
        try {
            return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder().decode(base64String)));
        } catch (final IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public static int getVersion(Player player) {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion();
    }
}
