package invaded.cc.core.event;

import invaded.cc.core.event.base.BaseEvent;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.util.Skin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
public class PlayerDisguiseEvent extends BaseEvent {

    private final String server;
    private final Player player;
    private final String fakeName, realName;
    private final Skin fakeSkin;
    private final Rank fakeRankData;
    @Setter
    private boolean cancelled;
}
