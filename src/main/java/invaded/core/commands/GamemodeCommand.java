package invaded.core.commands;

import invaded.core.util.Color;
import invaded.core.util.command.BasicCommand;
import invaded.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GamemodeCommand {

    public GamemodeCommand(){
        new CreativeCommand();
        new SurvivalCommand();
        new GlobalCommand();
    }


    private class CreativeCommand extends BasicCommand {

        public CreativeCommand() {
            super("gamemodecreative", PermLevel.ADMIN, "gmc");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(Color.translate("&CPlayer only command."));
                return;
            }

            Player player = (Player) sender;

            if(args.length != 0) player.sendMessage(Color.translate("&cUse /gmc"));
            else changeGamemode(player, 1);
        }
    }

    private class SurvivalCommand extends BasicCommand {

        public SurvivalCommand() {
            super("gamemodesurvival", PermLevel.ADMIN, "gms");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if(!(sender instanceof Player)) {
                sender.sendMessage(Color.translate("&CPlayer only command."));
                return;
            }

            Player player = (Player) sender;

            if(args.length != 0) player.sendMessage(Color.translate("&cUse /gms"));
            else changeGamemode(player, 0);
        }
    }

    private class GlobalCommand extends BasicCommand {

        public GlobalCommand() {
            super("gm", PermLevel.ADMIN);
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (sender instanceof Player) {
                Player player = (Player) sender;

                switch (args.length) {
                    case 1:
                        if (!isInt(args[0])) {
                            player.sendMessage(Color.translate("&cPlease use /gm <0:1>"));
                            break;
                        }

                        changeGamemode(player, Integer.parseInt(args[0]));
                        break;
                    case 2:
                        if (!isInt(args[0])) {
                            player.sendMessage(Color.translate("&cPlease use /gm <0:1> <player>"));
                            break;
                        }

                        Player target = Bukkit.getPlayer(args[1]);

                        if (target == null) {
                            player.sendMessage(Color.translate("&CThat player is offline."));
                            break;
                        }

                        changeGamemode(target, Integer.parseInt(args[0]));
                        break;
                    default:
                        player.sendMessage(Color.translate("&cPlease check the command syntax and try again."));
                        break;
                }

                return;
            }

            if (args.length != 2)  { sender.sendMessage(Color.translate("&cPlease use /gm <0:1> <player>")); return; }

            if(!isInt(args[0])){
                sender.sendMessage(Color.translate("&cPlease use /gm <0:1> <player>"));
                return;
            }

            Player target = Bukkit.getPlayer(args[1]);

            if(target == null) sender.sendMessage(Color.translate("&cThat player is offline."));
            else changeGamemode(target, Integer.parseInt(args[0]));
        }
    }

    private boolean isInt(String s) {
        try {
            int i = Integer.parseInt(s);
            return i < 2;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private void changeGamemode(Player target, int value) {
        if (value == 0) target.setGameMode(GameMode.SURVIVAL);
        else target.setGameMode(GameMode.CREATIVE);

        target.sendMessage(Color.translate("&6You successfully updated your gamemode."));
    }
}
