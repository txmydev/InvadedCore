package invaded.cc.util.perms;

import lombok.Getter;

public enum PermLevel {

    DEFAULT("invaded.default"),
    VIP("invaded.default", "invaded.vip"),
    MEDIA("invaded.default", "invaded.vip","invaded.media"),
    STAFF( "invaded.default", "invaded.vip","invaded.media","invaded.staff"),
    ADMIN("invaded.default", "invaded.vip","invaded.media","invaded.staff", "invaded.admin"),
    DEVELOPER("invaded.default", "invaded.vip","invaded.media","invaded.staff", "invaded.admin", "invaded.developer");


    @Getter
    private final String[] perm;

    PermLevel(String... s) {
        this.perm = s;
    }
}
