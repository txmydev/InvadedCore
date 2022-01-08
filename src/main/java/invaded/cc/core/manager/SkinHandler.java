package invaded.cc.core.manager;

import invaded.cc.core.tasks.CheckPremiumTask;
import invaded.cc.core.util.ConfigFile;
import invaded.cc.core.util.ConfigTracker;
import invaded.cc.core.util.Skin;
import invaded.cc.core.util.Task;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Getter
public class SkinHandler {

    private final ConfigFile skinsFile;
    private final Map<String, Skin> skins;

    public SkinHandler() {
        skinsFile = new ConfigFile("nicks.yml", null, false);
        skins = new HashMap<>();

        setupSkins();
    }

    public void setupSkins() {
        Task.async(() -> {
            ConfigTracker configTracker = new ConfigTracker(skinsFile.get(), "");

            for (String key : configTracker.getStringList("skins")) {
                String[] val = key.split(":");

                if (val.length != 2) {
                    Bukkit.getLogger().info("Error trying to load skin for " + key + " in nicks.yml file, check the syntax.");
                    continue;
                }

                String display = val[0];
                String username = val[1];

                /*if (!CheckPremiumTask.runCheck(username)) {
                    Bukkit.getLogger().info(username + " is not a premium player, cannot extract an skin from him.");
                    continue;
                }*/

                Skin skin = null;
                try {
                    skin = fetchSkinRaw(username);
                }catch(IllegalStateException ex) {
                    Bukkit.getLogger().info(username + " is not a premium player, cannot extract an skin from him.");
                    continue;
                } catch (IOException e) {
                    e.printStackTrace();
                    continue;
                }

                if (skin == null) {
                    Bukkit.getLogger().info("Couldn't fetch skin for username " + username + ", please check the username field is correct.");
                    continue;
                }

                skins.put(display, skin);
            }
        });
    }

    public Skin fetchSkinRaw(String username) throws IOException, IllegalStateException {
        String link1 = "https://api.mojang.com/users/profiles/minecraft/" + username;
        String link2 = "https://sessionserver.mojang.com/session/minecraft/profile/";

        Skin skin = null;

        URL url1 = new URL(link1);
        InputStreamReader reader1 = new InputStreamReader(url1.openStream());
        String id = new JsonParser().parse(reader1).getAsJsonObject().get("id").getAsString();

        URL url2 = new URL(link2 + id + "?unsigned=false");
        InputStreamReader reader2 = new InputStreamReader(url2.openStream());
        JsonObject jsonObject = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

        skin = new Skin(jsonObject.get("value").getAsString(), jsonObject.get("signature").getAsString());

        return skin;
    }

    public Skin fetchSkin(String username) {
        String link1 = "https://api.mojang.com/users/profiles/minecraft/" + username;
        String link2 = "https://sessionserver.mojang.com/session/minecraft/profile/";

        Skin skin = null;

        try {
            URL url1 = new URL(link1);
            InputStreamReader reader1 = new InputStreamReader(url1.openStream());
            String id = new JsonParser().parse(reader1).getAsJsonObject().get("id").getAsString();

            URL url2 = new URL(link2 + id + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject jsonObject = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            skin = new Skin(jsonObject.get("value").getAsString(), jsonObject.get("signature").getAsString());
        } catch (IllegalStateException ignored) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return skin;
    }

    public Skin getSkinOf(String display) {
        return skins.get(display);
    }


}
