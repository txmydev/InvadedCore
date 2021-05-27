package invaded.cc.commands;

import invaded.cc.Basic;
import invaded.cc.menu.DisguiseRankMenu;
import invaded.cc.profile.Profile;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DisguiseCommand extends BasicCommand {

    public DisguiseCommand(){
        super("disguise", PermLevel.DEFAULT, "d");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;
        Profile profile = Basic.getInstance().getProfileHandler().getProfile(player.getUniqueId());


        if(!Permission.test(player, PermLevel.MEDIA) && !profile.isAllowDisguise()){
            player.sendMessage(Color.translate("&cYou don't have permissions."));
            return;
        }

        if(args.length != 1){
            player.sendMessage(Color.translate("&cYou may use /disguise <nick>"));
            return;
        }

        if(!player.getName().equals("txmy")) {
            if (!Common.validDisguise(args[0]) || !Basic.getInstance().getProfileHandler().canDisguise(args[0])) {
                player.sendMessage(Color.translate("&CYou aren't allowed to disguise with that name!"));
                return;
            }
        }

        if(profile.isDisguised()) {
            player.sendMessage(Color.translate("&cPlease undisguise first."));
            return;
        }

        String disguiseNick = args[0];
        new DisguiseRankMenu(player, disguiseNick).open(player);
    }
}
