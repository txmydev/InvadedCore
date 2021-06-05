package invaded.core.database.redis.reader;

import invaded.core.Spotify;
import invaded.core.profile.ProfileHandler;

public interface Callback<V> {

    ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

    void callback(V jsonObject);

}
