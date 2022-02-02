package invaded.cc.core.commands.messaging;

import invaded.cc.core.Spotify;
import invaded.cc.core.manager.SocialSpyHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SocialSpyCommand extends BasicCommand {

    public SocialSpyCommand() {
        super("socialspy", PermLevel.STAFF, "msgspy", "spy");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        switch (args.length) {
            case 0:
                ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
                Profile profile = profileHandler.getProfile((Player) sender);

                profile.setSocialSpy(!profile.isSocialSpy());
                sender.sendMessage((profile.isSocialSpy() ? CC.GREEN : CC.GRAY) + "You've toggled your social spy.");
                break;
            case 1:
                if (!Permission.isOwner(sender)) {
                    sender.sendMessage(CC.RED + "Please use /socialspy");
                    return;
                }
                SocialSpyHandler spyHandler = Spotify.getInstance().getSocialSpyHandler();
                switch (args[0]) {
                    case "toggle":
                        boolean v = !spyHandler.isEnabled();
                        spyHandler.setEnabled(v);

                        sender.sendMessage((v ? CC.GREEN : CC.RED) + "You've " + (v ? "enabled" : "disabled") + " global social spy.");
                        break;
                    case "enable":
                        spyHandler.setEnabled(true);
                        sender.sendMessage(CC.GREEN + "You've enabled global social spy.");
                        break;
                    case "disable":
                        spyHandler.setEnabled(false);
                        sender.sendMessage(CC.RED + "You've disabled global social spy.");
                        break;
                }
                break;
        }
    }
}
