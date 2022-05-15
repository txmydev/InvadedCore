package invaded.cc.core.commands.rank;

import com.google.common.base.Joiner;
import invaded.cc.core.Spotify;
import invaded.cc.core.rank.Rank;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.NumberUtils;
import invaded.cc.core.util.command.BasicCommand;
import invaded.cc.core.util.perms.PermLevel;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.yaml.snakeyaml.error.YAMLException;

import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class RankCommand extends BasicCommand {

    private final Spotify plugin;

    public RankCommand(Spotify plugin) {
        super("rank", PermLevel.ADMIN);

        this.plugin = plugin;
    }

    private void help(CommandSender sender) {
        sender.sendMessage(Common.getLine(40));
        sender.sendMessage(CC.YELLOW + "Rank Commands");
        sender.sendMessage(" ");
        sender.sendMessage(CC.YELLOW + "/rank create <name> <priority> " + CC.GRAY + "| " + CC.WHITE + "Create's a rank");
        sender.sendMessage(CC.YELLOW + "/rank setpriority <name> <priority> " + CC.GRAY + "| " + CC.WHITE + "Modifies a rank's priority.");
        sender.sendMessage(CC.YELLOW + "/rank setprefix <name> <prefix> " + CC.GRAY + "| " + CC.WHITE + "Modifies a rank's prefix.");
        sender.sendMessage(CC.YELLOW + "/rank setsuffix <name> <suffix> " + CC.GRAY + "| " + CC.WHITE + "Modifies a rank's suffix.");
        sender.sendMessage(CC.YELLOW + "/rank setcolor <name> <color> " + CC.GRAY + "| " + CC.WHITE + "Modifies a rank's color.");
        sender.sendMessage(CC.YELLOW + "/rank addperm <name> <permission> " + CC.GRAY + "| " + CC.WHITE + "Adds a permission to a certain rank.");
        sender.sendMessage(CC.YELLOW + "/rank removeperm <name> <permission> " + CC.GRAY + "| " + CC.WHITE + "Removes a permission to a certain rank.");
        sender.sendMessage(CC.YELLOW + "/rank setitalic <name> " + CC.GRAY + "| " + CC.WHITE + "Toggles a certain rank italic mode.");
        sender.sendMessage(CC.YELLOW + "/rank setbold <name> " + CC.GRAY + "| " + CC.WHITE + "Toggles a certain rank bold mode.");
        sender.sendMessage(CC.YELLOW + "/rank setdefaultrank <name> " + CC.GRAY + "| " + CC.WHITE + "Toggles a certain rank default rank mode.");
        sender.sendMessage(CC.YELLOW + "/rank setitalic <name> <true:false> " + CC.GRAY + "| " + CC.WHITE + "Modifies a rank's italic mode.");
        sender.sendMessage(CC.YELLOW + "/rank setbold <name> <true:false> " + CC.GRAY + "| " + CC.WHITE + "Modifies a rank's bold mode.");
        sender.sendMessage(CC.YELLOW + "/rank setdefaultrank <name> <true:false> " + CC.GRAY + "| " + CC.WHITE + "Modifies a rank's default rank mode.");
        sender.sendMessage(CC.YELLOW + "/rank save " + CC.GRAY + "| " + CC.WHITE + "Save's all ranks");
        sender.sendMessage(Common.getLine(40));
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length <= 1) {
            help(sender);
            return;
        }

        switch (args[0]) {
            case "create":
                // /rank create <rank> <
                if (args.length < 3) {
                    help(sender);
                    return;
                }

                String newName = args[1];
                int newWeight = NumberUtils.getInt(args[2], () -> sender.sendMessage(CC.RED + "Please provide a valid number."));

                Rank newRank = new Rank(newName);
                newRank.setPriority(newWeight);

                plugin.getRankHandler().getRanks().add(newRank);
                sender.sendMessage(CC.YELLOW + "You've created rank " + newRank.getColoredName() + CC.YELLOW + " with weight " + newRank.getColor() + newWeight + CC.YELLOW + ".");
                break;
            case "setweight":
            case "setpriority":
                // /rank setpriority <rank> <weight>
                if (args.length < 3) {
                    help(sender);
                    return;
                }

                execute(args, 1, rank -> {
                    int weight = NumberUtils.getInt(args[2], () -> sender.sendMessage(CC.RED + "Please provide a valid number."));
                    rank.setPriority(weight);

                    sender.sendMessage(CC.YELLOW +
                            "You've changed " + rank.getColoredName() + CC.YELLOW
                            + "'s weight to " + rank.getColor() + weight + CC.YELLOW + ".");
                }, () -> sender);
                break;
            case "setprefix":
                // /rank setpriority <rank> <weight>
                if (args.length < 3) {
                    help(sender);
                    return;
                }

                execute(args, 1, rank -> {
                    String prefix = CC.toColor(args[2]);
                    if (prefix.equalsIgnoreCase("none")) prefix = "";

                    rank.setPrefix(prefix);

                    if (prefix.isEmpty()) {
                        sender.sendMessage(CC.YELLOW + "You've removed " + rank.getColoredName() + "'s " + CC.YELLOW + " prefix.");
                        return;
                    }

                    sender.sendMessage(CC.YELLOW +
                            "You've changed " + rank.getColoredName() + CC.YELLOW
                            + "'s prefix to " + rank.getColor() + prefix + CC.YELLOW + ".");
                }, () -> sender);
                break;
            case "setsuffix":
                // /rank setpriority <rank> <weight>
                if (args.length < 3) {
                    help(sender);
                    return;
                }

                execute(args, 1, rank -> {
                    String suffix = CC.toColor(args[2]);
                    if (suffix.equalsIgnoreCase("none")) suffix = "";

                    rank.setSuffix(suffix);

                    if (suffix.isEmpty()) {
                        sender.sendMessage(CC.YELLOW + "You've removed " + rank.getColoredName() + "'s " + CC.YELLOW + " suffix.");
                        return;
                    }

                    sender.sendMessage(CC.YELLOW +
                            "You've changed " + rank.getColoredName() + CC.YELLOW
                            + "'s suffix to " + rank.getColor() + suffix + CC.YELLOW + ".");
                }, () -> sender);
                break;
            case "addperm":
                if (args.length < 3) {
                    help(sender);
                    return;
                }

                execute(args, 1, rank -> {
                    String perm = args[2];
                    if (rank.getPermissions().contains(perm)) {
                        sender.sendMessage(rank.getColoredName() + CC.YELLOW + " already has that permission.");
                        return;
                    }

                    rank.getPermissions().add(perm);

                    sender.sendMessage(CC.YELLOW +
                            "You've added " + rank.getColor() + perm + CC.YELLOW + " permission to " + rank.getColoredName() + CC.YELLOW + ".");
                }, () -> sender);
                break;
            case "removeperm":
            case "delperm":
                if (args.length < 3) {
                    help(sender);
                    return;
                }

                execute(args, 1, rank -> {
                    String perm = args[2];
                    if (!rank.getPermissions().contains(perm)) {
                        sender.sendMessage(rank.getColoredName() + CC.YELLOW + " doesn't have that permission.");
                        return;
                    }

                    rank.getPermissions().remove(perm);

                    sender.sendMessage(CC.YELLOW +
                            "You've removed " + rank.getColor() + perm + CC.YELLOW + " permission from " + rank.getColoredName() + CC.YELLOW + ".");
                }, () -> sender);
                break;
            case "setcolor":
                // /rank setpriority <rank> <weight>
                if (args.length < 3) {
                    help(sender);
                    return;
                }

                execute(args, 1, rank -> {
                    String color = args[2];

                    try {
                        ChatColor chatColor = ChatColor.valueOf(color);
                        rank.setColor(chatColor);

                        sender.sendMessage(CC.YELLOW +
                                "You've changed " + rank.getColoredName() + CC.YELLOW
                                + "'s color to " + rank.getColor() + chatColor.name() + CC.YELLOW + ".");
                    } catch (EnumConstantNotPresentException ex) {
                        sender.sendMessage(CC.RED + color + " is not a valid color, use the following: " + Joiner.on(",").join(Arrays.stream(ChatColor.values()).map(c -> c.name()).collect(Collectors.toList())));
                    }
                }, () -> sender);
                break;
            case "setitalic":

                // /rank setitalic <rank>

                execute(args, 1, rank -> {
                    boolean value;

                    if(args.length == 2) value = !rank.isItalic();
                    else value = Arrays.asList("true", "enabled", "yes", "t").contains(args[2].toLowerCase());

                    rank.setItalic(value);

                    sender.sendMessage(CC.YELLOW +
                            "You've changed " + rank.getColoredName() + CC.YELLOW
                            + "'s italic mode to " + rank.getColor() + value + CC.YELLOW + ".");
                }, () -> sender);

                break;

            case "setbold":
                execute(args, 1, rank -> {
                    boolean value;

                    if(args.length == 2) value = !rank.isBold();
                    else value = Arrays.asList("true", "enabled", "yes", "t").contains(args[2].toLowerCase());

                    rank.setBold(value);

                    sender.sendMessage(CC.YELLOW +
                            "You've changed " + rank.getColoredName() + CC.YELLOW
                            + "'s bold mode to " + rank.getColor() + value + CC.YELLOW + ".");
                }, () -> sender);

                break;
            case "setdefaultrank":
                execute(args, 1, rank -> {
                    boolean value;

                    if(args.length == 2) value = !rank.isBold();
                    else value = Arrays.asList("true", "enabled", "yes", "t").contains(args[2].toLowerCase());

                    rank.setDefaultRank(value);

                    sender.sendMessage(CC.YELLOW +
                            "You've changed " + rank.getColoredName() + CC.YELLOW
                            + "'s default rank mode to " + rank.getColor() + value + CC.YELLOW + ".");
                }, () -> sender);
                break;
            case "save":
                plugin.getRankHandler().saveAll();
                sender.sendMessage(CC.YELLOW + "You've saved all the ranks.");
                break;
        }
    }

    private void execute(String[] args, int targetArg, Consumer<Rank> foundConsumer, Supplier<CommandSender> failed) {
        String rankName = args[targetArg];
        Rank rank = plugin.getRankHandler().getRank(rankName);

        if (rank == null) {
            failed.get().sendMessage(CC.RED + "Couldn't find rank with name '" + rankName + "'.");
            return;
        }

        foundConsumer.accept(rank);
    }
}
