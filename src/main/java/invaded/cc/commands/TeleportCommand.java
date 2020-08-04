package invaded.cc.commands;

import invaded.cc.profile.Profile;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TeleportCommand {

    public TeleportCommand(){
        new TpAllCommand();
        new TpCommand();
        new TpHereCommand();
    }

    private class TpAllCommand extends InvadedCommand {

        public TpAllCommand(){
            super("teleportall", PermLevel.ADMIN, "tpall");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(Color.translate("&cPlayer only command."));
                return;
            }

            Player player = (Player) sender;

            Common.getOnlinePlayers().stream().filter(other -> other.getUniqueId() != player.getUniqueId())
                    .forEach(other -> {
                        other.teleport(player);
                    });
        }
    }

    private class TpCommand extends InvadedCommand {
        public TpCommand(){
            super("teleport", PermLevel.STAFF, "tp");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(Color.translate("&cPlayer only command."));
                return;
            }

            if(args.length == 3){
                ((Player) sender).performCommand("bukkit:tp " + args[0] + " " + args[1] + " " + args[2]);
                return;
            }

            if(args.length == 4) {
                ((Player) sender).performCommand("bukkit:tp " + args[0] + " " + args[1] + " " + args[2] + " " + args[3]);
                return;
            }

            Player player = (Player) sender;

            switch(args.length){
                case 1:
                    Player target = Bukkit.getPlayer(args[0]);

                    if(target == null) player.sendMessage(Color.translate("&cThat player is offline."));
                    else {
                        player.teleport(target);

                        player.sendMessage(Color.translate("&fYou were teleported to " + target.getName()));
                    }

                    break;
                case 2:
                    Player playerOne = Bukkit.getPlayer(args[0]);

                    if(playerOne.getUniqueId() == player.getUniqueId()){
                        player.performCommand("teleport " + args[1]);
                        break;
                    }

                    Player playerTwo = Bukkit.getPlayer(args[1]);

                    if(playerTwo == null) player.sendMessage(Color.translate("&cThe player '" + args[1] + "' is offline."));
                    else {
                        playerOne.teleport(playerTwo);

                        player.sendMessage(Color.translate("&fYou teleported &b" + playerOne.getName() + " &fto &b" + playerTwo.getName()));
                    }

                    break;
            }
        }
    }

    private class TpHereCommand extends InvadedCommand {

        public TpHereCommand(){
            super("tphere", PermLevel.STAFF, "s");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(Color.translate("&cPlayer only command."));
                return;
            }

            Player player = (Player) sender;

            if(args.length != 1) player.sendMessage(Color.translate("&cPlease use /tphere <player>"));
            else {
                Player target = Bukkit.getPlayer(args[0]);

                target.teleport(player);

              //  player.sendMessage(Color.translate(targetData.getColoredName() + " &6was teleported to you."));
                target.sendMessage(Color.translate("&6You were teleported by &b" + player.getName()));
            }
        }

    }
}
