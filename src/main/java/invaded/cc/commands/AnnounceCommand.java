package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AnnounceCommand extends InvadedCommand {

    public AnnounceCommand(){
        super("announce", PermLevel.VIP, "alertgame");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;

        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if(Core.getInstance().getServerName().contains("hub")) {
            player.sendMessage(Color.translate("&cYou are trying to announce you are in the hub??"));
            return;
        }

        new JedisPoster(JedisAction.BROADCAST)
                .addInfo("message", profile.getChatFormat() + " &bis playing in &f'" + Core.getInstance().getServerName() + "'&b, you think you can destroy him? Type &f'/join " + Core.getInstance().getServerName()
                +"' &bor &eClick here&b!")
        .addInfo("hover", "false;xd")
        .addInfo("click", "true;/join " + Core.getInstance().getServerName())
        .post();
    }
}
