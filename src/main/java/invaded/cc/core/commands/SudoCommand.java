package invaded.cc.core.commands;

import invaded.cc.core.util.Color;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SudoCommand extends BasicCommand {

    public SudoCommand() {
        super("sudo", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length <= 1) {
            sender.sendMessage(Color.translate("&cPlease use /sudo <player> <c:cmd:>"));
            return;
        }

        Player target = Bukkit.getPlayer(args[0]);

        if(target==null) {
            sender.sendMessage(Color.translate("&cThat player is offline"));
            return;
        }

        String toDo = args[1];

        if(toDo.split(":").length == 0){
            sender.sendMessage(Color.translate("&cPlease use /sudo <player> <c:cmd:>"));
            return;
        }

        String type = toDo.split(":")[0];
        StringBuilder message = new StringBuilder();

        for(int i = 1; i < args.length; i++) {
            if(args[i].contains(":")) message.append(args[i].substring(args[i].lastIndexOf(":") + 1)).append(" ");
            else message.append(args[i]).append(" ");
        }

        SudoType sudoType = getType(type);
        if(sudoType == null) {
            sender.sendMessage(Color.translate("&cThat player is offline"));
            return;
        }

        switch(sudoType) {
            case CHAT:
                target.chat(message.toString());
                break;
            case COMMAND:
                target.performCommand(message.toString().replace("/", ""));
                break;
        }
    }

    private SudoType getType(String type) {
        if(type.equals("c")) {
            return SudoType.CHAT;
        }else if(type.equals("cmd"))
            return SudoType.COMMAND;

        return null;
    }

    private enum SudoType {
        CHAT, COMMAND;
    }
}
