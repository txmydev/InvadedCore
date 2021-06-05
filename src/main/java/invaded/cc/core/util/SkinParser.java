package invaded.cc.core.util;

public class SkinParser {

    public static String parse(Skin skin) {
        return skin.getTexture() + ";" + skin.getSignature();
    }

    public static Skin unParse(String[] data) {
        return new Skin(data[0], data[1]);
    }

}
