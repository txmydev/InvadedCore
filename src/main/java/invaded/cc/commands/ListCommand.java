package invaded.cc.commands;

import invaded.cc.Basic;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.rank.Rank;
import invaded.cc.rank.RankHandler;
import invaded.cc.util.Color;
import invaded.cc.util.command.BasicCommand;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class ListCommand extends BasicCommand {
    public ListCommand() {
        super("list", PermLevel.DEFAULT, "who");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ProfileHandler profileHandler = Basic.getInstance().getProfileHandler();

        if(args.length != 0)  {
            if (args.length == 1) {
                String arg0 = args[0];

                if (arg0.equalsIgnoreCase("disguised") && Permission.test(sender, PermLevel.STAFF)) {
                    sender.sendMessage(Color.translate("&6Disguised Players&7:"));

                    for (Profile profile : profileHandler.getProfiles().values()) {
                        if(profile.isDisguised())
                            sender.sendMessage(Color.translate(profile.getRealColoredName() + " &7is disguised as " + profile.getColoredName()));
                    }
                } else Bukkit.dispatchCommand(sender, "list");
            } else {
                Bukkit.dispatchCommand(sender, "list");
            }

            return;
        }

        RankHandler rankHandler = Basic.getInstance().getRankHandler();
        List<Rank> list = rankHandler.getRanks();

        boolean first = true;

        StringBuilder builder = new StringBuilder();

        for(Rank rank : list) {
            builder.append(first ? "" + rank.getColoredName() : ", " + rank.getColoredName());
            first = false;
        }

        sender.sendMessage(Color.translate(builder.toString()));

        first = true;
        builder = new StringBuilder();


        List<Profile> list1 = new ArrayList<>();//profileHandler.getProfiles().values().stream().filter(Profile::isOnline).sorted((p1, p2) -> p2.getHighestRank().getPriority() - p1.getHighestRank().getPriority()).collect(Collectors.toList()));
        Bukkit.getOnlinePlayers().forEach(player -> list1.add(profileHandler.getProfile(player)));

        for(Profile profile : list1) {
            builder.append(first ? profile.getColoredName() : "&7, " + profile.getColoredName());

            first = false;
        }

        sender.sendMessage(Color.translate(builder.toString()));
    }
}
