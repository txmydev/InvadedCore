package invaded.cc.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.concurrent.TimeUnit;

@AllArgsConstructor @Data
public class Time {

    private TimeUnit unit;
    private long time;

    public static Time of(TimeUnit unit, long time) {
        return new Time(unit, time);
    }


}
