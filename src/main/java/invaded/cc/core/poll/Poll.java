package invaded.cc.core.poll;

import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.Cooldown;
import invaded.cc.core.util.perms.PermLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RequiredArgsConstructor
@Data
public class Poll {

    private final String id;
    private final String joinTextMessage, option1, option2;
    private final Cooldown time;

    private int oneVotes, twoVotes;
    private List<Player> voted = new ArrayList<>();

    private Cooldown announceTimer = new Cooldown(0L);

    public void announce() {
        Bukkit.getOnlinePlayers().forEach(this::announce);
    }

    public boolean voted(Player player) {
        return voted.contains(player);
    }

    public void finish() {
        Arrays.asList(
                " ",
                "       " + CC.B_PRIMARY + joinTextMessage,
                " ",
                CC.B_RED + "Final Votes:",
                " ",
                "    " + CC.B_GREEN + option1 + ": " + CC.B_YELLOW + oneVotes + " votes",
                "    " + CC.B_RED + option2 + ": " + CC.B_YELLOW + twoVotes + " votes",
                " ",
                CC.B_GREEN + ((oneVotes == twoVotes) ? "Draw!" : oneVotes > twoVotes ? option1 : option2) + " won!",
                " "
        ).forEach(s -> Common.broadcastMessage(PermLevel.DEFAULT, s));
    }

    public void vote(Player player, boolean option1) {
        if(voted.contains(player)) {
            player.sendMessage(CC.RED + "You already voted.");
            return;
        }

        voted.add(player);

        if (option1) this.oneVotes++;
        else this.twoVotes++;

        player.sendMessage(CC.GREEN + "Your vote has been counted!");
    }

    public void announce(Player player) {
        Arrays.asList(
                " ",
                "       " + CC.B_YELLOW + joinTextMessage,
                " "
        ).forEach(player::sendMessage);

        player.spigot().sendMessage(new ComponentBuilder("   " + CC.B_GREEN + option1 + ": " + CC.B_SECONDARY +
                (voted(player) ? oneVotes + " votes" : CC.B_YELLOW + "Click to vote!  "))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(voted(player) ? CC.RED + "You already voted." :
                        CC.GREEN + "This option has " + CC.B_YELLOW + oneVotes + " votes").create()))
                .event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/poll vote " + id + " option1")).create());

        player.spigot().sendMessage(new ComponentBuilder("   " + CC.B_RED + option2 + ": " + CC.B_SECONDARY +
                (voted(player) ? twoVotes + " votes" : CC.B_YELLOW + "Click to vote!"))
                .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(voted(player) ? CC.RED + "You already voted." :
                        CC.GREEN + "This option has " + CC.B_YELLOW + twoVotes + " votes").create())).event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/poll vote " + id + " option2")).create());

        player.sendMessage(" ");
        player.sendMessage(CC.B_PRIMARY + "Ending in " + CC.SECONDARY + time.getTimeLeft() + (time.getRemaining() > 60_000L ? "" : " seconds"));
        player.sendMessage(" ");
    }
}
