package invaded.cc.grant;

import invaded.cc.Basic;
import invaded.cc.profile.Profile;
import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

@Getter @Setter
public class Grant {

    public static Comparator<Grant> WEIGHT_COMPARATOR = (g1,g2) -> Basic.getInstance().getRankHandler().getRank(g2.getRank()).getWeight()
            - Basic.getInstance().getRankHandler().getRank(g1.getRank()).getWeight();

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
