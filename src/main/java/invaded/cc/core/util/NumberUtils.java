package invaded.cc.core.util;

public class NumberUtils {

    public static int getInt(String s, Runnable failed) {
        int a;
        try{
            a = Integer.parseInt(s);
        }catch(NumberFormatException ex){
            failed.run();
            a = -100;
        }

        return a;
    }

}
