package invaded.cc.grant;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter @Setter
public class Grant {

    public static Comparator<Grant> WEIGHT_COMPARATOR = (g1,g2) -> Core.getInstance().getRankHandler().getRank(g2.getRank()).getPriority()
            - Core.getInstance().getRankHandler().getRank(g1.getRank()).getPriority();

    private String rank;
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
        this.addedBy = addedBy;
    }
}
