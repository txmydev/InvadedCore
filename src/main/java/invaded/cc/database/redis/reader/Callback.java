package invaded.cc.database.redis.reader;

import invaded.cc.Spotify;
import invaded.cc.profile.ProfileHandler;

public interface Callback<V> {

    ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

    void callback(V jsonObject);

}
