package invaded.cc.core.nametag.impl;

import invaded.cc.core.Spotify;
import invaded.cc.core.nametag.Nametag;
import invaded.cc.core.nametag.NametagProvider;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.rank.Rank;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class DefaultNametagProvider implements NametagProvider {

    private final Spotify plugin;

    private final List<String> groups = new ArrayList<>();

    public DefaultNametagProvider(Spotify plugin) {
        this.plugin = plugin;

        groups.addAll(plugin.getRankHandler().getRanks().stream().sorted((one, two) -> two.getPriority() - one.getPriority()).map(Rank::getName).collect(Collectors.toList()));
    }

    @Override
    public Nametag getNametag(Player player, Player target) {
        Profile profile = plugin.getProfileHandler().getProfile(target.getUniqueId());
        String rank;
        if (profile == null || (rank = profile.isDisguised() ? profile.getFakeRank().getName() : profile.getHighestRank().getName()) == null) return new Nametag(groups.size() + "-unknown", plugin.getRankHandler().getDefault().getColors());
        String color = profile.getColors();

        return new Nametag(groups.indexOf(rank) + '-' + rank, color);
    }
}