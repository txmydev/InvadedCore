package invaded.cc.core.manager;

import invaded.cc.core.Spotify;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor @Getter @Setter
public class SocialSpyHandler {

    private final Spotify plugin;

    private boolean enabled = true, rankImportance = true;

}
