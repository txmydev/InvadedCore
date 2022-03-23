package invaded.cc.core.tablist;

import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.Common;
import net.minecraft.util.com.mojang.authlib.UserType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WeightTablistAdapter implements TabAdapter{

    @Override
    public void updateTab(Player player, Tablist tab) {
        int index = 0;
        int maxIndex = tab.isLegacy() ? 60 : 80;

        // clear the other ones
        for(int i = index; i < maxIndex; i++) {
            //if(tab.names[i].isEmpty()) tab.setPing(index, -1);
            tab.set(i, "asdasdadad");
        }

        Map<Profile, Integer> indexes = new HashMap<>();
        List<Profile> organized = plugin.getProfileHandler().getProfiles().values().stream()
                .filter(profile -> Common.getPlayer(profile) != null)
                .sorted((p1, p2) -> p2.getCurrentRank().getPriority() - p1.getCurrentRank().getPriority())
                .collect(Collectors.toList());

        for (Profile profile : organized) {
            if(index > maxIndex) break;
            indexes.put(profile, index++);
        }

        indexes.forEach((profile, position) -> {
            tab.set(position, "adfasdada");
            // tab.setPing(position, Common.getPing(profile));
        });


    }
}
