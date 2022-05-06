package invaded.cc.core.manager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import invaded.cc.core.Spotify;
import invaded.cc.core.event.PlayerDisguiseEvent;
import invaded.cc.core.event.PlayerUnDisguiseEvent;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.Skin;
import invaded.cc.core.util.Task;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Getter
public class DisguiseHandler {

    private final Map<UUID, String> disguisedPlayers = new HashMap<>();
    private final SkinHandler skinManager;

    public DisguiseHandler() {
        skinManager = new SkinHandler();
    }

    public Map<UUID, String> getDisguisedPlayers() {
        return disguisedPlayers;
    }

    public List<Rank> getAvailableDisguiseRanks(Profile profile) {
        return Spotify.getInstance().getRankHandler().getRanks().stream().filter(rank -> rank.getPriority() <= profile.getHighestRank().getPriority() && !rank.isMedia()).collect(Collectors.toList());
    }

    public void undisguise(Profile profile, boolean quit) {
        Player player = Bukkit.getPlayer(profile.getId());
        if (player == null) return;

        String fakeName = profile.getFakeName();
        GameProfile gameProfile = profile.getRealProfile();

        sendPackets(player, gameProfile, profile.getRealSkin());

        player.setPlayerListName(gameProfile.getName());

        sendRespawnPacket(player, (n) -> {
            PlayerUnDisguiseEvent event = new PlayerUnDisguiseEvent(profile, Spotify.SERVER_NAME, fakeName, quit);
            event.call();
        });


    }

    public void sendPackets(Player player, GameProfile gameProfile, Skin skin) {
        this.sendPackets(player, gameProfile, skin, true);
    }


    public void sendPackets(Player player, GameProfile gameProfile, Skin skin, boolean changeProfile) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

        PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(entityPlayer.getId());
        PacketPlayOutPlayerInfo remove = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, entityPlayer);

        Common.getOnlinePlayers().forEach(other -> {
            Common.sendPacket(other, destroy);
            Common.sendPacket(other, remove);
        });

        if (changeProfile) Common.modifyField("bH", entityPlayer, gameProfile, true);

        gameProfile.getProperties().clear();
        gameProfile.getProperties().put("textures", new Property("textures", skin.getTexture(), skin.getSignature()));

        PacketPlayOutPlayerInfo addPlayer = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, entityPlayer);
        PacketPlayOutNamedEntitySpawn spawn = new PacketPlayOutNamedEntitySpawn(entityPlayer);

        Common.getOnlinePlayers().forEach(other -> {
            Common.sendPacket(other, addPlayer);
            if (!other.getUniqueId().equals(player.getUniqueId())) Common.sendPacket(other, spawn);
        });
    }

    public void disguise(Profile profile) {
        Player player = Bukkit.getPlayer(profile.getId());
        if (player == null) return;

        String name = profile.getFakeName();

        profile.setFakeProfile(new GameProfile(profile.getId(), name));
        GameProfile gameProfile = profile.getFakeProfile();

        sendPackets(player, gameProfile, profile.getFakeSkin());

        player.setPlayerListName(name);

        sendRespawnPacket(player, (none) -> {
            PlayerDisguiseEvent event = new PlayerDisguiseEvent(Spotify.SERVER_NAME, player, profile.getFakeName(), profile.getName(), profile.getFakeSkin(), profile.getFakeRank());
            event.call();

        });

    }

    public void sendRespawnPacket(Player player, Consumer<?> onFinish) {
        ItemStack[] contents = player.getInventory().getContents();
        ItemStack[] armor = player.getInventory().getArmorContents();
        double health = player.getHealth();
        int foodLevel = player.getFoodLevel();
        Location location = player.getLocation();
        int heldSlot = player.getInventory().getHeldItemSlot();
        float exp = player.getExp();

        Task.later(() -> {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();

            PacketPlayOutRespawn respawn = new PacketPlayOutRespawn(entityPlayer.world.getWorld().getEnvironment().getId(), entityPlayer.server.getDifficulty(), entityPlayer.world.worldData.getType(), entityPlayer.playerInteractManager.getGameMode());
            Common.sendPacket(player, respawn);

            player.setExp(exp);
            player.setHealth(health);
            player.setFoodLevel(foodLevel);

            PlayerInventory inventory = player.getInventory();
            inventory.setHeldItemSlot(heldSlot);
            inventory.setContents(contents);
            inventory.setArmorContents(armor);

            ((CraftPlayer) player).updateScaledHealth();
            player.updateInventory();

            player.teleport(location);
            onFinish.accept(null);
        }, 5L);


        player.setPlayerListName(player.getName());
    }


}
