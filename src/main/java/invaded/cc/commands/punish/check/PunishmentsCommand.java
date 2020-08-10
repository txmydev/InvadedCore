package invaded.cc.commands.punish.check;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import invaded.cc.Core;
import invaded.cc.manager.RequestHandler;
import invaded.cc.menu.PunishmentsMenu;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.punishment.Punishment;
import invaded.cc.util.Color;
import invaded.cc.util.Task;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import jodd.http.HttpResponse;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PunishmentsCommand extends InvadedCommand {

    public PunishmentsCommand() {
        super("punishments", PermLevel.ADMIN, "c");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            if (sender instanceof ConsoleCommandSender) {
                Bukkit.getLogger().info("You cannot execute this, please log in.");
                return;
            }

            Player player = (Player) sender;

            if (args.length != 1) {
                player.sendMessage(Color.translate("&cPlease specify a player!"));
                return;
            }

            ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
            Profile target = profileHandler.getProfile(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
            if (target == null) target = profileHandler.load(Bukkit.getOfflinePlayer(args[0]).getUniqueId(), args[0]);

            player.sendMessage(Color.translate("&aRetrieving data, a menu will be displayed when the data is ready."));
            HttpResponse response = RequestHandler.get("/punishments/cheaterUuid/" + target.getId().toString());

            if(response.statusCode() == 200) {
                JsonArray jsonArray = new JsonParser().parse(response.body()).getAsJsonArray();
                List<Punishment> punishmentList = new ArrayList<>();

                for (JsonElement element : jsonArray) punishmentList.add(Core.GSON.fromJson(element, Punishment.class));

                Profile finalTarget = target;
                Task.run(() -> new PunishmentsMenu(finalTarget, punishmentList).open(player));
            }else player.sendMessage(Color.translate("&cCouldn't found any punishments on record for that player."));

            response.close();
        });
    }
}
