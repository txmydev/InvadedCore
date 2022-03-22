package invaded.cc.core.event;

import invaded.cc.core.event.base.BaseEvent;
import invaded.cc.core.settings.SettingsOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor @Getter
public class PlayerChangeSettingEvent extends BaseEvent {

    private final Player player;
    private final SettingsOption setting;

}
