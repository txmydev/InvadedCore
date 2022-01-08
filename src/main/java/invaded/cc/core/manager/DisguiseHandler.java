package invaded.cc.core.manager;

import invaded.cc.core.Spotify;
import invaded.cc.core.event.PlayerDisguiseEvent;
import invaded.cc.core.event.PlayerUnDisguiseEvent;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.tasks.CheckPremiumTask;
import invaded.cc.core.util.*;
import lombok.Getter;
import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PacketPlayOutRespawn;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
public class DisguiseHandler {

    private static final Map<UUID, String> disguisedPlayers = new HashMap<>();
    private final SkinHandler skinManager;

    public DisguiseHandler() {
        skinManager = new SkinHandler();
    }

    public static Map<UUID, String> getDisguisedPlayers() {
        return disguisedPlayers;
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

        Common.modifyField("i", entityPlayer, profile, true);

        player.setPlayerListName(playerData.getName());

        PacketPlayOutPlayerInfo updateDisplayName = PacketPlayOutPlayerInfo.updateDisplayName(entityPlayer);
        updateDisplayName.player = profile;
        updateDisplayName.username = profile.getName();

        player.setPlayerListName(profile.getName());

        Common.getOnlinePlayers().forEach(other -> {
            Common.sendPacket(other, updateDisplayName);
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
        PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(entityPlayer.world.getWorld().getEnvironment().getId(), entityPlayer.server.getDifficulty(), entityPlayer.world.worldData.getType(), entityPlayer.playerInteractManager.getGameMode());
        updateDisplayName.player = gameProfile;
        updateDisplayName.username = gameProfile.getName();

        Common.getOnlinePlayers().forEach(other -> {
            Common.sendPacket(other, updateDisplayName);
            other.showPlayer(player);
        });

        player.setPlayerListName(gameProfile.getName());

        Common.sendPacket(player, respawn);

        PlayerDisguiseEvent event = new PlayerDisguiseEvent(Spotify.SERVER_NAME, player, profile.getFakeName(), profile.getFakeSkin(), profile.getFakeRank(), false);
        Task.later(event::call, 2L);
    }



}
