package invaded.cc.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.UUID;

@Data
public class SkinFetch {


    private Player requester;
    private UUID requesterId;
    private String target;
    private boolean ready, failed;
    private Skin skin;

    public SkinFetch(Player player, String target, boolean ready, Skin skin) {
        this.requester = player;
        this.requesterId = player.getUniqueId();
        this.target = target;
        this.ready = ready;
        this.skin = skin;
    }

    public boolean isDone() {
        return ready || failed;
    }
}
