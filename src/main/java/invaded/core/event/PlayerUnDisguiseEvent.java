package invaded.core.event;

import invaded.core.event.base.BaseEvent;
import invaded.core.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor @Getter
public class PlayerUnDisguiseEvent extends BaseEvent {

    private Profile profile;

}
