package invaded.cc.core.commands;

import invaded.cc.core.Spotify;
import invaded.cc.core.menu.ColorMenu;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ColorCommand extends BasicCommand {

    public ColorCommand(){
        super("color", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;

        if(args.length != 0){
            player.sendMessage(Color.translate("&cPlease use /color."));
            return;
        }

        new ColorMenu(Spotify.getInstance().getProfileHandler().getProfile(player.getUniqueId())).open(player);
    }
}
