package invaded.cc.commands.messaging;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SoundCommand extends InvadedCommand {

    public SoundCommand() {
        super("privatemessagesound", PermLevel.DEFAULT, "pmsound");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        if(args.length != 0) {
            sender.sendMessage(Color.translate("&cPlease use /pm"));
            return;
        }

        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

        Player player = (Player) sender;
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        boolean v = !profile.isMessagesSound();
        profile.setMessagesSound(v);

        player.sendMessage(Color.translate((v ? "&a" : "&7") + "You toggled your pm's sound."));
    }
}
