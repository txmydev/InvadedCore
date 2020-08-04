package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.database.redis.JedisAction;
import invaded.cc.database.redis.poster.JedisPoster;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnDisguiseCommand extends InvadedCommand {

    public UnDisguiseCommand(){
        super("undisguise", PermLevel.DEFAULT, "ud");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;
        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        Profile profile = profileHandler.getProfile(player.getUniqueId());

        if(!Permission.test(player, PermLevel.MEDIA) && !profile.isAllowDisguise()){
            player.sendMessage(Color.translate("&cYou don't have permissions."));
            return;
        }

        if(args.length != 0){
            sender.sendMessage(Color.translate("&cYou may use /ud"));
            return;
        }

        if(!Core.getInstance().getServerName().contains("hub")){
            player.sendMessage(Color.translate("&cYou may only disguise in the hub."));
            return;
        }

        if(!profile.isDisguised()){
            player.sendMessage(Color.translate("&cYou aren't disguised."));
            return;
        }

        new JedisPoster(JedisAction.UNDISGUISE)
                .addInfo("profileId", profile.getId().toString())
                .post();

        player.sendMessage(Color.translate("&aYou've been undisguised."));
    }
}
