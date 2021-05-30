package invaded.cc.commands;

import invaded.cc.Spotify;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IgnoreCommand extends BasicCommand {

    public IgnoreCommand(){
        super("ignore", PermLevel.DEFAULT);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player))
            return;

        Player player = (Player) sender;
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        if (args.length != 1) {
            player.sendMessage(Color.translate("&cYou may use /ignore <player>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if (target == null) {
            player.sendMessage(Color.translate("&cThat player is offline."));
            return;
        }

        if(player.getUniqueId() == target.getUniqueId()){
            player.sendMessage(Color.translate("&cYou cannot ignore yourself."));
            return;
        }

        Profile profile = profileHandler.getProfile(player.getUniqueId());
        Profile targetData  = profileHandler.getProfile(target.getUniqueId());

        if(profile.getIgnoreList().contains(target.getName())) {
            profile.getIgnoreList().remove(target.getName());
            player.sendMessage(Color.translate("&aYou have removed " + targetData.getDisguisedName() + " &afrom your ignore list."));
            return;
        }

        profile.getIgnoreList().add(target.getName());
        player.sendMessage(Color.translate("&aYou are now ignoring " + targetData.getDisguisedName() + "."));
    }
}
