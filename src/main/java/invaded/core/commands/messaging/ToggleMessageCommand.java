package invaded.core.commands.messaging;

import invaded.core.Spotify;
import invaded.core.profile.Profile;
import invaded.core.profile.ProfileHandler;
import invaded.core.util.Color;
import invaded.core.util.command.BasicCommand;
import invaded.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ToggleMessageCommand extends BasicCommand {

    public ToggleMessageCommand() {
        super("togglepm", PermLevel.DEFAULT, "pm");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();

        if(args.length != 0) {
            sender.sendMessage(Color.translate("&cPlease use /pm"));
            return;
        }

        Player player = (Player) sender;
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        boolean v = !profile.isMessages();
        profile.setMessages(v);

        player.sendMessage(Color.translate((v ? "&a" : "&7") + "You toggled your private messages."));
    }
}
