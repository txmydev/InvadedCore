package invaded.cc.core.tags;

import lombok.*;

@Data
public class Tag {

    private String id, display;
    private int price;
    private boolean suffix, modified = false;

    public Tag(String id, String display, int price, boolean suffix) {
        this.id = id;
        this.display = display;
        this.price = price;
        this.suffix = suffix;
    }

    public String getType() {
        return isSuffix() ? "suffix" : "prefix";
    }

}
