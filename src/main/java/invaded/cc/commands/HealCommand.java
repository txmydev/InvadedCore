package invaded.cc.commands;

import invaded.cc.Basic;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HealCommand extends BasicCommand {

    public HealCommand(){
        super("heal", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = null;
        ProfileHandler profileHandler = Basic.getInstance().getProfileHandler();

        switch(args.length){
            case 0:
                if(!(sender instanceof Player)){
                    sender.sendMessage(Color.translate("&cYou can only heal other players."));
                    break;
                }

                player = (Player) sender;
                player.setHealth(player.getMaxHealth());

                player.sendMessage(Color.translate("&aYou have been healed."));
                break;
            case 1:
                if(args[0].equalsIgnoreCase("all")){
                    Common.getOnlinePlayers().forEach(other -> {
                        other.setHealth((double) other.getMaxHealth());

                        other.sendMessage(Color.translate("&aYou have been healed by " + (sender instanceof Player ?
                                profileHandler.getProfile(((Player)sender).getUniqueId()).getColoredName() :
                         "&4Console") + "&a."));
                    });
                    return;
                }

                player = Bukkit.getPlayer(args[0]);

                if(player == null){
                    sender.sendMessage(Color.translate("&cThat player is offline."));
                    return;
                }

                player.sendMessage(Color.translate("&aYou have been healed."));
                player.setHealth(player.getMaxHealth());
                sender.sendMessage(Color.translate("&aYou have healed " + profileHandler.getProfile(player.getUniqueId()).getColoredName()));
                break;
            default:
                sender.sendMessage(Color.translate("&cPlease use /heal <player> or /heal."));
                break;
        }
    }
}
