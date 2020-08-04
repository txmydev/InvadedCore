package invaded.cc.event;

import invaded.cc.event.base.BaseEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.UUID;

@AllArgsConstructor @Getter
public class PlayerUnDisguiseEvent extends BaseEvent {

    private UUID uuid;

}
