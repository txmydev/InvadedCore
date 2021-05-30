package invaded.cc.commands;

import invaded.cc.Spotify;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnnounceCommand extends BasicCommand {

    public AnnounceCommand(){
        super("announce", PermLevel.VIP, "alertgame");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if(Spotify.getInstance().getServerName().contains("hub")) {
            player.sendMessage(Color.translate("&cYou are trying to announce you are in the hub??"));
            return;
        }

        new JedisPoster(JedisAction.BROADCAST)
                .addInfo("message", profile.getChatFormat() + " &bis playing in &f'" + Spotify.getInstance().getServerName() + "'&b, you think you can destroy him? Type &f'/join " + Spotify.getInstance().getServerName()
                +"' &bor &eClick here&b!")
        .addInfo("hover", "false;xd")
        .addInfo("click", "true;/join " + Spotify.getInstance().getServerName())
        .post();
    }
}
