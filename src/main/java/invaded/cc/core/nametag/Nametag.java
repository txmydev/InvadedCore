package invaded.cc.core.nametag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.PacketPlayOutScoreboardTeam;
import net.minecraft.server.v1_8_R3.ScoreboardTeamBase;
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
        packet.name = team;
        packet.action = 0;
        packet.displayName = team;
        packet.prefix = prefix;
        packet.suffix = suffix;

        if (!visible) {
            packet.nameTagVisibility = ScoreboardTeamBase.EnumNameTagVisibility.NEVER.e;
        }

        packet.optionData = 1;
        return packet;
    }

    public PacketPlayOutScoreboardTeam addPlayerPacket(Player player) {
        PacketPlayOutScoreboardTeam packet = new PacketPlayOutScoreboardTeam();
        packet.name = team;
        packet.action = 3;
        packet.playerNames.addAll(Collections.singletonList(player.getName()));
        return packet;
    }
}