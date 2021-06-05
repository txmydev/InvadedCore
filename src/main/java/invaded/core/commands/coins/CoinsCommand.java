package invaded.core.commands.coins;

import invaded.core.Spotify;
import invaded.core.profile.Profile;
import invaded.core.util.Color;
import invaded.core.util.command.BasicCommand;
import invaded.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class CoinsCommand extends BasicCommand {

    public CoinsCommand() {
        super("coins", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length== 0) {
            sender.sendMessage("/coins <add:remove> <player> <coins>");
            return;
        }

        String arg1 = args[0].toLowerCase(Locale.ROOT);
        if(arg1.equals("add")) {
            if(args.length != 3) {
                sender.sendMessage("/coins <add:remove> <player> <coins>");
                return;
            }

            Player player = Bukkit.getPlayer(args[1]);
            int coins = getInt(args[2]);
            if(coins == -1) {
                sender.sendMessage("Invalid number");
                return;
            }

            Profile profile = Spotify.getInstance().getProfileHandler().getProfile(player.getUniqueId());
            profile.aggregateCoins(coins);
            sender.sendMessage("Added " + coins + " coins to player " + profile.getColoredName());
            player.sendMessage(Color.translate("&aYour coins have been updated to &6" + profile.getCoins() + " coins&a. &7(&a+" + coins + " coins&7)"));
        } else if(arg1.equals("remove")) {
            if(args.length != 3) {
                sender.sendMessage("/coins <add:remove> <player> <coins>");
                return;
            }

            Player player = Bukkit.getPlayer(args[1]);
            int coins = getInt(args[2]);
            if(coins == -1) {
                sender.sendMessage("Invalid number");
                return;
            }

            Profile profile = Spotify.getInstance().getProfileHandler().getProfile(player.getUniqueId());
            profile.removeCoins(coins);
            sender.sendMessage("Removed " + coins + " coins to player " + profile.getColoredName());
            player.sendMessage(Color.translate("&aYour coins have been updated to &6" + profile.getCoins() + " coins&a. &7(&c-" + coins +" coins&7)"));
        }
    }
    private int getInt(String arg) {
        try{
            return Integer.parseInt(arg);
        }catch(NumberFormatException ex) {
            return -1;
        }
    }
}
