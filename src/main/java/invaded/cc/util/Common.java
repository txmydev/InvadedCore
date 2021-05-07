package invaded.cc.util;

import com.google.common.base.Strings;
import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.Punishment;
import invaded.cc.util.perms.PermLevel;
import lombok.SneakyThrows;
import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_7_R4.Packet;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Predicate;

public class Common {

    public static Collection<? extends Player> getOnlinePlayers(){
        return Bukkit.getServer().getOnlinePlayers();
    }

    public static List<Player> getPlayersWithFilter(Predicate<Player>... predicate) {
        List<Player> list = new ArrayList<>(getOnlinePlayers());

        for (Predicate<Player> playerPredicate : predicate) {
            for (Player player : list) {
                if(!playerPredicate.test(player)) continue;

                list.add(player);
            }
        }

        return list;
    }

    public static String getDisallowedReason(Punishment punishment) {
        if(punishment.getType().name().contains("MUTE")
        || punishment.getType().name().contains("WARN")) return "?";

        List<String> info = new ArrayList<>();

        if(punishment.getType() == Punishment.Type.TEMPORARY_BAN) {
            long left = punishment.getExpire() - System.currentTimeMillis();

            info.add("&cYour account has been temporary suspended from Invaded Network.");
            info.add("&e");
            info.add("&cThis punishment will be removed in " + DateUtils.formatTime(left));
            info.add("&2");
            info.add("&7You can appeal by going to our teamspeak &binvadednet&7.");

            return StringUtils.join(formatList(info), "\n");
        }else if (punishment.getType() == Punishment.Type.BAN) {
            info.add("&cYour account has been permanently suspended from Invaded Network.");
            info.add("&e");
            info.add("&7You can appeal by going to our teamspeak &binvadednet&7.");

            return StringUtils.join(formatList(info), "\n");
        } else if(punishment.getType() == Punishment.Type.BLACKLIST) {
            info.add("&cYour account has been blacklisted from Invaded Network.");
            info.add("&e");
            info.add("&7You can appeal by going to our teamspeak &binvadednet&7.");

            return StringUtils.join(formatList(info), "\n");
        }

        return "Couldn't fetch information to get this.";
    }

    public static void broadcastMessage(PermLevel permLevel, String message) {
        getOnlinePlayers().forEach(player -> {
            if(!player.hasPermission(permLevel.getPerm())) return;

            player.sendMessage(Color.translate(message));
        });

        Bukkit.getConsoleSender().sendMessage(Color.translate(message));
    }


    public static void broadcastMessage(PermLevel permLevel, TextComponent message) {
        getOnlinePlayers().forEach(player -> {
            if(!player.hasPermission(permLevel.getPerm())) return;

            player.spigot().sendMessage((message));
        });

        Bukkit.getConsoleSender().sendMessage(Color.translate(message.getText()));
    }

    public static void broadcastMessage(PermLevel permLevel, Predicate<Player> predicate, String message){
        broadcastMessage(permLevel, predicate, new TextComponent(Color.translate(message)));
    }

    public static void broadcastMessage(PermLevel permLevel, Predicate<Player> predicate, TextComponent message){
        getOnlinePlayers().stream().filter(predicate).forEach(player -> {
            if(!player.hasPermission(permLevel.getPerm())) return;

            player.spigot().sendMessage(message);
        });

        Bukkit.getConsoleSender().sendMessage(Color.translate(message.getText()));
    }

    @SneakyThrows
    public static void joinServer(Player player, String server) {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        DataOutputStream data = new DataOutputStream(b);

        data.writeUTF("Connect");
        data.writeUTF(server);

        data.close();
        b.close();

        player.sendPluginMessage(Core.getInstance(), "BungeeCord", b.toByteArray());
    }

    public static List<String> formatList(List<String> asList) {
        List<String> list = new ArrayList<>();

        asList.forEach(str -> list.add(Color.translate(str)));

        return list;
    }

    public static String wrapList(List<String> list){
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;

        for(String s : list){
            if(index == list.size() - 1 || index == list.size()) stringBuilder.append(s);
            else stringBuilder.append(s).append(",");

            index++;
        }

        return stringBuilder.toString();
    }

    public static List<String> wrapStringToList(String s){
        String[] split = s.split(",");

        return new ArrayList<>(Arrays.asList(split));
    }

    public static boolean isValidDisguiseName(String arg) {
        if(arg.length() >= 16 || arg.length() < 3) return false;

        List<String> list = new ArrayList<>(Arrays.asList("$", "!", "#", "$", "%", "&", "/", "(", ")", "=", "'", "¿"
                , "?", "¡", "|", "°", "´", "+", "¨", "*", "{", "}", "[", "]", "^", "`", ".", ","
                , ";", ":", "-", "@", "\n"));

        for (String s : list) {
            if(arg.contains(s)) return false;
        }

        return true;
    }

    public static void sendPacket(Player other, Packet packet) {
        ((CraftPlayer) other).getHandle().playerConnection.sendPacket(packet);
    }

    @SneakyThrows
    public static void modifyField(String fieldName, Object object, Object value, boolean superclazz){
        Field field = superclazz ? object.getClass().getSuperclass().getDeclaredField(fieldName) : object.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(object, value);
        field.setAccessible(false);
    }

    public static Map<String, Boolean> convertListToMap(Set<String> list) {
        if(list == null) return new HashMap<>();

        Map<String, Boolean> map = new HashMap<>();
        list.forEach(string -> map.putIfAbsent(string, true));

        return map;
    }

    public static String getLine(int times) {
        return ChatColor.GRAY + "" + ChatColor.STRIKETHROUGH + Strings.repeat("-", times);
    }

    public static Player getPlayer(String arg) {
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

        for (Profile profile : profileHandler.getProfiles().values()) {
            if(profile.isDisguised()) {
                if(arg.equals(profile.getName())) return null;
                if(profile.getFakeName().equals(arg)) return Bukkit.getPlayer(profile.getId());
            }else{
                return Bukkit.getPlayer(arg);
            }
        }

        return null;
    }
}