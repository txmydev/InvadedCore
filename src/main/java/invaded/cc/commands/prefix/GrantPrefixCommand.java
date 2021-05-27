package invaded.cc.commands.prefix;

import invaded.cc.Basic;
import invaded.cc.menu.prefix.GrantPrefixMenu;
import invaded.cc.profile.Profile;
import invaded.cc.util.Color;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GrantPrefixCommand extends BasicCommand {

    public GrantPrefixCommand() {
        super("grantprefix", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player)sender;
        if(args.length != 1){
            player.sendMessage(Color.translate("&cPlease use /grantprefix <player>."));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if(target==null){
            player.sendMessage(Color.translate("&cThat player is offline."));
            return;
        }

        Profile profile = Basic.getInstance().getProfileHandler().getProfile(target);
        new GrantPrefixMenu(profile).open(player);
    }
}
