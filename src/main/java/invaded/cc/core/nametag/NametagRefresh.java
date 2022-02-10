package invaded.cc.core.nametag;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@AllArgsConstructor
public final class NametagRefresh {

    private final Player player, target;
}