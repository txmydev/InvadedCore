package invaded.cc.core.commands.tags;

import com.google.common.collect.Maps;
import invaded.cc.core.Spotify;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.menu.CosmeticsMenu;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.tags.Tag;
import invaded.cc.core.tags.TagsHandler;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import jodd.http.HttpResponse;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TagsCommand extends BasicCommand {

    public TagsCommand() {
        super("tags", PermLevel.DEFAULT, "tag", "prefix", "suffix", "prefixs", "suffixs");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) return;

        Player player = (Player) sender;
        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        TagsHandler tagsHandler = Spotify.getInstance().getTagsHandler();

        if (args.length == 0) {
            Profile profile = profileHandler.getProfile(player);
            new CosmeticsMenu(profile).open(player);
            return;
        }

        String arg1 = args[0].toLowerCase();
        if (arg1.equals("create")) {
            if (!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if (args.length != 5) {
                player.sendMessage(Color.translate("&c/tags create <prefix:suffix> <id> <display> <price>"));
                return;
            }

            String type = args[1];
            String id = args[2];
            String display = args[3];
            int price = getInt(args[4]);

            if (price == -1) {
                player.sendMessage(Color.translate("&cYou may enter a valid price."));
                return;
            }

            if (tagsHandler.getTag(id) != null) {
                player.sendMessage(Color.translate("&cThat tag already exists, you may use /prefix modify."));
                return;
            }

            Tag tag;
            if (type.equalsIgnoreCase("suffix")) {
                Tag suffix = new Tag(id, display, price, true);
                tagsHandler.getTags().add(suffix);
                tag = suffix;
            } else {
                Tag prefix = new Tag(id, display, price, false);
                tagsHandler.getTags().add(prefix);
                tag = prefix;
            }

            player.sendMessage(Color.translate("&aYou've created the " + tag.getType() + " &6" + id + " &awith display &r" + display + " &aand price &6" + price + " coins&a."));
        } else if (arg1.equals("delete") || arg1.equals("remove")) {
            if (!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if (args.length != 2) {
                player.sendMessage(Color.translate("&c/tags delete/remove <id>"));
                return;
            }

            String id = args[1];
            Tag tag = tagsHandler.getTag(id);
            if (tag == null) {
                player.sendMessage(Color.translate("&cThat prefix doesn't exist."));
                return;
            }

            Task.async(() -> tagsHandler.remove(tag));
            player.sendMessage(Color.translate("&cYou've removed " + tag.getType() + " &6" + id + "&c."));
        } else if (arg1.equals("modify")) {
            if (!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if (args.length != 4) {
                player.sendMessage(Color.translate("&c/prefix modify <id> <display> <price>"));
                return;
            }

            String id = args[1];
            String display = args[2];
            int price = getInt(args[3]);

            Tag tag = tagsHandler.getTag(id);
            if (tag == null) {
                player.sendMessage(Color.translate("&cThat tag doesn't exist."));
                return;
            }

            tag.setDisplay(display.equals("@@stay") ? tag.getDisplay() : display);
            tag.setPrice(price);
            player.sendMessage(Color.translate("&aYou've modified &6" + id + "&a's display to &r" + display + " &aand price &6" + price + " coins&a."));
        } else if (arg1.equals("deleteall")) {
            if (!Permission.test(sender, PermLevel.ADMIN)) {
                player.sendMessage(Color.translate("&cYou don't have permissions to perform this action."));
                return;
            }

            if (args.length != 1) {
                player.sendMessage(Color.translate("&c/prefix deleteall"));
                return;
            }

            player.sendMessage(Color.translate("&aDeleting all prefixes and removing the ones that are stored by users is a task which has to be run asynchronously so it may take to time, don't panic."));
            deleteAll(player);
        }
    }

    private int getInt(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private void deleteAll(Player player) {
        Task.async(() -> {

            if (deletePrefixes().equals("ok"))
                player.sendMessage(Color.translate("&aAll the prefixes have been removed successfully, so now we are removing all of them from the players, this may take several minutes."));
            else
                player.sendMessage(Color.translate("&cAn error has ocurred while trying to delete all the prefixes, you should check http console."));

            if (deleteFromPlayers().equals("ok")) {
                if (player != null)
                    player.sendMessage(Color.translate("&aSuccessfully deleted all the prefixes from all the users."));
                else
                    Bukkit.getConsoleSender().sendMessage(Color.translate("&aSuccessfully deleted all the prefixes from all the users."));
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
            if (Bukkit.getPlayer(name) != null) {
                Task.run(() -> Bukkit.getPlayer(name).kickPlayer(Color.translate("&cAn admin has ran a command to delete all the prefixes from users, \nyou cannot enter right now.")));
                Spotify.getInstance().getProfileHandler().getDeletingPrefix().add(UUID.fromString(id));
            }

            Map<String, Object> body = new HashMap<>();
            body.put("uuid", id);
            body.put("name", name);
            body.put("tags", new ArrayList<String>());
            body.put("activePrefix", "none");
            body.put("activeSuffix", "none");

            HttpResponse changeResponse = RequestHandler.post("/profiles", body);
            changeResponse.close();

            Spotify.getInstance().getProfileHandler().getDeletingPrefix().remove(UUID.fromString(id));
        });

        return "ok";
    }

    private String deletePrefixes() {
        HttpResponse response = RequestHandler.delete("/tags/deleteall", Maps.newHashMap());
        JsonParser parser = new JsonParser();
        JsonObject jsonObject = parser.parse(response.bodyText()).getAsJsonObject();

        if (jsonObject.get("message").getAsString().equals("ok")) {
            response.close();
            return "ok";
        } else {
            response.close();
            return "not ok";
        }
    }
}
