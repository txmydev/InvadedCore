package invaded.cc.core.tasks;

import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class CheckPremiumTask implements Runnable {

    private static final JsonParser PARSER = new JsonParser();

    public static boolean runCheck(String name){
        CheckPremiumTask checkPremiumTask = new CheckPremiumTask(name);
        checkPremiumTask.run();
        return checkPremiumTask.result;
    }

    private final String name;

    @Getter
    private boolean result;

    public CheckPremiumTask(String name){
        this.name = name;
    }

    @Override
    public void run() {
        String link = "https://api.mojang.com/users/profiles/minecraft/" + name;

        try {
            URL url = new URL(link);
            PARSER.parse(new InputStreamReader(url.openStream())).getAsJsonObject();
            result = true;
        }catch(IllegalStateException ex) {
            result = false;
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }
}
