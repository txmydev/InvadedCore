package invaded.cc.core.commands.poll;

import invaded.cc.core.Spotify;
import invaded.cc.core.poll.Poll;
import invaded.cc.core.poll.PollHandler;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Cooldown;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.NumberFormat;

public class PollCommand extends BasicCommand {

    private final Spotify plugin;

    public PollCommand(Spotify plugin){
        super("poll", PermLevel.STAFF);

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        if(args.length < 1) {
            sender.sendMessage(CC.RED + " ");
            sender.sendMessage(CC.RED + "/poll Help");
            sender.sendMessage(CC.RED + " ");
            sender.sendMessage(CC.RED + "/poll create <id> <text> <option1> <option2> <duration> |- Creates a new poll.");
            sender.sendMessage(CC.RED + "/poll vote <id> <option1:option2>");
            sender.sendMessage(CC.RED + "/poll status <id>");
            sender.sendMessage(" ");
            return;
        }

        PollHandler handler = plugin.getPollHandler();

        switch(args[0]) {
            case "create":
                if(args.length < 6) {
                    sender.sendMessage(CC.RED + "Use: /poll create <id> <option1> <option2> <duration> <text>  |- Creates a new poll.");
                    break;
                }

                String id = args[1], option1 = args[2], option2 = args[3];
                if(!isInt(args[4])){
                    sender.sendMessage(CC.RED + "The duration should be a number, you now, time stamps, etc...");
                    break;
                }
                int duration = Integer.parseInt(args[4]);
                StringBuilder builder = new StringBuilder();
                for(int i = 5; i < args.length; i++) builder.append(args[i]).append(" ");

                handler.create(new Poll(id, builder.toString(), option1, option2, new Cooldown(duration * 1000L)));
                break;
            case "vote":
                if(args.length != 3) {
                    sender.sendMessage(CC.RED + "Use: /poll vote <id> <option1:option2>");
                    break;
                }

                id = args[1];
                Poll poll = handler.getPoll(id);
                if(poll == null) {
                    sender.sendMessage(CC.RED + "The specified poll doesn't exist.");
                    break;
                }

                String option = args[2].toLowerCase();
                if(!option.equals("option1") && !option.equals("option2")) {
                    sender.sendMessage(CC.RED + "Use: /poll vote <id> <option1:option2>");
                    break;
                }

                poll.vote((Player) sender, option.equals("option1"));
                break;
            case "status":
                if(args.length != 2) {
                    sender.sendMessage(CC.RED + "Use: /poll vote <id> <option1:option2>");
                    break;
                }

                id = args[1];
                poll = handler.getPoll(id);
                if(poll == null) {
                    sender.sendMessage(CC.RED + "The specified poll doesn't exist.");
                    break;
                }

                poll.announce((Player) sender);
                break;
        }

    }

    private boolean isInt(String s ){
        try{
            Integer.parseInt(s);
            return true;
        }catch(NumberFormatException ex){
            return false;
        }
    }
}
