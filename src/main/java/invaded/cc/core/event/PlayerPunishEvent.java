package invaded.cc.core.event;

import invaded.cc.core.event.base.BaseEvent;
import invaded.cc.core.punishment.Punishment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.OfflinePlayer;

@AllArgsConstructor @Getter
public class PlayerPunishEvent extends BaseEvent {

    private final String executor;
    private final OfflinePlayer target;
    private final Punishment punishment;

    @Setter
    private boolean cancelled;

}
