package invaded.cc.core.manager;

import com.google.common.collect.Maps;
import invaded.cc.core.Spotify;
import invaded.cc.core.util.ConfigFile;
import invaded.cc.core.util.ConfigTracker;
import invaded.cc.core.util.Skin;
import invaded.cc.core.util.Task;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

@Getter
public class SkinHandler {

    private final ConfigFile skinsFile;
    private Map<String, Skin> skins;

    public SkinHandler() {
        skinsFile = new ConfigFile("nicks.yml", null, false);
        skins = new HashMap<>();

        Spotify.getInstance().getRedisDatabase().executeCommand((jedis) -> {
            if(!jedis.hexists("server-" + Spotify.SERVER_NAME, "lastRestart")) {
                System.out.println("lastRestart field doesn't exist");
                this.setupSkins();
                return null;
            }

            if(System.currentTimeMillis() -
                    Long.parseLong(jedis.hget("server-"+Spotify.SERVER_NAME, "lastRestart")) > 50000) {
                setupSkins();
            } else {
                attemptFromCache();
            }

            return null;
        });
    }

    private boolean attemptFromCache() {
        return Spotify.getInstance().getRedisDatabase().executeCommand(jedis -> {
            if(!jedis.exists("skins")) return false;

            this.skins = stringToMap(jedis.get("skins"));
            return true;
        });
    }

    private void cache() {
        Spotify.getInstance().getRedisDatabase().executeCommand((jedis) -> {
            String skinsToString = mapToString(this.skins);
            jedis.set("skins", skinsToString);
            return null;
        });
    }

    private Map<String, Skin> stringToMap(String s){
        Map<String, Skin> skins = Maps.newHashMap();

        String[] global = s.split(";");
        for (String s1 : global) {
            String[] entrySplit = s1.split(",");
            String name = entrySplit[0];
            if(entrySplit.length < 2) continue;
            String[] skinSplit = entrySplit[1].split(":");
            Skin skin = new Skin(skinSplit[0], skinSplit[1]);

            skins.put(name, skin);
        }

        return skins;
    }

    private String mapToString(Map<String, Skin> map) {
        StringBuilder builder = new StringBuilder();
        map.forEach((key, value) -> builder.append(key).append(",").append(value.toString()).append(";"));
        return builder.toString();
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

            this.cache();
        });
    }

    public Skin fetchSkinRaw(String username) throws IOException, IllegalStateException {
        String link1 = "https://api.mojang.com/users/profiles/minecraft/" + username;
        String link2 = "https://sessionserver.mojang.com/session/minecraft/profile/";

        Skin skin = null;

        String id = null;

        if(Bukkit.getPlayer(username) == null) {
            URL url1 = new URL(link1);
            InputStreamReader reader1 = new InputStreamReader(url1.openStream());
            try{
                id = new JsonParser().parse(reader1).getAsJsonObject().get("id").getAsString();
            } catch (IllegalStateException ex) {
                return Skin.STEVE_SKIN;
            }
        } else {
            id = Bukkit.getPlayer(username).getUniqueId().toString().replace("-", "");
        }
        URL url2 = new URL(link2 + id + "?unsigned=false");
        InputStreamReader reader2 = new InputStreamReader(url2.openStream());
        JsonObject jsonObject = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

        skin = new Skin(jsonObject.get("value").getAsString(), jsonObject.get("signature").getAsString());

        return skin;
    }

    public Skin fetchSkin(String username) {
        try {
            return this.fetchSkinRaw(username);
        }catch( IllegalStateException ignored){
        }catch(IOException ex) {
            return null;
        }
/*
        String link1 = "https://api.mojang.com/users/profiles/minecraft/" + username;
        String link2 = "https://sessionserver.mojang.com/session/minecraft/profile/";

        Skin skin = null;

        try {
            String id = null;

            if(Bukkit.getPlayer(username) == null) {
                URL url1 = new URL(link1);
                InputStreamReader reader1 = new InputStreamReader(url1.openStream());
                id = new JsonParser().parse(reader1).getAsJsonObject().get("id").getAsString();
            } else {
                id = Bukkit.getPlayer(username).getUniqueId().toString().replace("-", "");
            }

            URL url2 = new URL(link2 + id + "?unsigned=false");
            InputStreamReader reader2 = new InputStreamReader(url2.openStream());
            JsonObject jsonObject = new JsonParser().parse(reader2).getAsJsonObject().get("properties").getAsJsonArray().get(0).getAsJsonObject();

            skin = new Skin(jsonObject.get("value").getAsString(), jsonObject.get("signature").getAsString());
        } catch (IllegalStateException ignored) {
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return skin;*/
        return null;
    }

    public Skin getSkinOf(String display) {
        return skins.get(display);
    }


    public Skin getSkinOf(Player player) {
        CraftPlayer craftPlayer = (CraftPlayer) player;
        GameProfile profile = craftPlayer.getProfile();

        Property property = profile.getProperties().get("textures").stream().findAny().orElse(null);
        if (property == null) {
            System.out.println("Didn't find skin of " + player.getName());
            return null;
        }

        return new Skin(property.getValue(), property.getSignature());
    }

    public void applySkin(Player player, Skin skin) {
        Spotify plugin = Spotify.getInstance();

        System.out.println(skin);

        plugin.getDisguiseHandler().sendPackets(player, ((CraftPlayer) player).getProfile(), skin, false);
        plugin.getDisguiseHandler().sendRespawnPacket(player, (none) -> {});
    }
}
