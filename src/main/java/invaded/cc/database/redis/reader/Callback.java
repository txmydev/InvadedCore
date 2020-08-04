package invaded.cc.database.redis.reader;

import invaded.cc.Core;
import invaded.cc.profile.ProfileHandler;

public interface Callback<V> {

    ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

    void callback(V jsonObject);

}
