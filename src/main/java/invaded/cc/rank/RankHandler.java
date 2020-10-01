package invaded.cc.rank;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import invaded.cc.manager.RequestHandler;
import jodd.http.HttpResponse;
import org.bukkit.ChatColor;

import java.util.*;
import java.util.stream.Collectors;

public class RankHandler {

    private final Map<String, Rank> ranks;
    private Rank defaultRank;
    private final List<Rank> priorityOrdered;

    public static Comparator<Rank> PRIORITY_COMPARATOR = (o1, o2) -> o2.getPriority() - o1.getPriority();

    public RankHandler() {
        this.ranks = new HashMap<>();

//        MongoCollection<Document> collection = Core.getInstance().getDb().getCollection("ranks");
//
//        collection.find().forEach((Consumer<? super Document>) doc -> {
//            load(doc.getString("name"));
//        });
        setupRanks();
        loadAll();

        priorityOrdered = ranks.values().stream().sorted(PRIORITY_COMPARATOR).collect(Collectors.toList());
    }

    private void loadAll() {
        HttpResponse httpResponse = RequestHandler.get("/ranks");

        JsonArray jsonObject = new JsonParser().parse(httpResponse.bodyText()).getAsJsonArray();

        jsonObject.forEach(element -> {
            newLoad(element.getAsJsonObject());
        });

        httpResponse.close();
    }

    private void newLoad(JsonObject jsonObject) {
        String name = jsonObject.get("name").getAsString();
        int priority = jsonObject.get("priority").getAsInt();

        ranks.putIfAbsent(name, new Rank(name));
        Rank rank = ranks.get(name);
        rank.setPriority(priority);

        JsonArray permissionArray = jsonObject.getAsJsonArray("permissions");
        List<String> perms = Lists.newArrayList();
        permissionArray.forEach(element -> perms.add(element.getAsString()));

        rank.setPermissions(perms);

        rank.setPrefix(jsonObject.get("prefix").getAsString());
        rank.setColor(ChatColor.valueOf(jsonObject.get("color").getAsString()));
        rank.setSuffix(jsonObject.get("suffix").getAsString());

        rank.setBold(jsonObject.get("bold").getAsBoolean());
        rank.setItalic(jsonObject.get("italic").getAsBoolean());

        rank.setDefaultRank(jsonObject.get("defaultRank").getAsBoolean());

        if(rank.isDefaultRank()) this.defaultRank = rank;
    }
/*
    public void load(String name) {
        ranks.putIfAbsent(name, new Rank(name));
        Rank rank = ranks.get(name);

        MongoCollection<Document> collection = Core.getInstance().getDb().getCollection("ranks");
        Document found = collection.find(Filters.eq("name", name)).first();

        if (found != null) {
            if (found.containsKey("prefix")) rank.setPrefix(found.getString("prefix"));
            if (found.containsKey("suffix")) rank.setSuffix(found.getString("suffix"));
            if (found.containsKey("color"))
                rank.setColor(found.getString("color").equals("none") ? ChatColor.WHITE : ChatColor.valueOf(found.getString("color")));
            if (found.containsKey("bold")) rank.setBold(found.getBoolean("bold"));
            if (found.containsKey("italic")) rank.setItalic(found.getBoolean("italic"));
            if (found.containsKey("defaultRank")) rank.setDefaultRank(found.getBoolean("defaultRank"));
            if (found.containsKey("permissions")) rank.setPermissions(found.getList("permissions", String.class));
            if (found.containsKey("priority")) rank.setPriority(found.getInteger("priority"));
        } else {
            rank.setPrefix("");
            rank.setSuffix("");
            rank.setPermissions(Lists.newArrayList());
            rank.setColor(ChatColor.WHITE);
        }

        if (rank.isDefaultRank()) defaultRank = rank;
    }*/

    public void save(Rank rank) {
        Map<String, Object> map = new HashMap<>();

        map.put("name", rank.getName());
        map.put("priority", rank.getPriority());
        map.put("prefix", rank.getPrefix());
        map.put("suffix", rank.getSuffix());
        map.put("defaultRank", rank.isDefaultRank());
        map.put("italic", rank.isItalic());
        map.put("bold", rank.isBold());
        map.put("color", rank.getColor().name());
        map.put("permissions", rank.getPermissions());

        HttpResponse response = RequestHandler.post("/ranks", map);
        response.close();
    }

    /*public void save(Rank rank) {
        MongoCollection<Document> collection = Core.getInstance().getDb().getCollection("ranks");
        Document found = collection.find(Filters.eq("name", rank.getName())).first();

        Document doc = new Document("name", rank.getName())
                .append("priority", rank.getPriority())
                .append("prefix", rank.getPrefix())
                .append("suffix", rank.getSuffix())
                .append("defaultRank", rank.isDefaultRank())
                .append("italic", rank.isItalic())
                .append("bold", rank.isBold())
                .append("color", rank.getColor().name())
                .append("permissions", rank.getPermissions());

        if (found != null) collection.replaceOne(found, doc);
        else collection.insertOne(doc);
    }*/

    public Rank getRank(String name) {
        return ranks.get(name);
    }

    public Rank getDefault() {
        return defaultRank;
    }

    public Rank getRankOrDefault(String name) {
        return Optional.of(getRank(name)).orElse(getDefault());
    }

    public List<Rank> getPriorityOrdered() {
        return priorityOrdered;
    }


    public void setupRanks() {
        if (!ranks.containsKey("Default")) {
            ranks.put("Default", new Rank("Default"));
            Rank rank = getRank("Default");
            rank.setPermissions(Lists.newArrayList("invaded.default", "uhc.spectate.100"));
            rank.setDefaultRank(true);
            rank.setPriority(0);
        }

        if (!ranks.containsKey("Stellar")) {
            ranks.put("Stellar", new Rank("Stellar"));
            Rank rank = ranks.get("Stellar");
            rank.setPermissions(Lists.newArrayList("invaded.default"
                    , "invaded.vip", "uhc.spectate.500", "uhc.vip"));
            rank.setPrefix("&8[&7Stellar&8] ");
            rank.setColor(ChatColor.GRAY);
            rank.setPriority(10);
        }

        if (!ranks.containsKey("Mythic")) {
            ranks.put("Mythic", new Rank("Mythic"));
            Rank rank = ranks.get("Mythic");

            rank.setPermissions(Lists.newArrayList("invaded.default"
                    , "invaded.vip", "uhc.spectate.1000", "uhc.vip"));
            rank.setPrefix("&8[&5Mythic&8] ");
            rank.setColor(ChatColor.DARK_PURPLE);
            rank.setPriority(20);
        }

        if (!ranks.containsKey("Ultra")) {
            ranks.put("Ultra", new Rank("Ultra"));
            Rank rank = ranks.get("Ultra");

            rank.setPermissions(Lists.newArrayList("invaded.default"
                    , "invaded.vip", "uhc.spectate.1000", "uhc.vip"));
            rank.setPrefix("&8[&eUltra&8] ");
            rank.setColor(ChatColor.YELLOW);
            rank.setPriority(30);
        }

        if (!ranks.containsKey("Moon")) {
            ranks.put("Moon", new Rank("Moon"));
            Rank rank = ranks.get("Moon");

            rank.setPermissions(Lists.newArrayList("invaded.default"
                    , "invaded.vip", "uhc.spectate.1000", "uhc.vip"));
            rank.setPrefix("&8[&bMoon&8] ");
            rank.setColor(ChatColor.AQUA);
            rank.setPriority(40);
        }


        if (!ranks.containsKey("Media")) {
            ranks.put("Media", new Rank("Media"));
            Rank rank = ranks.get("Media");

            rank.setPermissions(Lists.newArrayList(
                    "invaded.default"
                    , "invaded.media"
                    , "uhc.spectate.1500"
                    , "uhc.vip"
                    , "disguise.use.Default"
                    , "disguise.use.Stellar"
                    , "disguise.use.Ultra"
                    , "disguise.use.Mythic"
                    , "disguise.use.Moon"));

            rank.setPrefix("&7[&dMedia&7] ");
            rank.setColor(ChatColor.LIGHT_PURPLE);
            rank.setPriority(50);
        }

        if (!ranks.containsKey("Famous")) {
            ranks.put("Famous", new Rank("Famous"));
            Rank rank = ranks.get("Famous");

            rank.setPermissions(Lists.newArrayList(
                    "invaded.default"
                    , "invaded.media"
                    , "uhc.spectate.1500"
                    , "uhc.vip"
                    , "disguise.use.Default"
                    , "disguise.use.Stellar"
                    , "disguise.use.Ultra"
                    , "disguise.use.Mythic"
                    , "disguise.use.Moon"));

            rank.setPrefix("&7[&bFamous&7] ");
            rank.setColor(ChatColor.AQUA);
            rank.setPriority(60);
        }

        if (!ranks.containsKey("Partner")) {
            ranks.put("Partner", new Rank("Partner"));
            Rank rank = ranks.get("Partner");

            rank.setPermissions(Lists.newArrayList(
                    "invaded.default"
                    , "invaded.media"
                    , "uhc.spectate.1500"
                    , "uhc.vip"
                    , "disguise.use.Default"
                    , "disguise.use.Stellar"
                    , "disguise.use.Ultra"
                    , "disguise.use.Mythic"
                    , "disguise.use.Moon"));

            rank.setPrefix("&7[&aPartner&7] ");
            rank.setColor(ChatColor.GREEN);
            rank.setPriority(70);
        }

        if (!ranks.containsKey("Trial")) {
            ranks.put("Trial", new Rank("Trial"));
            Rank rank = ranks.get("Trial");

            rank.setPermissions(Lists.newArrayList(
                    "invaded.staff"
                    , "uhc.spectate.1500"
                    , "uhc.vip"
                    , "uhc.mod"
                    , "uhc.staff"
                    , "uhc.host"
                    , "uhc.togglespectatorchat"
                    , "uhc.command.latescatter"
                    , "uhc.command.tp"));

            rank.setPrefix("&7[&6Trial-Mod&7] ");
            rank.setColor(ChatColor.YELLOW);
            rank.setPriority(80);
        }

        if (!ranks.containsKey("Mod")) {
            ranks.put("Mod", new Rank("Mod"));
            Rank rank = ranks.get("Mod");

            rank.setPermissions(Lists.newArrayList(
                    "invaded.default"
                    , "invaded.staff"
                    , "uhc.spectate.1500"
                    , "uhc.vip"
                    , "uhc.mod"
                    , "uhc.staff"
                    , "uhc.host"
                    , "uhc.togglespectatorchat"
                    , "uhc.command.latescatter"
                    , "uhc.command.tp"));

            rank.setPrefix("&8[&bMod&8] ");
            rank.setColor(ChatColor.DARK_AQUA);
            rank.setPriority(90);
        }

        if (!ranks.containsKey("Mod+")) {
            ranks.put("Mod+", new Rank("Mod+"));
            Rank rank = ranks.get("Mod+");

            rank.setPermissions(Lists.newArrayList(
                    "invaded.default"
                    , "invaded.staff"
                    , "uhc.spectate.1500"
                    , "uhc.vip"
                    , "uhc.mod"
                    , "uhc.staff"
                    , "uhc.host"
                    , "uhc.togglespectatorchat"
                    , "uhc.command.latescatter"
                    , "uhc.command.tp"));

            rank.setPrefix("&8[&dMod+&8] ");
            rank.setColor(ChatColor.LIGHT_PURPLE);
            rank.setPriority(100);
        }

        if (!ranks.containsKey("Admin")) {
            ranks.put("Admin", new Rank("Admin"));
            Rank rank = ranks.get("Admin");

            rank.setPermissions(Lists.newArrayList(
                    "invaded.admin"
                    , "uhc.spectate.1500"
                    , "uhc.command.giveall"
                    , "bukkit.command.give"
                    , "uhc.vip"
                    , "uhc.mod"
                    , "uhc.staff"
                    , "uhc.host"
                    , "uhc.togglespectatorchat"
                    , "uhc.command.latescatter"
                    , "uhc.command.tp"));

            rank.setPrefix("&7[&cAdmin&7]");
            rank.setColor(ChatColor.RED);
            rank.setPriority(110);
        }

        if (!ranks.containsKey("Platform-Admin")) {
            ranks.put("Platform-Admin", new Rank("Platform-Admin"));
            Rank rank = ranks.get("Platform-Admin");

            rank.setPermissions(Lists.newArrayList(
                    "invaded.admin"
                    , "uhc.spectate.1500"
                    , "uhc.command.giveall"
                    , "bukkit.command.give"
                    , "uhc.vip"
                    , "uhc.mod"
                    , "uhc.staff"
                    , "uhc.host"
                    , "uhc.togglespectatorchat"
                    , "uhc.command.latescatter"
                    , "uhc.command.tp"));

            rank.setPrefix("&7[&c&oPlatform-Admin&7] ");
            rank.setColor(ChatColor.RED);
            rank.setPriority(120);
        }

        if (!ranks.containsKey("Owner")) {
            ranks.put("Owner", new Rank("Owner"));
            Rank rank = ranks.get("Owner");

            rank.setPermissions(Lists.newArrayList(
                    "*"
                    , "uhc.command.tp"));

            rank.setPrefix("&7[&4Owner&7] ");
            rank.setColor(ChatColor.DARK_RED);
            rank.setPriority(130);
        }
    }
}
