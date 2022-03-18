package invaded.cc.core.commands.staff;

import invaded.cc.core.Spotify;
import invaded.cc.core.lunarapi.nethandler.client.LCPacketCooldown;
import invaded.cc.core.lunarapi.nethandler.client.LCPacketNotification;
import invaded.cc.core.lunarapi.nethandler.client.LCPacketTeammates;
import invaded.cc.core.lunarapi.nethandler.client.LCPacketTitle;
import invaded.cc.core.lunarapi.title.LCTitleBuilder;
import invaded.cc.core.lunarapi.title.TitleType;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TestLunarAPICommand extends BasicCommand {

    private final Spotify plugin;

    public TestLunarAPICommand(Spotify plugin) {
        super("testapi", PermLevel.DEVELOPER);

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length == 1) {
            plugin.getLunarHandler().hideNametag(Bukkit.getPlayer(args[0]), (Player) sender);
         /*   Map<UUID, Map<String, Double>> players = new HashMap<>();
            Player player = (Player) sender;

            Player target = Bukkit.getPlayer(args[0]);
            players.put(target.getUniqueId(), new HashMap<>());

            Map<String, Double> coords = players.get(Bukkit.getPlayer(args[0]).getUniqueId());

            coords.put("x", target.getLocation().getX());
            coords.put("y", target.getLocation().getY());
            coords.put("z", target.getLocation().getY());

            plugin.getLunarHandler().sendTeammates(player, new LCPacketTeammates(player.getUniqueId(), System.currentTimeMillis(), players));

           */
            return;
        }

        sender.sendMessage(plugin.getLunarHandler().isRunningLunarClient((Player) sender) + "");
        plugin.getLunarHandler().sendPacket((Player) sender, LCTitleBuilder.of(CC.YELLOW + "Hello MF", TitleType.TITLE).build());
        sender.sendMessage(CC.RED + "s");
    }
}
