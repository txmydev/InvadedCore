package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import me.txmy.command.BaseCommand;
import me.txmy.command.Command;
import me.txmy.command.CommandArgs;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DisguiseCheckCommand extends BaseCommand {

    @Command(name = "checkdisguise", aliases = {"isd, checkd, dcheck"}, permission = "invaded.staff")
    public void onCommand(CommandArgs command) {
        if(command.getArgs().length != 1){
            command.getSender().sendMessage(Color.translate("&cUse /checkdisguise <player>."));
            return;
        }

        Player player = Bukkit.getPlayer(command.getArgs()[0]);
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

        profileHandler.ifPresent(player.getUniqueId(), profile -> {
            if(!profile.isDisguised()) command.getSender().sendMessage(profile.getColoredName() + " &cisn't &adisguised.");
            else command.getSender().sendMessage(Color.translate(profile.getRealColoredName() + " &ais disguised as " + profile.getColoredName()));
        }, command.getSender(), "That player is offline.");
    }
}
