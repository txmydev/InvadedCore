package invaded.cc.core.commands.staff;

import invaded.cc.core.Spotify;
import invaded.cc.core.network.packet.PacketStaffChat;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.Color;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StaffChatCommand extends BasicCommand {

    public StaffChatCommand() {
        super("staffchat", PermLevel.STAFF, "sc");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            if (args.length < 1) {
                sender.sendMessage(Color.translate("&cPlease use /sc <message>"));
                return;
            }

            StringBuilder stringBuilder = new StringBuilder();

            for (String s : args) stringBuilder.append(s).append(" ");

           // ChatColor prefixColor = ChatColor.GRAY;
            //ChatColor messageColor = ChatColor.DARK_PURPLE;
            Spotify.getInstance().getNetworkHandler().sendPacket(new PacketStaffChat("&4Console", Spotify.SERVER_NAME, stringBuilder.toString()));

            return;
        }

        Player player = (Player) sender;
        Profile profile = Spotify.getInstance().getProfileHandler().getProfile(player.getUniqueId());

        if (args.length == 0) {
            boolean v = !profile.isStaffChat();
            profile.setStaffChat(v);
            player.sendMessage(Color.translate((v ? "&a" : "&7") + "You've toggled your staff mode."));
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();

        for (String s : args) stringBuilder.append(s).append(" ");

        Spotify.getInstance().getNetworkHandler().sendPacket(new PacketStaffChat(profile.getColoredName(), Spotify.SERVER_NAME, stringBuilder.toString()));
    }
}
