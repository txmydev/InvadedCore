package invaded.cc.commands;

import invaded.cc.Core;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.util.Color;
import invaded.cc.util.Common;
import invaded.cc.util.Filter;
import invaded.cc.util.command.InvadedCommand;
import invaded.cc.util.perms.PermLevel;
import invaded.cc.util.perms.Permission;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageCommand {

    private ProfileHandler profileHandler = Core.getInstance().getProfileHandler();

    public MessageCommand() {
        new MSGCommand();
        new ReplyCommand();
        new PMCommand();
        new SoundCommand();
        new FilterCommand();
    }

    private class MSGCommand extends InvadedCommand {

        public MSGCommand() {
            super("message", PermLevel.DEFAULT, "msg", "w", "tell");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.translate("&cPlayer only command."));
                return;
            }

            Player player = (Player) sender;

            if (args.length < 2) {
                player.sendMessage(Color.translate("&CPlease use /msg <player> <message>"));
                return;
            }

            Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(Color.translate("&cThat player is offline."));
                return;
            }

            Profile profile = profileHandler.getProfile(player.getUniqueId());
            Profile targetData = profileHandler.getProfile(target.getUniqueId());

            if(targetData.isDisguised() && !targetData.getName().equalsIgnoreCase(args[0])){
                player.sendMessage(Color.translate("&cThat player is offline."));
                return;
            }

            if(player.getUniqueId() == target.getUniqueId()){
                player.sendMessage(Color.translate("&cYou cannot message yourself."));
                return;
            }

            if(!profile.isMessages()){
                player.sendMessage(Color.translate("&cYour pm's are off."));
                return;
            }

            if(!targetData.isMessages()){
                player.sendMessage(Color.translate("&cThat player has pm's toggled off."));
                return;
            }

            if (profile.getIgnoreList().contains(targetData.getName())) {
                player.sendMessage(Color.translate("&cYou cannot send a message to a player you're ignoring."));
                return;
            }

            if(profile.isMuted()){
                player.sendMessage(Color.translate("&cYou cannot send private messages as your are currently muted."));
                return;
            }

            String from = "&7(From " + profile.getColoredName() + "&7) ";
            String to = "&7(To " + targetData.getColoredName() + "&7) ";

            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 1; i < args.length; i++) {
                stringBuilder.append(args[i]).append(" ");
            }

            from = from + stringBuilder.toString();
            to = to + stringBuilder.toString();

            player.sendMessage(Color.translate(to));
            if(!targetData.getIgnoreList().contains(player.getName())) target.sendMessage(Color.translate(from));

            if(profile.isMessagesSound()) player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
            if(!targetData.getIgnoreList().contains(player.getName()) && targetData.isMessagesSound()) target.playSound(target.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

            profile.setRecentTalker(targetData);
            targetData.setRecentTalker(profile);

            if (Filter.needFilter(stringBuilder.toString().trim())) {
                String filter = Filter.PREFIX + " &e(" + profile.getColoredName() + " &eto " + targetData.getColoredName() + "&e) " + stringBuilder.toString();

                Common.getOnlinePlayers().forEach(other -> {
                    if (!Permission.test(other, PermLevel.STAFF)) return;

                    Profile otherData = profileHandler.getProfile(other.getUniqueId());
                    if(!otherData.isFilter()) return;

                    other.sendMessage(Color.translate(filter));
                });
            }
        }
    }

    private class ReplyCommand extends InvadedCommand {

        public ReplyCommand() {
            super("reply", PermLevel.DEFAULT, "r");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Color.translate("&CPlayer only command."));
                return;
            }

            if (args.length < 1) {
                sender.sendMessage(Color.translate("&cPlease use /r <message>"));
                return;
            }

            Player player = (Player) sender;
            Profile profile = profileHandler.getProfile(player.getUniqueId());

            Profile targetData = profile.getRecentTalker();
            Player target = Bukkit.getPlayer(targetData.getId());

            if (target == null) {
                player.sendMessage(Color.translate("&cYour recently talker is offline."));
                return;
            }

            if(!profile.isMessages()){
                player.sendMessage(Color.translate("&cYour pm's are off."));
                return;
            }

            if(!targetData.isMessages()){
                player.sendMessage(Color.translate("&cThat player has pm's toggled off."));
                return;
            }


            if (profile.getIgnoreList().contains(targetData.getName())) {
                player.sendMessage(Color.translate("&cYou cannot send a message to a player you're ignoring."));
                return;
            }

            String from = "&7(From " + profile.getColoredName() + "&7) ";
            String to = "&7(To " + targetData.getColoredName() + "&7) ";

            StringBuilder stringBuilder = new StringBuilder();

            for (String arg : args) {
                stringBuilder.append(arg).append(" ");
            }

            from = from + stringBuilder.toString();
            to = to + stringBuilder.toString();

            player.sendMessage(Color.translate(to));

            if (!targetData.getIgnoreList().contains(profile.getName()))
              target.sendMessage(Color.translate(from));

            profile.setRecentTalker(targetData);
            targetData.setRecentTalker(profile);

            if(profile.isMessagesSound()) player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);
            if(targetData.isMessagesSound()) target.playSound(target.getLocation(), Sound.ORB_PICKUP, 1.0f, 1.0f);

            if (Filter.needFilter(stringBuilder.toString())) {
                String filter = Filter.PREFIX + " &e(" + profile.getColoredName() + " &eto " + targetData.getColoredName() + "&e) " + stringBuilder.toString();

                Common.getOnlinePlayers().forEach(other -> {
                    if (!Permission.test(other, PermLevel.STAFF)) return;

                    other.sendMessage(Color.translate(filter));
                });
            }
        }
    }

    private class PMCommand extends InvadedCommand {

        public PMCommand(){
            super("privatemessage", PermLevel.DEFAULT, "pm");
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if(!(sender instanceof Player)) return;

            if(args.length != 0) {
                sender.sendMessage(Color.translate("&cPlease use /pm"));
                return;
            }

            Player player = (Player) sender;
            Profile profile = profileHandler.getProfile(player.getUniqueId());

            boolean v = !profile.isMessages();
            profile.setMessages(v);

            player.sendMessage(Color.translate((v ? "&a" : "&7") + "You toggled your private messages."));
        }

    }

    private class SoundCommand extends InvadedCommand {

        public SoundCommand(){
            super("privatemessagesound", PermLevel.DEFAULT, "pmsound");
        }


        @Override
        public void execute(CommandSender sender, String[] args) {
            if(!(sender instanceof Player)) return;

            if(args.length != 0) {
                sender.sendMessage(Color.translate("&cPlease use /pm"));
                return;
            }

            Player player = (Player) sender;
            Profile profile = profileHandler.getProfile(player.getUniqueId());

            boolean v = !profile.isMessagesSound();
            profile.setMessagesSound(v);

            player.sendMessage(Color.translate((v ? "&a" : "&7") + "You toggled your pm's sound."));
        }
    }

    private class FilterCommand extends InvadedCommand {

        public FilterCommand(){
            super("filter", PermLevel.STAFF);
        }

        @Override
        public void execute(CommandSender sender, String[] args) {
            if(!(sender instanceof Player)) return;

            if(args.length != 0){
                sender.sendMessage(Color.translate("&cPlease use /filter instead."));
                return;
            }

            Player player = (Player) sender;
            Profile profile = profileHandler.getProfile(player.getUniqueId());

            boolean v = !profile.isFilter();
            profile.setFilter(v);

            player.sendMessage(Color.translate((v ? "&a" : "&c") + "You toggled your filter alerts."));
        }
    }
}
