package invaded.cc.commands.prefix;

import com.google.common.collect.Maps;
import invaded.cc.Basic;
import invaded.cc.manager.RequestHandler;
import invaded.cc.menu.prefix.PrefixMenu;
import invaded.cc.prefix.Prefix;
import invaded.cc.prefix.PrefixHandler;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Task;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import jodd.http.HttpResponse;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class PrefixCommand extends BasicCommand {

    public PrefixCommand() {
        super("prefix", PermLevel.DEFAULT, "prefixs", "prefixes");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(!(sender instanceof Player)) return;

        Player player = (Player) sender;
        ProfileHandler profileHandler = Basic.getInstance().getProfileHandler();
        PrefixHandler prefixHandler = Basic.getInstance().getPrefixHandler();

        if(args.length == 0) {
            Profile profile = profileHandler.getProfile(player);
            new PrefixMenu(profile).open(player);
            return;
        }

        String arg1 = args[0].toLowerCase();
        if(arg1.equals("create")) {
            if(!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if(args.length != 4) {
                player.sendMessage(Color.translate("&c/prefix create <id> <display> <price>"));
                return;
            }

            String id = args[1];
            String display = args[2];
            int price = getInt(args[3]);

            if(price == -1){
                player.sendMessage(Color.translate("&cYou may enter a valid price."));
                return;
            }

            if(prefixHandler.getPrefix(id) != null) {
                player.sendMessage(Color.translate("&cThat prefix already exists, you may use /prefix modify."));
                return;
            }

            Prefix prefix = new Prefix(id, display, price);
            prefixHandler.getPrefixes().add(prefix);
            player.sendMessage(Color.translate("&aYou've created the prefix &6" +id+" &awith display &r" + display + " &aand price &6" + price + " coins&a."));
        } else if(arg1.equals("delete") || arg1.equals("remove")) {
            if(!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if(args.length != 2) {
                player.sendMessage(Color.translate("&c/prefix delete/remove <id>"));
                return;
            }

            String id = args[1];
            Prefix prefix = prefixHandler.getPrefix(id);
            if(prefix == null){
                player.sendMessage(Color.translate("&cThat prefix doesn't exist."));
                return;
            }

            Task.async(() -> prefixHandler.remove(prefix));
            player.sendMessage(Color.translate("&cYou've removed prefix &6"+id+"&c."));
        } else if(arg1.equals("modify")) {
            if(!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if(args.length != 4) {
                player.sendMessage(Color.translate("&c/prefix modify <id> <display> <price>"));
                return;
            }

            String id = args[1];
            String display = args[2];
            int price = getInt(args[3]);

            Prefix prefix = prefixHandler.getPrefix(id);
            if(prefix == null){
                player.sendMessage(Color.translate("&cThat prefix doesn't exist."));
                return;
            }

            prefix.setDisplay(display.equals("@@stay") ? prefix.getDisplay() : display);
            prefix.setPrice(price);
            player.sendMessage(Color.translate("&aYou've modified &6" + id + "&a's display to &r" + display + " &aand price &6" + price + " coins&a."));
        } else if(arg1.equals("deleteall")) {
            if(!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if(args.length != 2) {
                player.sendMessage(Color.translate("&c/prefix deleteall"));
                return;
            }

            player.sendMessage(Color.translate("&aDeleting all prefixes and removing the ones that are stored by users is a task which has to be run asynchronously so it may take to time, don't panic."));
            deleteAll(player);
        }
    }

    private int getInt(String arg) {
        try{
            return Integer.parseInt(arg);
        }catch(NumberFormatException ex) {
            return -1;
        }
    }

    private void deleteAll(Player player) {
        Task.async(() -> {

            if(deletePrefixes().equals("ok")) player.sendMessage(Color.translate("&aAll the prefixes have been removed successfully, so now we are removing all of them from the players, this may take several minutes."));
            else player.sendMessage(Color.translate("&cAn error has ocurred while trying to delete all the prefixes, you should check http console."));

            if(deleteFromPlayers().equals("ok")) {
                if(player != null) player.sendMessage(Color.translate("&aSuccessfully deleted all the prefixes from all the users."));
                else Bukkit.getConsoleSender().sendMessage(Color.translate("&aSuccessfully deleted all the prefixes from all the users."));
            }
        });
    }

    private String deleteFromPlayers() {
        HttpResponse response = RequestHandler.get("/profiles");

        JsonParser parser = new JsonParser();
        JsonArray array = parser.parse(response.bodyText()).getAsJsonArray();
        Map<String, String> data = new HashMap<>();

        array.forEach(element -> {
            JsonObject object = element.getAsJsonObject();
            data.put(object.get("uuid").getAsString(), object.get("name").getAsString());
        });

        response.close();
        data.forEach((id, name) -> {
            if(Bukkit.getPlayer(name) != null) {
                Task.run(() -> Bukkit.getPlayer(name).kickPlayer(Color.translate("&cAn admin has ran a command to delete all the prefixes from users, \nyou cannot enter right now.")));
                Basic.getInstance().getProfileHandler().getDeletingPrefix().add(UUID.fromString(id));
            }

            Map<String, Object> body = new HashMap<>();
            body.put("uuid", id);
            body.put("name", name);
            body.put("prefixes", new ArrayList<String>());
            body.put("activePrefix", "none");

            HttpResponse changeResponse = RequestHandler.post("/profiles", body);
            changeResponse.close();

            Basic.getInstance().getProfileHandler().getDeletingPrefix().remove(UUID.fromString(id));
        });

        return "ok";
    }

    private String deletePrefixes() {
        HttpResponse response = RequestHandler.delete("/prefixs/deleteall", Maps.newHashMap());
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(response.bodyText()).getAsJsonObject();

        if(jsonObject.get("message").getAsString().equals("ok")) {
            response.close();
            return "ok";
        }else {
            response.close();
            return "not ok";
        }
    }
}
