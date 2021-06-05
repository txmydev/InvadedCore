package invaded.cc.core.util.perms;

import lombok.Getter;

public enum PermLevel {

    DEFAULT("invaded.default"),
    VIP("invaded.vip"),
    MEDIA("invaded.media"),
    STAFF( "invaded.staff"),
    ADMIN("invaded.admin"),
    DEVELOPER("invaded.developer");


    @Getter
    private final String perm;

    PermLevel(String s) {
        this.perm = s;
    }
}
