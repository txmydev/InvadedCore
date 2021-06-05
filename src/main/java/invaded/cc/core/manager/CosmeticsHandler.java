package invaded.cc.core.manager;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import lombok.Getter;
import lombok.Setter;

public class CosmeticsHandler {

    @Getter
    private static final double TITAN_MULTIPLIER = 1.10, ULTRA_MULTIPLIER = 1.15, LEGEND_MULTIPLIER = 1.25;
    @Getter
    @Setter
    private static double GLOBAL_MULTIPLIER = 0;

    public static double getMultiplier(Profile profile) {
        double finalMultiplier = GLOBAL_MULTIPLIER;
        if (Spotify.getAPI().getRankWeight(profile.getId()) == 9) finalMultiplier = finalMultiplier + TITAN_MULTIPLIER;
        else if (Spotify.getAPI().getRankWeight(profile.getId()) == 10)
            finalMultiplier = finalMultiplier + ULTRA_MULTIPLIER;
        else if (Spotify.getAPI().getRankWeight(profile.getId()) >= 11) finalMultiplier += LEGEND_MULTIPLIER;

        return finalMultiplier;
    }

}
