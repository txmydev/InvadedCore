package invaded.cc.core.commands.player;

import invaded.cc.core.Spotify;
import invaded.cc.core.commands.staff.RedisFailingCommand;
import invaded.cc.core.network.packet.PacketRequestHelp;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.*;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpOpCommand extends BasicCommand {

    public HelpOpCommand() {
        super("helpop", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        if (args.length == 0) {
            sender.sendMessage(Color.translate("&cPlease use /helpop <message>"));
            return;
        }

        Player player = (Player) sender;
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (!profile.getHelpOpCooldown().hasExpired()) {
            player.sendMessage(Color.translate("&CYou may wait " + profile.getHelpOpCooldown().getTimeLeft() + " seconds until you can use helpop again."));
            return;
        }

        StringBuilder builder = new StringBuilder();

        for (String arg : args) builder.append(arg).append(" ");

        profile.setHelpOpCooldown(new Cooldown(50_000));

        player.sendMessage(Color.translate("&aWe received your request."));

        if(RedisFailingCommand.FAILING) {
            ComponentBuilder componentBuilder = new ComponentBuilder(CC.BLUE + "[Helpop] " + CC.GRAY + "[" + Spotify.SERVER_NAME + "] " + CC.AQUA + name + CC.GRAY + " asked for help: ");
            ComponentBuilder reasonBuilder = new ComponentBuilder("   " +CC.BLUE + "Reason: " + CC.GRAY + builder.toString());

            componentBuilder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + name));
            componentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.GREEN + "Click to teleport to " + name).create()));
            Common.getOnlinePlayers().forEach(other -> {
                if(Permission.test(other, PermLevel.STAFF)) {
                    other.spigot().sendMessage(componentBuilder.create());
                    other.spigot().sendMessage(reasonBuilder.create());
                }
            });
            Bukkit.getConsoleSender().sendMessage(TextComponent.toPlainText(componentBuilder.create()));
            return;
        }

        PacketRequestHelp helpRequest = new PacketRequestHelp(profile, builder.toString(), Spotify.SERVER_NAME);
        Spotify.getInstance().getNetworkHandler().sendPacket(helpRequest);

    }
}
