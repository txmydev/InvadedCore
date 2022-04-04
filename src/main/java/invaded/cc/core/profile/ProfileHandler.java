package invaded.cc.core.profile;

import invaded.cc.core.database.player.PlayerStorage;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.util.Color;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
public class ProfileHandler {

    private final PlayerStorage playerStorage;

    private final List<UUID> deletingPrefix;

    public ProfileHandler(PlayerStorage playerStorage) {
        this.playerStorage = playerStorage;

        this.deletingPrefix = new ArrayList<>();
    }

    public Profile load(UUID uuid, String name) {
        return load(uuid, name, true);
    }

    public void save(Profile profile) {
        playerStorage.save(profile);
    }

    public Profile getProfile(UUID uuid) {
        return playerStorage.getProfile(uuid);
    }

    public Profile getProfile(Player player) {
        return getProfile(player.getUniqueId());
    }

    public boolean canDisguise(String arg) {
        return playerStorage.canDisguise(arg);
    }

    public Profile load(UUID uuid, String name, boolean cache) {
        return playerStorage.load(uuid, name, cache);
    }

    public void ifPresent(UUID id, ProfileCallback callback, CommandSender sender, String s) {
        Profile profile = getProfile(id);
        if (profile == null) {
            sender.sendMessage(Color.translate(s));
            return;
        }

        callback.apply(profile);
    }

    public Rank getRank(Player player) {
        return this.getProfile(player).getHighestRank();
    }

    public Map<UUID, Profile> getProfiles() {
        return playerStorage.getProfiles();
    }

    public interface ProfileCallback {

        void apply(Profile profile);

    }
}
