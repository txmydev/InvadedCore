package invaded.cc.core.util;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ExceptionCounter {

    private final int limit;
    private int current = 0;

    public int add() {
        return current++;
    }

    public boolean hasFinished() {
        return current >= limit;
    }

}
