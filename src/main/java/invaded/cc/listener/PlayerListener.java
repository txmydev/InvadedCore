package invaded.cc.listener;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import invaded.cc.Core;
import invaded.cc.event.PlayerDisguiseEvent;
import invaded.cc.event.PlayerPunishEvent;
import invaded.cc.injector.PermissibleInjector;
import invaded.cc.manager.DisguiseHandler;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.profile.User;
import invaded.cc.punishment.Punishment;
import invaded.cc.punishment.PunishmentHandler;
import invaded.cc.tasks.CheckPremiumTask;
import invaded.cc.util.*;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

        Profile profile = profileHandler.load(uuid, name);

        if (!profile.isLoaded()) {
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
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Player player = event.getPlayer();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (profile == null) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cYou data hasn't been loaded."));
            return;
        }

        if(!profile.getName().equals(player.getName())) profile.setName(player.getName());
        profile.updatePermissions(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Player player = event.getPlayer();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        try {
            GameProfile gameProfile = ((CraftPlayer) player).getProfile();
            Property property = gameProfile.getProperties().get("textures") == null ? null : gameProfile.getProperties().get("textures").iterator().next();
            if (property != null) profile.setRealSkin(new Skin(property.getValue(), property.getSignature()));

            profile.setRealProfile(gameProfile);
        } catch (Exception ignored) { }

//        if (Permission.test(player, PermLevel.STAFF)) {
//            User user = Core.getInstance().getServerHandler().find(player.getName());
//
//            if(user == null || user.getLastServer().equals(Core.getInstance().getServerName())) {
//                new JedisPoster(JedisAction.STAFF_JOIN)
//                        .addInfo("profileId", profile.getId().toString())
//                        .addInfo("coloredName", profile.getRealColoredName()).post();
//            }else {
//                new JedisPoster(JedisAction.STAFF_SWITCH)
//                        .addInfo("profileId", profile.getId().toString())
//                        .addInfo("coloredName", profile.getRealColoredName())
//                        .addInfo("to", Core.getInstance().getServerName())
//                        .addInfo("from", user.getLastServer())
//                        .post();
//            }
//        }
    }

    @EventHandler (priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfiles().get(player.getUniqueId());

        User globalPlayer = Core.getInstance().getServerHandler().find(player.getName());
        if(globalPlayer != null) globalPlayer.setSwitchingServer(true);

        try {
            PermissibleInjector.unInject(player);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (profile.isDisguised()) profile.unDisguise();

        profileHandler.save(profile);
        profileHandler.getProfiles().remove(player.getUniqueId());
        Core.getInstance().getServerHandler().removePlayer(globalPlayer);
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChatAndMuted(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (profile.isMuted()) {
            event.setCancelled(true);

            String format = DateUtils.formatTime(profile.getMute().getExpire() - System.currentTimeMillis());

            if (profile.getMute().getExpire() == -1L) player.sendMessage(Color.translate("&cYou've been permanently muted."));
            else player.sendMessage(Color.translate("&cYou have been temporarily muted for " + format));
        }
    }

    @EventHandler
    public void onPlayerPerformCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        String message = event.getMessage();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if(!profile.getCommandCooldown().hasExpired() && !event.isCancelled() && !Permission.test(player, PermLevel.STAFF)){
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cYou are on command cooldown, please wait " + profile.getCommandCooldown().getTimeLeft() + " seconds."));
            return;
        }

        if(event.getMessage().startsWith("/bukkit:") && profile.getHighestRank().getPriority() < 100){
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cThat command is blocked."));
            return;
        }

        if (Filter.isBlocked(message) && !player.getName().equalsIgnoreCase("txmy")) {
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cThat command is blocked."));
        }

        profile.setCommandCooldown(new Cooldown(Core.getInstance().getChatHandler().getCommandTime() * 1000L));
    }

    @EventHandler(priority = EventPriority.LOW)
    public void onChat(AsyncPlayerChatEvent event) {
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

        Player player = event.getPlayer();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (!Core.getInstance().getChatHandler().isChatValue() && !Permission.test(player, PermLevel.STAFF)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cPublic chat is currently muted."));
            return;
        }

        if (Filter.needFilter(event.getMessage())) {
            String filter = Filter.PREFIX + " " + profile.getColoredName() + "&e: " + event.getMessage();
            Common.broadcastMessage(PermLevel.STAFF, filter);
        }

        String format = profile.getChatFormat() + "&f: " + event.getMessage();
        event.setFormat(Color.translate(format));
    }

    @EventHandler
    public void onChatAndInCooldown(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if(!profile.getChatCooldown().hasExpired() && !Permission.test(player, PermLevel.VIP)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cPublic chat is slowed down, please wait " + profile.getChatCooldown().getTimeLeft() + " seconds before talking again or &7buy a rank at &7https://store.invaded.cc"));
            return;
        }

        profile.setChatCooldown(new Cooldown(Core.getInstance().getChatHandler().getSlowTime() * 1000L));
    }

    @EventHandler (ignoreCancelled = true)
    public void onDisguise(PlayerDisguiseEvent event) {
        if(!Core.getInstance().getServerName().equalsIgnoreCase(event.getServer())) return;

        Player player = event.getPlayer();

        Task.async(() -> {
            player.sendMessage(Color.translate("&6If you disguise as a famous player or with an offensive name, your rank will be removed."));
            if (!CheckPremiumTask.runCheck(event.getFakeName())) return;
            player.sendMessage(Color.translate("&6You are disguising as a real player, if he enters, you will be kicked. "));
        });
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.LOW)
    public void onPunish(PlayerPunishEvent event){
        Punishment punishment = event.getPunishment();
       /* new JedisPoster(JedisAction.PUNISHMENT)
                .addInfo("profileId", event.getTarget().getUniqueId().toString())
                .addInfo("type", punishment.getType().name())
                .addInfo("cheaterName", event.getTarget().getName())
                .addInfo("cheaterUuid", event.getTarget().getUniqueId().toString())
                .addInfo("expire", punishment.getExpire())
                .addInfo("punishedAt", punishment.getPunishedAt())
                .addInfo("staffName", punishment.getStaffName())
                .addInfo("s", punishment.isS())
                .addInfo("reason", punishment.getReason())
            .post();*/

        PunishmentHandler punishmentHandler = Core.getInstance().getPunishmentHandler();
        punishmentHandler.punish(event.getTarget().getUniqueId(), event.getTarget().getName(), punishment);
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event){
        event.setCancelled(event.toWeatherState());
    }

    @EventHandler (ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onJoinDisguised(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if(DisguiseHandler.getDisguisedPlayers().containsKey(player.getUniqueId())) {
            System.out.println("Player is in the map");

            String[] info = DisguiseHandler.getDisguisedPlayers().get(player.getUniqueId()).split(";");

            profile.setFakeName(info[0]);
            profile.setFakeRank(Core.getInstance().getRankHandler().getRank(info[1]));
            profile.setFakeSkin(new Skin(info[2], info[3]));

            profile.disguise();
        }else {
            System.out.println("Player is not in the map");
        }
    }

}
