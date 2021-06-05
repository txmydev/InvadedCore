package invaded.cc.core.permission;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.Common;
import invaded.cc.core.injector.PermissibleInjector;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PermissionHandler {

    @Getter
    private static final Map<UUID, PermissionAttachment> attachments = new ConcurrentHashMap<>();

    public void updatePermissions(Player player, Map<String, Boolean> permissions) {
        attachments.putIfAbsent(player.getUniqueId(), player.addAttachment(Spotify.getInstance()));
        PermissionAttachment attachment = attachments.get(player.getUniqueId());

        try{
            Common.modifyField("permissions", attachment, permissions, false);
        }catch(Exception ex){
             ex.printStackTrace();
        }

        PermissibleInjector.inject(player);
        player.recalculatePermissions();
    }

}
