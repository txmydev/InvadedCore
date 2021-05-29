package invaded.cc.tags;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Suffix implements Tag {

    private String id, display;
    private int price;
    private boolean suffix = true;

    public Suffix(String id, String display, int price) {
        this.id = id;
        this.display = display;
        this.price = price;
    }

}
