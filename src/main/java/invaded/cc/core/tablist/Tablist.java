package invaded.cc.core.tablist;

import invaded.cc.core.util.Common;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.stream.IntStream;

@RequiredArgsConstructor
public class Tablist {

    private final Player player;
    public String[] names;
   // private TabEntry[] entries;

    public void setup() {
        Bukkit.getOnlinePlayers().forEach(other -> Common.sendPacket(player, PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) other).getHandle())));
        names = new String[81];
      //  entries = new TabEntry[isLegacy() ? 61 : 81];

        if(isLegacy()) {
            System.out.println("Setting up legacy tab for " + player.getName());
            setupLegacy();
        }
        else setupNormal();
    }

    private void setupLegacy() {
        IntStream.rangeClosed(1, 20).forEach((y) -> {
            IntStream.range(0, 3).forEach((x) -> {
                create(x * 20 + y);
            });
        });
    }

    private void setupNormal() {
        IntStream.rangeClosed(0, 80).forEach(this::create);
    }

    public boolean isLegacy() {
        return ((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() <= 5;
    }

    private void create(int index) {
        String name = getTeamName(index);

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        GameProfile profile = new GameProfile(UUID.randomUUID(), name);

        if(isLegacy()) {
            packet.username = profile.getName();
            packet.action = 1;
            packet.player = profile;
            packet.gamemode = -1;
        } else {
            packet.username = profile.getName();
            profile.getProperties().removeAll("textures");
            profile.getProperties().put("textures", new Property("textures", "eyJ0aW1lc3RhbXAiOjE0MTEyNjg3OTI3NjUsInByb2ZpbGVJZCI6IjNmYmVjN2RkMGE1ZjQwYmY5ZDExODg1YTU0NTA3MTEyIiwicHJvZmlsZU5hbWUiOiJsYXN0X3VzZXJuYW1lIiwidGV4dHVyZXMiOnsiU0tJTiI6eyJ1cmwiOiJodHRwOi8vdGV4dHVyZXMubWluZWNyYWZ0Lm5ldC90ZXh0dXJlLzg0N2I1Mjc5OTg0NjUxNTRhZDZjMjM4YTFlM2MyZGQzZTMyOTY1MzUyZTNhNjRmMzZlMTZhOTQwNWFiOCJ9fX0=", "u8sG8tlbmiekrfAdQjy4nXIcCfNdnUZzXSx9BE1X5K27NiUvE1dDNIeBBSPdZzQG1kHGijuokuHPdNi/KXHZkQM7OJ4aCu5JiUoOY28uz3wZhW4D+KG3dH4ei5ww2KwvjcqVL7LFKfr/ONU5Hvi7MIIty1eKpoGDYpWj3WjnbN4ye5Zo88I2ZEkP1wBw2eDDN4P3YEDYTumQndcbXFPuRRTntoGdZq3N5EBKfDZxlw4L3pgkcSLU5rWkd5UH4ZUOHAP/VaJ04mpFLsFXzzdU4xNZ5fthCwxwVBNLtHRWO26k/qcVBzvEXtKGFJmxfLGCzXScET/OjUBak/JEkkRG2m+kpmBMgFRNtjyZgQ1w08U6HHnLTiAiio3JswPlW5v56pGWRHQT5XWSkfnrXDalxtSmPnB5LmacpIImKgL8V9wLnWvBzI7SHjlyQbbgd+kUOkLlu7+717ySDEJwsFJekfuR6N/rpcYgNZYrxDwe4w57uDPlwNL6cJPfNUHV7WEbIU1pMgxsxaXe8WSvV87qLsR7H06xocl2C0JFfe2jZR4Zh3k9xzEnfCeFKBgGb4lrOWBu1eDWYgtKV67M2Y+B3W5pjuAjwAxn0waODtEn/3jKPbc/sxbPvljUCw65X+ok0UUN1eOwXV5l2EGzn05t3Yhwq19/GxARg63ISGE8CKw="));
            packet.player = profile;
            packet.action = 0;
        }

        sendPacket(packet);
        sendPacket(getScoreboardPacket(name, "", "", name, 0));

        //TabEntry entry = new TabEntry(profile, index, 0);

        /*packet = new PacketPlayOutPlayerInfo();
        packet.action = 2;
        packet.username = profile.getName();
        packet.player = profile;
        packet.ping = 2;

        sendPacket(packet);*/

        names[index] = name;
        //entries[index] = entry;
    }


    public void setPing(int index, int ping) {
     /*   TabEntry entry = entries[index];
        if(entry == null || ping == entry.ping) return;

        GameProfile player = entry.profile;

        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo();
        packet.player = entry.profile;
        packet.action = 2;
        packet.username = player.getName();
        packet.player = player;
        packet.ping = ping;

        sendPacket(packet);*/
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
