package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatControlCommand {

    public ChatControlCommand() {
        new SlowChatCommand();
        new ToggleChatCommand();
        new ClearChatCommand();
    }

    private class ClearChatCommand extends InvadedCommand {

        public ClearChatCommand(){
            super("clearchat", PermLevel.STAFF);
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            for(int i = 0; i < 100; i++){
                Common.getOnlinePlayers().forEach(player -> player.sendMessage(" "));
            }

            Bukkit.broadcastMessage(Color.translate("&aThe chat was cleared by " +
                    (sender instanceof Player ? Core.getInstance().getProfileHandler().getProfile(((Player) sender).getUniqueId()).getColoredName() : "&4Console") + "&a."));
        }
    }

    private class ToggleChatCommand extends InvadedCommand {

        public ToggleChatCommand(){
            super("togglechat", PermLevel.STAFF, "tc");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if(args.length != 0)  { sender.sendMessage(Color.translate("&cPlease use /togglechat")); return; }

            boolean b = !Core.getInstance().getChatHandler().isChatValue();

            Bukkit.broadcastMessage(String.format(Color.translate((b ? "&a" : "&c") + "The public chat was %s."), b ? "enabled" : "disabled"));
            Core.getInstance().getChatHandler().setChatValue(b);
        }
    }

    private class SlowChatCommand extends InvadedCommand {

        public SlowChatCommand() {
            super("slowchat", PermLevel.STAFF);
        }


        @Override
        public void execute(CommandSender sender, String[] args) {
            if (!(sender instanceof Player)) sender.sendMessage(Color.translate("&cPlayer only command"));
            else {
                Player player = (Player) sender;

                if (args.length != 1) player.sendMessage(Color.translate("&cPlease use /slowchat <time:off>"));

                else {
                    if (!isInt(args[0])) {
                        if(!validToggle(args[0])) player.sendMessage(Color.translate("&cPlease use /slowchat <time:off>"));
                        else {
                            Core.getInstance().getChatHandler().setSlowTime(-1);

                            Bukkit.broadcastMessage(Color.translate("&cPublic chat was unslowed."));
                        }

                        return;
                    }

                    int time = Integer.parseInt(args[0]);
                    Core.getInstance().getChatHandler().setSlowTime(time);

                    Bukkit.broadcastMessage(Color.translate("&aPublic chat was slowed for " + time + " seconds."));
                }
            }
        }
    }

    private boolean validToggle(String s){
        return s.equalsIgnoreCase("off");
    }

    private boolean isInt(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

}
