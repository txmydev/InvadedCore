package invaded.cc.core.database.redis.reader;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.ProfileHandler;

public interface Callback<V> {

    ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

    void callback(V jsonObject);

}
