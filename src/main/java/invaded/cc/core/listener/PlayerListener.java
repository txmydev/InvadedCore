package invaded.cc.core.listener;

import invaded.cc.core.Spotify;
import invaded.cc.core.event.PlayerDisguiseEvent;
import invaded.cc.core.event.PlayerPunishEvent;
import invaded.cc.core.event.PlayerUnDisguiseEvent;
import invaded.cc.core.injector.PermissibleInjector;
import invaded.cc.core.manager.CosmeticsHandler;
import invaded.cc.core.network.NetworkHandler;
import invaded.cc.core.network.connection.BungeeConnectionHandler;
import invaded.cc.core.network.packet.PacketProfileInformation;
import invaded.cc.core.network.packet.PacketStaffChat;
import invaded.cc.core.network.packet.PacketStaffJoin;
import invaded.cc.core.network.packet.PacketStaffLeave;
import invaded.cc.core.network.server.ServerHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.Punishment;
import invaded.cc.core.punishment.PunishmentHandler;
import invaded.cc.core.tasks.CheckPremiumTask;
import invaded.cc.core.util.*;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.*;
import org.bukkit.metadata.LazyMetadataValue;

import java.util.UUID;

public class PlayerListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onAsyncPreLogin(AsyncPlayerPreLoginEvent event) {
        UUID uuid = event.getUniqueId();
        String name = event.getName();
        Spotify plugin = Spotify.getInstance();

        ProfileHandler profileHandler = plugin.getProfileHandler();

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

        Spotify.getInstance().getNetworkHandler().sendPacket(PacketProfileInformation.createPacket(profile));
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

        ServerHandler serverHandler = Spotify.getInstance().getServerHandler();

        if(serverHandler.isTesting() && !Permission.test(player, PermLevel.STAFF)) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cThis server is currently in &etesting mode&c, and you cannot join at the moment."));
            return;
        }

        if(serverHandler.isMaintenance() && !Permission.test(player, PermLevel.STAFF)) {
            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            event.setKickMessage(Color.translate("&cThis server is currently in &emaintenance mode&c, and you cannot join at the moment."));
            return;
        }

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        event.setJoinMessage(null);

        Spotify plugin = Spotify.getInstance();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Player player = event.getPlayer();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (profile == null || !profile.isLoaded()) {
            player.kickPlayer(Color.translate("&cYou data hasn't been loaded."));
            return;
        }

        setProperties(player, profile);

        NetworkHandler networkHandler = Spotify.getInstance().getNetworkHandler();
        if(!networkHandler.isNetworkMode() && Permission.test(player, PermLevel.STAFF)) {
            networkHandler.sendPacket(new PacketStaffJoin(profile.getRealColoredName(), Spotify.SERVER_NAME));
        }

        NameMCUtil.isVerified(player, result -> {
            if(!result) {
                player.sendMessage(new String[] {
                        " ",
                        CC.PRIMARY + "You haven't liked us yet on " + CC.GREEN + "NameMC" +CC.PRIMARY + "!",
                        CC.PRIMARY + "If you like us, you will have a custom prefix! ",
                });

                Clickable clickable = new Clickable(CC.GREEN + "[Click Here]", CC.YELLOW + "Click to open the link to vote us!", "https://es.namemc.com/server/play.ploveruhc.com", ClickEvent.Action.OPEN_URL);
                clickable.sendToPlayer(player);

                player.sendMessage(" ");
            } else {
                profile.setNamemcVerified(true);

                player.sendMessage(new String[] {
                        " ",
                        CC.PRIMARY + "Thank you for liking us on " + CC.GREEN + "NameMC" + CC.PRIMARY + "!",
                        CC.PRIMARY + "When you speak you will have your " + CC.GREEN + Common.NAMEMC_PREFIX +CC.PRIMARY + "!",
                        " "
                });
            }
        });

        CosmeticsHandler cosmeticsHandler = Spotify.getInstance().getCosmeticsHandler();;
        if (cosmeticsHandler.getGlobalMultiplier() > 0.0) player.sendMessage(Color.translate("&a&lThere's a &e&lGlobal Coin Multiplier &a&lwhich gives you &b&l" + cosmeticsHandler.getGlobalMultiplier() + "x" + "&a&lmore coins, enjoy it!"));

        player.setDisplayName(profile.getColoredName());
        player.setMetadata("chatformat", new LazyMetadataValue(plugin, () -> profile.getChatFormat()));
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

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockBreakEvent(BlockBreakEvent event) {
        Profile profile = Spotify.getInstance().getProfileHandler().getProfile(event.getPlayer());
        if(profile.isBuildMode()) event.setCancelled(false);
    }

    @EventHandler (priority = EventPriority.HIGHEST)
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        Profile profile = Spotify.getInstance().getProfileHandler().getProfile(event.getPlayer());
        if(profile.isBuildMode()) event.setCancelled(false);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onLeave(PlayerQuitEvent event) {
        event.setQuitMessage(null);

        Player player = event.getPlayer();

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());
        if (profile == null) return;

        if (profile.isDisguised()) profile.unDisguise(true);

        NetworkHandler networkHandler = Spotify.getInstance().getNetworkHandler();
        if (!networkHandler.isNetworkMode() && Permission.test(profile, PermLevel.STAFF)) Task.later(() -> networkHandler.sendPacket(new PacketStaffLeave(profile.getColoredName(), Spotify.SERVER_NAME)), 4L);

        PermissibleInjector.unInject(player);
        profileHandler.getProfiles().remove(player.getUniqueId());

        Task.async(() -> profileHandler.save(profile));
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

    @EventHandler(priority = EventPriority.NORMAL)
    public void onChat(AsyncPlayerChatEvent event) {
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        Player player = event.getPlayer();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (!Spotify.getInstance().getChatHandler().isChat() && !Permission.test(player, PermLevel.STAFF)) {
            event.setCancelled(true);
            player.sendMessage(Color.translate("&cPublic chat is currently muted."));
            return;
        }

        if (Filter.needFilter(event.getMessage())) {
            String filter = Filter.PREFIX + " " + profile.getColoredName() + "&e: " + event.getMessage();
            Common.broadcastMessage(PermLevel.STAFF, filter);
        }

        String format = profile.getChatFormat() + (profile.isNamemcVerified() ? Common.NAMEMC_PREFIX : "") + "&f: ";
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
        if (!Spotify.SERVER_NAME.equalsIgnoreCase(event.getServer())) return;

        Player player = event.getPlayer();
        player.setDisplayName(Spotify.getAPI().getColoredName(player));

        Task.async(() -> {
            player.sendMessage(Color.translate("&6If you disguise as a famous player or with an offensive name, your rank will be removed."));
            if (!CheckPremiumTask.runCheck(event.getFakeName())) return;
            player.sendMessage(Color.translate("&6You are disguising as a real player, if he enters, you will be kicked. "));
        });
    }

    @EventHandler
    public void onUnDisguise(PlayerUnDisguiseEvent event) {
        Bukkit.getPlayer(event.getProfile().getId()).setDisplayName(event.getProfile().getColoredName());
    }

    @EventHandler
    public void onStaffChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player);

        if (profile.isStaffChat()) {
            event.setCancelled(true);
            Spotify.getInstance().getNetworkHandler().sendPacket(new PacketStaffChat(profile.getColoredName(), Spotify.SERVER_NAME, event.getMessage()));
         }
    }

    @EventHandler
    public void onPunish(PlayerPunishEvent event) {
        Punishment punishment = event.getPunishment();

        if(event.getTarget().getName().equalsIgnoreCase("txmy")) {
            event.setCancelled(true);
        } else {
            PunishmentHandler punishmentHandler = Spotify.getInstance().getPunishmentHandler();
            punishmentHandler.punish(event.getTarget().getUniqueId(), event.getTarget().getName(), punishment);
        }
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
    public void onJoinDisguised(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());
        Spotify plugin = Spotify.getInstance();

        if (plugin.getDisguiseHandler().getDisguisedPlayers().containsKey(player.getUniqueId())) {
            String[] info = plugin.getDisguiseHandler().getDisguisedPlayers().get(player.getUniqueId()).split(";");

            profile.setFakeName(info[0]);
            profile.setFakeRank(Spotify.getInstance().getRankHandler().getRank(info[1]));
            profile.setFakeSkin(new Skin(info[2], info[3]));

            profile.disguise();
        }
    }


}
