package invaded.cc.core.tablist;

import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.Common;
import net.minecraft.util.com.mojang.authlib.UserType;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeightTablistAdapter implements TabAdapter{

    @Override
    public void updateTab(Player player, Tablist tab) {
        int index = 0;
        int maxIndex = 60;

        /*
           0 21 41

         */

        Map<Profile, Integer> indexes = new HashMap<>();
        List<Profile> organized = plugin.getProfileHandler().getProfiles().values().stream()
                .filter(profile -> Common.getPlayer(profile) != null)
                .sorted((p1, p2) -> p2.getCurrentRank().getPriority() - p1.getCurrentRank().getPriority())
                .collect(Collectors.toList());

        for (Profile profile : organized) {
            if(index > maxIndex) break;
            indexes.put(profile, index++);
        }

        indexes.forEach((profile, position) -> tab.set(position, profile.getColoredName()));

        // clear the other ones
        for(int i = index; i < maxIndex; i++) {
            tab.set(i, "");
        }
    }
}