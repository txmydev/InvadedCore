package invaded.cc.rank;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;

import java.util.List;

@Getter
@Setter
public class Rank {

    /*@Getter
    private static Map<String, Rank> ranks = new HashMap<>();*/

    private final String name;
    private String prefix = "", suffix = "";
    private List<String> permissions;
    private ChatColor color = ChatColor.WHITE;
    private boolean defaultRank;
    private boolean italic = false, bold = false;
    private int priority;

    public Rank(String name) {
        this.name = name;
    }

  /*  private void load() {
        ConfigFile configFile = Core.getInstance().getRanksConfig();
        ConfigTracker configTracker = new ConfigTracker(configFile, "ranks");

        configTracker.setPath("ranks." + name);

        ConfigTracker optionsTracker = new ConfigTracker(configFile, "ranks." + name + ".options");

        this.prefix = optionsTracker.getString("prefix");
        this.suffix = optionsTracker.getString("suffix");

        this.priority = configTracker.getInt("priority");

        this.perms = configTracker.getStringList("permissions");

        if(perms != null ) {
            this.perms.forEach(permission -> {
                Permission perm = Bukkit.getPluginManager().getPermission(permission);
                if(perm == null) return;

                perm.getChildren().forEach((permName, value) -> { if (!value) return; perms.add(permName); });
            });
        }

        this.defaultRank = configTracker.getBoolean("default");

        optionsTracker.setPath("ranks." + name + ".options.color");

        try {
            this.color = ChatColor.valueOf(optionsTracker.getString("value"));
        }catch(IllegalArgumentException ex){
            this.color = ChatColor.getByChar(optionsTracker.getString("value").charAt(1));
        }
        this.italic = optionsTracker.getBoolean("italic");
        this.bold = optionsTracker.getBoolean("bold");
    }

    public void loadInheritance(){
        List<Rank> ranks = Rank.getFilteredByPriority(priority);
        ranks.removeIf(rank -> rank.getName().equalsIgnoreCase(name));

        ranks.forEach(rank -> {
            rank.getPerms().forEach(subPerm -> {
                if(!perms.contains(subPerm)) perms.add(subPerm);
            });
        });
    }

    public static Rank getRankOrDefault(String name){
        return ranks.getOrDefault(name, Rank.getDefault());
    }

    public static Rank getRank(String name) {
        return ranks.get(name);
    }

    public static void loadRanks(){
        ConfigFile configFile = Core.getInstance().getRanksConfig();
        ConfigTracker configTracker = new ConfigTracker(configFile, "ranks");

        for(String key : configTracker.getKeys()){
            new Rank(key);
        }

        getOrdered().forEach(Rank::loadInheritance);
    }

    public static Rank getDefault(){
        Optional<Rank> optional = ranks.values().stream().filter(Rank::isDefaultRank).findFirst();

        return optional.orElse(new Rank("Default"));
    }

    public static List<Rank> getOrdered(){
        List<Rank> ranks = new ArrayList<>();

        Rank.getRanks().values().stream().sorted(Comparator.comparingInt(Rank::getPriority))
        .forEach(ranks::add);

        Collections.reverse(ranks);

        return ranks;
    }

    public static List<Rank> getFilteredByPriority(int maxPriority) {
        List<Rank> ranks = getOrdered();
        ranks.removeIf(rank1 -> rank1.getPriority() > maxPriority);
        return ranks;
    }*/

    public String getColoredName() {
        return color + (italic? "" + ChatColor.ITALIC : "") + (bold ? "" + ChatColor.BOLD : "") + name;
    }

    public String getColors() {
        return color + (italic ? "" + ChatColor.ITALIC : "") + (bold ? "" + ChatColor.BOLD : "");
    }
}
