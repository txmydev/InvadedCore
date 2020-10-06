package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PingCommand extends InvadedCommand {

    public PingCommand(){
        super("ping", PermLevel.DEFAULT);
    }

    private ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(!(sender instanceof Player)) {
            sender.sendMessage(Color.translate("&cPlayer only command."));
            return;
        }

        Player player = (Player) sender;
        int ping;

        switch(args.length){
            case 0:
                ping = ((CraftPlayer) player).getHandle().ping;
                player.sendMessage(Color.translate("&6Your ping is&7: &f" + pretty(ping)));
                break;
            case 1:
                Player target = Bukkit.getPlayer(args[0]);

                if(target == null) player.sendMessage(Color.translate("&cThat player is offline"));

                else
                {
                    Profile profile = profileHandler.getProfile(target.getUniqueId());

                    ping = ((CraftPlayer) target).getHandle().ping;
                    player.sendMessage(Color.translate(profile.getColoredName() + "'s &6ping is&7: " + pretty(ping)));
                }
                break;
            default:
                player.sendMessage(Color.translate("Please use /ping or /ping <player>"));
                break;
        }
    }

    private String pretty(int ping) {
        String s = "";

        if(ping > 210) s = "&c" + ping;
        else if(ping > 120) s = "&e" + ping;
        else s = "&a" + ping;

        s = s + "ms";

        return s;
    }
}
