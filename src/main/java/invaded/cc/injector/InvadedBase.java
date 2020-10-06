package invaded.cc.injector;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
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

        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
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
