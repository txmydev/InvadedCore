package invaded.cc.core.commands.punish.check;

import com.google.common.collect.Maps;
import invaded.cc.core.Spotify;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.menu.PunishmentsMenu;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.Punishment;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import jodd.http.HttpResponse;
import invaded.cc.common.library.gson.JsonArray;
import invaded.cc.common.library.gson.JsonElement;
import invaded.cc.common.library.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PunishmentsCommand extends BasicCommand {

    public PunishmentsCommand() {
        super("punishments", PermLevel.STAFF, "c");
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

            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
            Profile target = profileHandler.getProfile(Bukkit.getOfflinePlayer(args[0]).getUniqueId());
            if (target == null) target = profileHandler.load(Bukkit.getOfflinePlayer(args[0]).getUniqueId(), args[0]);

            player.sendMessage(Color.translate("&aRetrieving data, a menu will be displayed when the data is ready."));

            Map<String, Object> query = Maps.newHashMap();
            query.put("cheaterUuid", target.getId().toString());
            HttpResponse response = RequestHandler.get("/punishments", query);
            List<Punishment> punishmentList = new ArrayList<>();

            if (response.statusCode() == 200) {
                JsonArray jsonArray = new JsonParser().parse(response.bodyText()).getAsJsonArray();
                for (JsonElement element : jsonArray)
                    punishmentList.add(Spotify.GSON.fromJson(element, Punishment.class));
            }

            if (target.getMute() != null) punishmentList.add(target.getMute());
            if (target.getBan() != null) punishmentList.add(target.getBan());

            Profile finalTarget = target;
            Task.run(() -> new PunishmentsMenu(finalTarget, punishmentList).open(player));

            //player.sendMessage(Color.translate("&cCouldn't found any punishments on record for that player."));

            response.close();
        });
    }
}
