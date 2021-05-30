package invaded.cc.commands;

import invaded.cc.Spotify;
import invaded.cc.menu.ColorMenu;
import invaded.cc.util.Color;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
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
