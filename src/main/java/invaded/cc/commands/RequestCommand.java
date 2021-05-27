package invaded.cc.commands;

import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.command.CommandSender;

public class RequestCommand extends BasicCommand {

    public RequestCommand() {
        super("testrequest", PermLevel.ADMIN);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length != 2) {
            return;
        }

        String method = args[0];
        String query = args[1];

       /* if(method.equalsIgnoreCase("get")) {
            HttpResponse response = RequestHandler.get(query);

            sender.sendMessage(Color.translate("&aGiven status code: " + response.statusCode()));
            sender.sendMessage(Color.translate(response.body()));

            response.close();
        }*/
    }
}
