package invaded.cc.core.grant;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter
@Setter
public class Grant {

    public static Comparator<Grant> WEIGHT_COMPARATOR = (g1, g2) -> Spotify.getInstance().getRankHandler().getRank(g2.getRank()).getPriority()
            - Spotify.getInstance().getRankHandler().getRank(g1.getRank()).getPriority();

    private String rank;
    private String name;
    private Profile profile;
    private long addedAt;
    private long removedAt;
    private String addedBy;
    private String removedBy;

    private boolean use = true;

    public Grant(Profile profile, long grantedAt, String rank, String addedBy) {
        this.profile = profile;
        this.addedAt = grantedAt;
        this.rank = rank;
        this.name = profile.getName();
        this.addedBy = addedBy;
    }
}
