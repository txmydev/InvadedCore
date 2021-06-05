package invaded.cc.core.commands;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.Color;
import me.txmy.command.BaseCommand;
import me.txmy.command.Command;
import me.txmy.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DisguiseCheckCommand extends BaseCommand {

    @Command(name = "checkdisguise", aliases = {"isd", "checkd", "dcheck"}, permission = "invaded.staff")
    public void onCommand(CommandArgs command) {
        if(command.getArgs().length != 1){
            command.getSender().sendMessage(Color.translate("&cUse /checkdisguise <player>."));
            return;
        }

        Player player = Bukkit.getPlayer(command.getArgs()[0]);
        if(player == null) {
            command.getSender().sendMessage(Color.translate("&cThat player is offline."));
            return;
        }

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        profileHandler.ifPresent(player.getUniqueId(), profile -> {
            if(!profile.isDisguised()) command.getSender().sendMessage(Color.translate(profile.getColoredName() + " &cisn't disguised."));
            else command.getSender().sendMessage(Color.translate(profile.getRealColoredName() + " &ais disguised as " + profile.getColoredName()));
        }, command.getSender(), "That player is offline.");
    }
}
