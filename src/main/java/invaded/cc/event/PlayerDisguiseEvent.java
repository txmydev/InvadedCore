package invaded.cc.event;

import invaded.cc.event.base.BaseEvent;
import invaded.cc.rank.Rank;
import invaded.cc.util.Skin;
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
