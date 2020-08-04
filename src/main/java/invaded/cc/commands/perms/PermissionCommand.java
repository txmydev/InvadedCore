package invaded.cc.commands.perms;

import invaded.cc.Core;
import invaded.cc.menu.PermissionMenu;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.rank.Rank;
import invaded.cc.rank.RankHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Task;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PermissionCommand extends InvadedCommand {

    public PermissionCommand() {
        super("permission", PermLevel.ADMIN, "perm");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Task.async(() -> {
            if (args.length == 0) {
                sender.sendMessage(Color.translate("&cPlease use the correct syntax."));
                return;
            }

            ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
            RankHandler rankHandler = Core.getInstance().getRankHandler();

            Profile profile;
            Rank rank;

            switch (args[0]) {
                case "show":
                    if (args.length != 2) break;

                    String arg = args[1];
                    rank = rankHandler.getRank(arg);

                    if(rank != null) {
                        handleShow(sender, rank);
                        break;
                    }

                    Player player = Bukkit.getPlayer(arg);

                    if(player == null) {
                        sender.sendMessage(Color.translate("&cThat player is not online!"));
                        return;
                    }

                    profile = profileHandler.getProfile(player.getUniqueId());
                    handleShow(sender, profile);
                    break;
                case "add":
                    if (args.length != 3) break;

                    arg = args[1];
                    rank = rankHandler.getRank(arg);

                    if(rank == null) {
                        sender.sendMessage(Color.translate("&cRank isn't valid."));
                        break;
                    }

                    String perm = args[2];
                    if(rank.getPermissions().contains(perm)) sender.sendMessage(Color.translate("&cThat rank already has that permission."));
                    else {
                        rank.getPermissions().add(perm);
                        sender.sendMessage(Color.translate("&aCorrectly added the permission!"));
                    }
                    break;
                case "remove":
                    if (args.length != 3) break;

                    arg = args[1];
                    rank = rankHandler.getRank(arg);

                    if(rank == null) {
                        sender.sendMessage(Color.translate("&cRank isn't valid."));
                        break;
                    }
                    perm = args[2];
                    if(!rank.getPermissions().remove(perm)) sender.sendMessage(Color.translate("&cThat rank does not have that permission."));
                    else {
                        sender.sendMessage(Color.translate("&aCorrectly removed the permission!"));
                    }
                    break;
                default:
                    sender.sendMessage(Color.translate("&cCouldn't find subcommand."));
                    break;
            }
        });
    }

    public void handleShow(CommandSender sender, Profile profile) {
        if(sender instanceof Player) new PermissionMenu.ProfileMenu(profile).open((Player) sender);
        else profile.getPermissions().forEach(p -> sender.sendMessage(Color.translate("&f-" + p)));
    }

    public void handleShow(CommandSender sender, Rank rank) {
        if(sender instanceof Player) new PermissionMenu.RankMenu(rank).open((Player) sender);
        else rank.getPermissions().forEach(p -> sender.sendMessage(Color.translate("&f-" + p)));
    }
}
