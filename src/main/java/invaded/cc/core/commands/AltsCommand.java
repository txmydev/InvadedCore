package invaded.cc.core.commands;

import invaded.cc.core.Spotify;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class AltsCommand extends BasicCommand {

    private final Spotify plugin;

    public AltsCommand(Spotify plugin) {
        super("alts", PermLevel.ADMIN, "multiaccounts");
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            if (args.length != 1) {
                if(args.length == 2) {
                    if(args[1].equalsIgnoreCase("findip")) {
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                        String address = plugin.getAltHandler().getIpAddress(offlinePlayer);
                        if(address == null) {
                            sender.sendMessage(CC.RED + "IP not found.");
                            return;
                        }

                        sender.sendMessage(CC.GREEN + address);
                        return;
                    }
                }

                sender.sendMessage(CC.RED + "Please use: /alts <player>");
                return;
            }
            Player player = getPlayer(args[0], sender, "Player offline.");
            if (player == null) return;
            List<UUID> uuids = plugin.getAltHandler().getAlts(player).stream().filter(id -> !id.equals(player.getUniqueId())).collect(Collectors.toList());
            sender.sendMessage(CC.GREEN + "Alts found for " + player.getDisplayName());
            sender.sendMessage(" ");
            uuids.forEach(id ->
                    sender.sendMessage(CC.GRAY + "- " + CC.GOLD + Bukkit.getOfflinePlayer(id).getName())
            );

            sender.sendMessage(" ");
        });
    }
}
