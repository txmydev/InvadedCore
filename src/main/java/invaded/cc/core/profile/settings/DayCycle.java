package invaded.cc.core.profile.settings;

import lombok.Getter;

public enum DayCycle {

    DAY("NIGHT"),NIGHT("EVENING"),EVENING("DAY");

    private final String next;

    DayCycle(String next) {
        this.next = next;
    }

    public DayCycle getNext(){
        return DayCycle.valueOf(next);
    }
}
