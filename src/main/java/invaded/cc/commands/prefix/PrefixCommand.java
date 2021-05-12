package invaded.cc.commands.prefix;

import invaded.cc.Core;
import invaded.cc.menu.prefix.PrefixMenu;
import invaded.cc.prefix.Prefix;
import invaded.cc.prefix.PrefixHandler;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Task;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Locale;

public class PrefixCommand extends InvadedCommand {

    public PrefixCommand() {
        super("prefix", PermLevel.DEFAULT, "prefixs", "prefixes");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        PrefixHandler prefixHandler = Core.getInstance().getPrefixHandler();

        if(args.length == 0) {
            Profile profile = profileHandler.getProfile(player);
            new PrefixMenu(profile).open(player);
            return;
        }

        String arg1 = args[0].toLowerCase();
        if(arg1.equals("create")) {
            if(!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if(args.length != 3) {
                player.sendMessage(Color.translate("&c/prefix create <id> <display>"));
                return;
            }

            String id = args[1];
            String display = args[2];

            Prefix prefix = new Prefix(id, display);
            prefixHandler.getPrefixes().add(prefix);

            player.sendMessage(Color.translate("&aYou've created the prefix &6" +id+" &awith display &r" + display));
        } else if(arg1.equals("delete") || arg1.equals("remove")) {
            if(!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if(args.length != 2) {
                player.sendMessage(Color.translate("&c/prefix delete/remove <id>"));
                return;
            }

            String id = args[1];
            Prefix prefix = prefixHandler.getPrefix(id);
            if(prefix == null){
                player.sendMessage(Color.translate("&cThat prefix doesn't exist."));
                return;
            }

            Task.async(() -> prefixHandler.remove(prefix));
            player.sendMessage("&cYou've removed prefix &6"+id+"&c.");
        } else if(arg1.equals("modify")) {
            if(!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if(args.length != 3) {
                player.sendMessage(Color.translate("&c/prefix modify <id> <display>"));
                return;
            }

            String id = args[1];
            String display = args[2];

            Prefix prefix = prefixHandler.getPrefix(id);
            if(prefix == null){
                player.sendMessage(Color.translate("&cThat prefix doesn't exist."));
                return;
            }

            prefix.setDisplay(display);
            player.sendMessage(Color.translate("&aYou've modified &6" + id + "&a's display to &r" + display + "&a."));
        }
    }
}
