package invaded.cc.core.event;

import invaded.cc.core.event.base.BaseEvent;
import invaded.cc.core.profile.Profile;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PlayerUnDisguiseEvent extends BaseEvent {

    private final Profile profile;
    private final String server;
    private final String disguisedName;
    private final boolean quit;

}
