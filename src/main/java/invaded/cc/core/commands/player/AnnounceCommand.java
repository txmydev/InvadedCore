package invaded.cc.core.commands.player;

import invaded.cc.core.Spotify;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnnounceCommand extends BasicCommand {

    public AnnounceCommand() {
        super("announce", PermLevel.VIP, "alertgame");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if (Spotify.SERVER_NAME.contains("hub")) {
            player.sendMessage(Color.translate("&cYou are trying to announce you are in the hub??"));
            return;
        }

        /*new JedisPoster(JedisAction.BROADCAST)
                .addInfo("message", profile.getChatFormat() + " &bis playing in &f'" + Spotify.SERVER_NAME + "'&b, you think you can destroy him? Type &f'/join " + Spotify.SERVER_NAME
                        + "' &bor &eClick here&b!")
                .addInfo("hover", "false;xd")
                .addInfo("click", "true;/join " + Spotify.SERVER_NAME)
                .post();*/
    }
}
