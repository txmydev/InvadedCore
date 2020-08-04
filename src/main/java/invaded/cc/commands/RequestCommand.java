package invaded.cc.commands;

import invaded.cc.manager.RequestHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import jodd.http.HttpResponse;
import org.bukkit.command.CommandSender;

public class RequestCommand extends InvadedCommand {

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

        if(method.equalsIgnoreCase("get")) {
            HttpResponse response = RequestHandler.get(query);

            sender.sendMessage(Color.translate("&aGiven status code: " + response.statusCode()));
            sender.sendMessage(Color.translate(response.body()));

            response.close();
        }
    }
}
