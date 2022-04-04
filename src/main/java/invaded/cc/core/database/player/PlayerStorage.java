package invaded.cc.core.database.player;

import invaded.cc.core.profile.Profile;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface PlayerStorage {

    Profile load(UUID id, String name, boolean cache);
    Profile load(UUID id, String name);

    Profile getProfile(UUID id);
    Profile getProfile(Player player);
    Profile getProfile(String name, boolean byPassDisguise);
    Profile getProfile(String name);

    List<UUID> getDeletingPrefix();

    void save(Profile profile);
    boolean canDisguise(String arg);

    Map<UUID, Profile> getProfiles();
}
