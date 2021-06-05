package invaded.core.injector;

import invaded.core.Spotify;
import invaded.core.profile.Profile;
import invaded.core.profile.ProfileHandler;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;
import org.bukkit.permissions.Permission;

@Getter
public class InvadedBase extends PermissibleBase {

    private final Player player;
    private final PermissibleBase oldBase;
    private final Profile profile;

    public InvadedBase(Player player, PermissibleBase oldBase) {
        super(player);

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        this.player = player;
        this.oldBase = oldBase;
        this.profile = profileHandler.getProfile(player.getUniqueId());
    }

    @Override
    public boolean hasPermission(String inName) {
        return super.hasPermission(inName) || profile.getPermissions().contains(inName) || profile.getPermissions().contains("*");
    }

    @Override
    public boolean hasPermission(Permission perm) {
        return super.hasPermission(perm) || profile.getPermissions().contains(perm.getName()) || profile.getPermissions().contains("*");
    }
}
