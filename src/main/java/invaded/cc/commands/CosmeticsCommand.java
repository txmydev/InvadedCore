package invaded.cc.commands;

import invaded.cc.Spotify;
import invaded.cc.menu.CosmeticsMenu;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CosmeticsCommand extends BasicCommand {

    public CosmeticsCommand() {
        super("cosmetics", PermLevel.DEFAULT, "tags", "prefixs", "suffixs", "trails", "disguiseaccess", "buydisguise", "buy");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        new CosmeticsMenu(Spotify.getInstance().getProfileHandler().getProfile((Player) sender)).open((Player) sender);
    }
}
