package invaded.core.tags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter
public class Tag {

    private String id, display;
    private int price;
    private boolean suffix;

    public String getType() {
        return isSuffix() ? "suffix" : "prefix";
    }

}
