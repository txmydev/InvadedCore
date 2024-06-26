package invaded.cc.core.commands.player;

import invaded.cc.core.Spotify;
import invaded.cc.core.menu.CosmeticsMenu;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CosmeticsCommand extends BasicCommand {

    public CosmeticsCommand() {
        super("cosmetics", PermLevel.DEFAULT, "trails", "disguiseaccess", "buydisguise", "buy");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        new CosmeticsMenu(Spotify.getInstance().getProfileHandler().getProfile((Player) sender)).open((Player) sender);
    }
}
