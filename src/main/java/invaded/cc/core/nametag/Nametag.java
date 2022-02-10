package invaded.cc.core.nametag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.PacketPlayOutScoreboardTeam;
import org.bukkit.entity.Player;

import java.util.Collections;


@Getter
@Setter
@AllArgsConstructor
public final class Nametag {

    private String team;
    private String prefix, suffix, tablistName;
    private boolean visible;

    public Nametag(String team, String prefix, String tablistName) {
        this(team, prefix, "", tablistName, true);
    }

    public Nametag(String team, String prefix) {
        this(team, prefix, prefix);
    }

    public PacketPlayOutScoreboardTeam createTeamPacket() {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        packet.a = team;
        packet.f = 0;
        packet.b = team;
        packet.c = prefix;
        packet.d = suffix;

        if (!visible) {
//            packet.setNameTagVisibility(EnumNameTagVisibility.NEVER);
        }

        packet.g = 1;
        return packet;
    }

    public PacketPlayOutScoreboardTeam addPlayerPacket(Player player) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        packet.a = team;
        packet.f = 3;
        packet.e.addAll(Collections.singletonList(player.getName()));
        return packet;
    }
}