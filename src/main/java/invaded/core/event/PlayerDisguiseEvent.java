package invaded.core.event;

import invaded.core.event.base.BaseEvent;
import invaded.core.rank.Rank;
import invaded.core.util.Skin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@AllArgsConstructor @Getter
public class PlayerDisguiseEvent extends BaseEvent {

    private final String server;
    private final Player player;
    private final String fakeName;
    private final Skin fakeSkin;
    private final Rank fakeRankData;
    @Setter
    private boolean cancelled;
}
