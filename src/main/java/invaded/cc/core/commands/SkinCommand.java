package invaded.cc.core.commands;

import invaded.cc.core.Spotify;
import invaded.cc.core.manager.SkinHandler;
import invaded.cc.core.tasks.SkinFetcherTask;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Skin;
import invaded.cc.core.util.Task;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SkinCommand extends BasicCommand {

    private final Spotify plugin;

    public SkinCommand(Spotify plugin) {
        super("skin", PermLevel.VIP, "changeskin");

        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(CC.RED + "Please use /skin <name>");
            return;
        }

        final Player player = (Player) sender;
        String name = args[0];

        Task.async(() -> {
            Skin skin;
            SkinHandler skinHandler = plugin.getDisguiseHandler().getSkinManager();
            player.sendMessage(CC.GREEN +"We're getting the skin of " + name + ", wait...");

            skin = SkinFetcherTask.inmeadiateRequest(player, name);

            skinHandler.applySkin(player, skin);
            player.sendMessage(CC.GREEN + "You now look like " + CC.YELLOW + name + CC.GREEN + ".");
        });
    }
}
