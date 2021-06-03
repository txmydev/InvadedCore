package invaded.cc.manager;

import invaded.cc.Spotify;
import invaded.cc.event.PlayerDisguiseEvent;
import invaded.cc.event.PlayerUnDisguiseEvent;
import invaded.cc.profile.Profile;
import invaded.cc.rank.Rank;
import invaded.cc.tasks.CheckPremiumTask;
import invaded.cc.util.*;
import lombok.Getter;
import net.minecraft.server.v1_7_R4.*;
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
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class DisguiseHandler {

    private SkinManager skinManager;

    public static Map<UUID, String> getDisguisedPlayers() {
        return disguisedPlayers;
    }

    private static final Map<UUID, String> disguisedPlayers = new HashMap<>();

    public DisguiseHandler() {
        skinManager = new SkinManager();
    }

    @Getter
    public class SkinManager {

        private final ConfigFile skinsFile;
        private final Map<String, Skin> skins;

        public SkinManager() {
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

                    if (!CheckPremiumTask.runCheck(username)) {
                        Bukkit.getLogger().info(username + " is not a premium player, cannot extract an skin from him.");
                        continue;
                    }

                    Skin skin = fetchSkin(username);

                    if (skin == null) {
                        Bukkit.getLogger().info("Couldn't fetch skin for username " + username + ", please check the username field is correct.");
                        continue;
                    }

                    skins.put(display, skin);
                }
            });
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
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException ignored) {
            }

            return skin;
        }

        public Skin getSkinOf(String display) {
            return skins.get(display);
        }

    }

    public static List<Rank> getAvailableDisguiseRanks(Profile profile) {
        return Spotify.getInstance().getRankHandler().getRanks().stream().filter(rank -> rank.getPriority() <= profile.getHighestRank().getPriority() && !rank.isMedia()).collect(Collectors.toList());
    }

    public static void undisguise(Profile playerData) {
        Player player = Bukkit.getPlayer(playerData.getId());
        if (player == null) return;

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        Common.getOnlinePlayers().forEach(other -> {
            other.hidePlayer(player);
        });

        GameProfile profile = playerData.getRealProfile();
        player.setPlayerListName(profile.getName());

        Common.modifyField("i", entityPlayer, profile, true);

        player.setPlayerListName(playerData.getName());

        PacketPlayOutPlayerInfo updateDisplayName = PacketPlayOutPlayerInfo.updateDisplayName(entityPlayer);
        updateDisplayName.player = profile;
        updateDisplayName.username = profile.getName();

        player.setPlayerListName(profile.getName());

        Common.getOnlinePlayers().forEach(other -> {
            Common.sendPacket(other,updateDisplayName);
            other.showPlayer(player);
        });

        PlayerUnDisguiseEvent event = new PlayerUnDisguiseEvent(playerData);
        Task.later(event::call, 2L);
    }

    public static void disguise(Profile profile) {
        Player player = Bukkit.getPlayer(profile.getId());
        if (player == null) return;

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        String name = profile.getFakeName();
        String texture = profile.getFakeSkin().getTexture();
        String signature = profile.getFakeSkin().getSignature();

        profile.setFakeProfile(new GameProfile(profile.getId(), name));
        GameProfile gameProfile = profile.getFakeProfile();

        Common.getOnlinePlayers().forEach(other -> {
            other.hidePlayer(player);
        });

        Common.modifyField("i", entityPlayer, gameProfile, true);
        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        PacketPlayOutPlayerInfo updateDisplayName = PacketPlayOutPlayerInfo.updateDisplayName(entityPlayer);
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(entityPlayer.world.getWorld().getEnvironment().getId(),entityPlayer.server.getDifficulty(), entityPlayer.world.worldData.getType(), entityPlayer.playerInteractManager.getGameMode());
        updateDisplayName.player = gameProfile;
        updateDisplayName.username = gameProfile.getName();

        Common.getOnlinePlayers().forEach(other -> {
            Common.sendPacket(other,updateDisplayName);
            other.showPlayer(player);
        });

        player.setPlayerListName(gameProfile.getName());

        Common.sendPacket(player, respawn);

        PlayerDisguiseEvent event = new PlayerDisguiseEvent(Spotify.getInstance().getServerName(), player, profile.getFakeName(), profile.getFakeSkin(), profile.getFakeRank(), false);
        Task.later(event::call, 2L);
    }

}
