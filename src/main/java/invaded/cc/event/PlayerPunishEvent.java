package invaded.cc.event;

import invaded.cc.event.base.BaseEvent;
import invaded.cc.punishment.Punishment;
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
