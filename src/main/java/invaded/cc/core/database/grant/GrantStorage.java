package invaded.cc.core.database.grant;

import invaded.cc.core.grant.Grant;
import invaded.cc.core.profile.Profile;

import java.util.List;

public interface GrantStorage {

    void updateGrant(Grant grant);

    List<Grant> get(Profile profile);

    void removeGrant(Grant grant);



}
