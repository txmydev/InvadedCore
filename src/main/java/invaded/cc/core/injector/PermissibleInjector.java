package invaded.cc.core.injector;

import lombok.SneakyThrows;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissibleBase;

import java.lang.reflect.Field;
import java.util.List;

public class PermissibleInjector {

    private static Field PERMISSIBLE_BASE_FIELD;

    public static void inject(Player player) {
        try {
            if (PERMISSIBLE_BASE_FIELD == null) {
                PERMISSIBLE_BASE_FIELD = player.getClass().getSuperclass().getDeclaredField("perm");
                PERMISSIBLE_BASE_FIELD.setAccessible(true);
            }

            PermissibleBase oldPerm = (PermissibleBase) PERMISSIBLE_BASE_FIELD.get(player);
            InvadedBase newPerm = new InvadedBase(player, oldPerm);

            copy(oldPerm, newPerm);

            PERMISSIBLE_BASE_FIELD.set(player, newPerm);
            newPerm.recalculatePermissions();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SneakyThrows
    private static void copy(PermissibleBase old, PermissibleBase newPerm) {
        Field attachmentField = PermissibleBase.class.getDeclaredField("attachments");
        attachmentField.setAccessible(true);
        List<Object> attachmentPerms = (List<Object>) attachmentField.get(newPerm);
        attachmentPerms.clear();
        attachmentPerms.addAll((List) attachmentField.get(old));
        newPerm.recalculatePermissions();
    }

    public static void unInject(Player player) {
        try {
            InvadedBase oldPerm = (InvadedBase) PERMISSIBLE_BASE_FIELD.get(player);
            PermissibleBase oldOldPerm = oldPerm.getOldBase();
            PERMISSIBLE_BASE_FIELD.set(player, oldOldPerm);
            oldOldPerm.recalculatePermissions();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
