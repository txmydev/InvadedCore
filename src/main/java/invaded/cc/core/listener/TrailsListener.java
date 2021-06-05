package invaded.cc.core.listener;

import invaded.cc.core.profile.Profile;
import invaded.cc.core.trails.Trail;
import invaded.cc.core.Spotify;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.Arrays;

public class TrailsListener implements Listener {

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile proj = event.getEntity();
        if(!(proj.getShooter() instanceof Player)) return;
        if(!Arrays.asList(EntityType.ARROW, EntityType.FISHING_HOOK).contains(proj.getType())) return;

        Player player = (Player) proj.getShooter();
        Profile profile = Spotify.getInstance().getProfileHandler().getProfile(player);

        if(profile.getActiveTrail() == null) return;

        Trail.getToDisplay().put(proj, profile.getActiveTrail());
    }


}
