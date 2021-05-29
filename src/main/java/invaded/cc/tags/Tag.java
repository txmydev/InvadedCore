package invaded.cc.tags;

public interface Tag {

    String getId();

    String getDisplay();

    void setDisplay(String display);

    int getPrice();

    void setPrice(int price);

    boolean isSuffix();

    default String getType() {
        return isSuffix() ? "suffix" : "prefix";
    }

}
