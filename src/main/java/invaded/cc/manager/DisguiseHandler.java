package invaded.cc.manager;

import invaded.cc.Core;
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
                        return;
                    }

                    Skin skin = fetchSkin(username);

                    if (skin == null) {
                        Bukkit.getLogger().info("Couldn't fetch skin for username " + username + ", please check the username field is correct.");
                        return;
                    }

                    skins.put(display, skin);
                    Bukkit.getLogger().info("Mapped skin for username " + username + ".");
                }
            });
        }

        private Skin fetchSkin(String username) {
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
            }

            return skin;
        }

        public Skin getSkinOf(String display) {
            return skins.get(display);
        }

    }

    public static List<Rank> getAvailableDisguiseRanks(Player player) {
        List<Rank> list = new ArrayList<>();

        for (Rank rank : Core.getInstance().getRankHandler().getRanks())
            if (player.hasPermission("disguise.use." + rank.getName()) || rank.isDefaultRank()) list.add(rank);

        return list;
    }

    public static void undisguise(Profile playerData) {
        Player player = Bukkit.getPlayer(playerData.getId());
        if (player == null) return;

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        GameProfile profile = playerData.getRealProfile();
        player.setPlayerListName(profile.getName());

        Common.modifyField("i", entityPlayer, profile, true);

        PacketPlayOutPlayerInfo remove =PacketPlayOutPlayerInfo.removePlayer(entityPlayer);
        PacketPlayOutPlayerInfo add = PacketPlayOutPlayerInfo.addPlayer(entityPlayer);

        Common.getOnlinePlayers().forEach(other -> {
            Common.sendPacket(other, remove);
            Common.sendPacket(other, add);
        });

        EntityTrackerEntry entityTracker = (EntityTrackerEntry) (entityPlayer.getWorld()).getWorld().getHandle().getTracker().trackedEntities.get(entityPlayer.getId());

        if (entityTracker == null) return;
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);

        entityTracker.broadcast(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
        entityTracker.broadcast(spawn);
    }

    public static void disguise(Profile profile) {
        Player player = Bukkit.getPlayer(profile.getId());
        if (player == null) return;

        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        PacketPlayOutPlayerInfo remove =PacketPlayOutPlayerInfo.removePlayer(entityPlayer);

        String name = profile.getFakeName();
        String texture = profile.getFakeSkin().getTexture();
        String signature = profile.getFakeSkin().getSignature();

        profile.setFakeProfile(new GameProfile(profile.getId(), name));
        GameProfile gameProfile = profile.getFakeProfile();

        player.setPlayerListName(name);

        Common.modifyField("i", entityPlayer, gameProfile, true);

        gameProfile.getProperties().put("textures", new Property("textures", texture, signature));

        PacketPlayOutPlayerInfo add = PacketPlayOutPlayerInfo.addPlayer(entityPlayer);

        Common.getOnlinePlayers().forEach(other -> {
            Common.sendPacket(other, remove);
            Common.sendPacket(other, add);
        });

        EntityTrackerEntry entityTracker = (EntityTrackerEntry) (entityPlayer.getWorld()).getWorld().getHandle().getTracker().trackedEntities.get(entityPlayer.getId());

        if (entityTracker == null) return;
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);

        entityTracker.broadcast(new PacketPlayOutEntityDestroy(entityPlayer.getId()));
        entityTracker.broadcast(spawn);
    }

}
