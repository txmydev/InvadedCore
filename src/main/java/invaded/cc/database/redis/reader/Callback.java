package invaded.cc.database.redis.reader;

import invaded.cc.Basic;
import invaded.cc.profile.ProfileHandler;

public interface Callback<V> {

    ProfileHandler profileHandler = Basic.getInstance().getProfileHandler();

    void callback(V jsonObject);

}
