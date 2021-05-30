package invaded.cc.listener;

import invaded.cc.Spotify;
import invaded.cc.event.PlayerDisguiseEvent;
import invaded.cc.event.PlayerPunishEvent;
import invaded.cc.injector.PermissibleInjector;
import invaded.cc.manager.DisguiseHandler;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.Punishment;
import invaded.cc.punishment.PunishmentHandler;
import invaded.cc.tasks.CheckPremiumTask;
import invaded.cc.util.*;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        if (profileHandler.getDeletingPrefix().contains(uuid)) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cAn admin has ran a command to delete all the prefixes from users, \nyou cannot enter right now."));
            return;
        }

        Profile profile = profileHandler.load(uuid, name);

        if (!profile.isLoaded() || profileHandler.getProfile(uuid) == null) {
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cYou data hasn't been loaded."));

            profileHandler.getProfiles().remove(uuid);
            return;
        }

        if (profile.isBanned()) {
            Punishment ban = profile.getBan();

            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_BANNED);
            event.setKickMessage(Common.getDisallowedReason(ban));
        }


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLogin(PlayerLoginEvent event) {
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Player player = event.getPlayer();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (profile == null || !profile.isLoaded()) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cYou data hasn't been loaded."));
            return;
        }

        if (!profile.getName().equals(player.getName())) profile.setName(player.getName());
        profile.updatePermissions(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Player player = event.getPlayer();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (profile == null || !profile.isLoaded()) {
            player.kickPlayer(Color.translate("&cYou data hasn't been loaded."));
            return;
        }

        setProperties(player, profile);

        if (Permission.test(player, PermLevel.STAFF)) {
            Common.broadcastMessage(PermLevel.STAFF
                    , "&9[Staff] " + profile.getColoredName()
                            + " &ajoined &bthe network.");
        }
    }

    private void setProperties(Player player, Profile profile) {
        try {
            GameProfile gameProfile = ((CraftPlayer) player).getProfile();
            Property property = gameProfile.getProperties().get("textures") == null ? null : gameProfile.getProperties().get("textures").iterator().next();
            if (property != null) profile.setRealSkin(new Skin(property.getValue(), property.getSignature()));

            profile.setRealProfile(gameProfile);
        } catch (Exception ignored) {
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfiles().get(player.getUniqueId());
        if (profile == null) return;

        if (profile.isDisguised()) profile.unDisguise();

        if (Permission.test(profile, PermLevel.STAFF) || Permission.test(profile, PermLevel.ADMIN)) {
            Common.broadcastMessage(PermLevel.STAFF
                    , "&9[Staff] " + profile.getColoredName()
                            + " &cleft &bthe network.");
        }


        PermissibleInjector.unInject(player);
        profileHandler.save(profile);
        profileHandler.getProfiles().remove(player.getUniqueId());
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChatAndMuted(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (profile.isMuted()) {
            event.setCancelled(true);

            String format = DateUtils.formatTime(profile.getMute().getExpire() - System.currentTimeMillis());

            if (profile.getMute().getExpire() == -1L)
                player.sendMessage(Color.translate("&cYou've been permanently muted."));
            else player.sendMessage(Color.translate("&cYou have been temporarily muted for " + format));
        }
    }

    @EventHandler
    public void onPlayerPerformCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        String message = event.getMessage();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (!profile.getCommandCooldown().hasExpired() && !event.isCancelled() && !Permission.test(player, PermLevel.STAFF)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cYou are on command cooldown, please wait " + profile.getCommandCooldown().getTimeLeft() + " seconds."));
            return;
        }

        if (Filter.isBlocked(message) && !Permission.test(player, PermLevel.ADMIN)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cThat command is blocked."));
        }

        profile.setCommandCooldown(new Cooldown(Spotify.getInstance().getChatHandler().getCommandTime() * 1000L));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event) {
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        Player player = event.getPlayer();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (!Spotify.getInstance().getChatHandler().isChatValue() && !Permission.test(player, PermLevel.STAFF)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cPublic chat is currently muted."));
            return;
        }

        if (Filter.needFilter(event.getMessage())) {
            String filter = Filter.PREFIX + " " + profile.getColoredName() + "&e: " + event.getMessage();
            Common.broadcastMessage(PermLevel.STAFF, filter);
        }

        String format = profile.getChatFormat() + "&f: ";
        event.setFormat(Color.translate(format + "%2$s"));
    }

    @EventHandler
    public void onChatAndInCooldown(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (!profile.getChatCooldown().hasExpired() && !Permission.test(player, PermLevel.VIP)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cPublic chat is slowed down, please wait " + profile.getChatCooldown().getTimeLeft() + " seconds before talking again or &7buy a rank at &7https://store.invaded.cc"));
            return;
        }

        profile.setChatCooldown(new Cooldown(Spotify.getInstance().getChatHandler().getSlowTime() * 1000L));
    }

    @EventHandler(ignoreCancelled = true)
    public void onDisguise(PlayerDisguiseEvent event) {
        if (!Spotify.getInstance().getServerName().equalsIgnoreCase(event.getServer())) return;

        Player player = event.getPlayer();

        Task.async(() -> {
            player.sendMessage(Color.translate("&6If you disguise as a famous player or with an offensive name, your rank will be removed."));
            if (!CheckPremiumTask.runCheck(event.getFakeName())) return;
            player.sendMessage(Color.translate("&6You are disguising as a real player, if he enters, you will be kicked. "));
        });
    }

    @EventHandler
    public void onStaffChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player);

        if (profile.isStaffChat()) {
            event.setCancelled(true);
            ChatColor prefixColor = ChatColor.GRAY;
            ChatColor messageColor = ChatColor.LIGHT_PURPLE;
            Common.broadcastMessage(PermLevel.STAFF, prefixColor + "[UHC-1] " + profile.getRealColoredName() + "&7: " + messageColor + event.getMessage());
        }
    }

    @EventHandler
    public void onPunish(PlayerPunishEvent event) {
        Punishment punishment = event.getPunishment();

        PunishmentHandler punishmentHandler = Spotify.getInstance().getPunishmentHandler();
        punishmentHandler.punish(event.getTarget().getUniqueId(), event.getTarget().getName(), punishment);
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onJoinDisguised(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (DisguiseHandler.getDisguisedPlayers().containsKey(player.getUniqueId())) {
            String[] info = DisguiseHandler.getDisguisedPlayers().get(player.getUniqueId()).split(";");

            profile.setFakeName(info[0]);
            profile.setFakeRank(Spotify.getInstance().getRankHandler().getRank(info[1]));
            profile.setFakeSkin(new Skin(info[2], info[3]));

            profile.disguise();
        }
    }

}
