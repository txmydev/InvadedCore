package invaded.cc.tags;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor @Getter @Setter
public class Prefix implements Tag {

    private String id, display;
    private int price;
    private boolean suffix;

    public Prefix(String id, String display, int price){
        this.id = id;
        this.display = display;
        this.price = price;
        this.suffix = false;
    }

}
