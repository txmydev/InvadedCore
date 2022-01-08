package invaded.cc.core.commands;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BuildCommand extends BasicCommand {

    public BuildCommand(){
        super("build", PermLevel.ADMIN, "togglebuild", "buildmode");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        Player player = ((Player) sender).getPlayer();
        Profile profile = Spotify.getInstance().getProfileHandler().getProfile(player);

        boolean newValue = !profile.isBuildMode();
        profile.setBuildMode(newValue);
        player.sendMessage(Color.translate("&eYou " + (newValue ? "&eare &anow" : "&eare &cno longer") + " &ein build mode."));
    }
}
