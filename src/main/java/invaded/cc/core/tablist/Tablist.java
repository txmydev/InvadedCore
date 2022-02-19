package invaded.cc.core.tablist;

import invaded.cc.core.util.Common;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class Tablist {

    private final Player player;
    private String[] names = new String[61];

    public void setup() {
        Bukkit.getOnlinePlayers().forEach(other -> Common.sendPacket(player, PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) other).getHandle())));

        IntStream.rangeClosed(0, 19).forEach(y -> {
            IntStream.range(0, 3).forEach(x -> {
                create(y * 3 + x);
            });
        });
    }

    private void create(int index) {
        String name = getTeamName(index);

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);
        packet.username = profile.getName();
        packet.action = 1;
        packet.player = profile;
        packet.gamemode = -1;
        packet.ping = 2;

        sendPacket(packet);
        sendPacket(getScoreboardPacket(name, "", "", name, 0));

        packet = new PacketPlayOutPlayerInfo();
        packet.action = 2;
        packet.username = profile.getName();
        packet.player = profile;
        packet.ping = 2;

        sendPacket(packet);

        names[index] = name;
    }

    public void set(int index, String line){
        if(player == null) return;
        if(names[index] != null && names[index].equalsIgnoreCase(line)) return;

        names[index] = line;
        update(index, line);
    }

    private void update(int index, String line) {
        String name = getTeamName(index);
        if(line.length() > 16) {
            int lastIndex = line.charAt(15) == ChatColor.COLOR_CHAR ? 15 : 16;
            String prefix = line.substring(0, lastIndex);
            String suffix = ChatColor.getLastColors(prefix) + line.substring(lastIndex);

            sendPacket(getScoreboardPacket(name, prefix, suffix, null, 2));
        } else sendPacket(getScoreboardPacket(name, line, "", null, 2));
    }


    private PacketPlayOutScoreboardTeam getScoreboardPacket(String name, String prefix, String suffix, String member, int action) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        packet.a = name;
        packet.b = name;
        packet.c = prefix;
        packet.d = suffix;
        packet.f = action;
        packet.g = 3;

        if(action == 0) packet.e.add(member);
        return packet;
    }

    private String getTeamName(int index) {
        return ChatColor.values()[index / 10].toString() + ChatColor.values()[index % 10].toString() + ChatColor.RESET.toString();
    }

    private void sendPacket(Packet packet) {
        Common.sendPacket(player, packet);
    }
}
