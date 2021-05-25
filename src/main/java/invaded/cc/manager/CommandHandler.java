package invaded.cc.manager;

import invaded.cc.Core;
import invaded.cc.commands.*;
import invaded.cc.commands.messaging.*;
import invaded.cc.commands.perms.PermissionCommand;
import invaded.cc.commands.prefix.GrantPrefixCommand;
import invaded.cc.commands.prefix.PrefixCommand;
import invaded.cc.commands.punish.ban.BanCMD;
import invaded.cc.commands.punish.ban.TemporalBanCMD;
import invaded.cc.commands.punish.blacklist.BlacklistCMD;
import invaded.cc.commands.punish.blacklist.UnBlacklistCMD;
import invaded.cc.commands.punish.check.PunishmentsCommand;
import invaded.cc.commands.punish.kick.KickCMD;
import invaded.cc.commands.punish.mute.MuteCMD;
import invaded.cc.commands.punish.mute.TemporalMuteCMD;
import invaded.cc.commands.punish.unpunish.UnBanCMD;
import invaded.cc.commands.punish.unpunish.UnMuteCMD;
import lombok.Getter;
import me.txmy.command.BaseCommand;
import me.txmy.command.CommandFramework;

import java.util.ArrayList;
import java.util.List;

public class CommandHandler {

    @Getter
    private final List<String> flyWorlds = new ArrayList<>();
    private CommandFramework commandFramework;

    public CommandHandler(){
        this.commandFramework = new CommandFramework(Core.getInstance());
        BaseCommand.setFramework(commandFramework);

        new GamemodeCommand();
        new FlyCommand();
        new StaffChatCommand();
        new BroadcastCommand();
        new PingCommand();
        new TeleportCommand();
        new ChatControlCommand();
        new HealCommand();
        new ReportCommand();
        new IgnoreCommand();
        new BlacklistCMD();
        new UnBlacklistCMD();
        new ColorCommand();
        new GrantCommand();
        new UnBanCMD();
        new BanCMD();
        new TemporalBanCMD();
        new AnnounceCommand();
        new MuteCMD();
        new TemporalMuteCMD();
        new UnMuteCMD();
        new KickCMD();
        new DisguiseCommand();
        new UnDisguiseCommand();
        new HelpOpCommand();
        new ToggleStaffCommand();
        new JoinCommand();
        new HubCommand();
        new FeedCommand();
        new ListCommand();
        new PunishmentsCommand();
        new GrantsCommand();
        new PermissionCommand();
        new SudoCommand();
        new SkullCommand();
        new PrivateMessageCommand();
        new ReplyCommand();
        new ToggleMessageCommand();
        new FilterCommand();
        new SoundCommand();
        new ReloadRanksCommand();
        new PrefixCommand();
        new GrantPrefixCommand();
        new DisguiseCheckCommand();
    }

}
