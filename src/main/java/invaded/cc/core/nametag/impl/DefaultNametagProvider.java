package invaded.cc.core.nametag.impl;

import invaded.cc.core.Spotify;
import invaded.cc.core.nametag.Nametag;
import invaded.cc.core.nametag.NametagProvider;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.Common;
import org.bukkit.entity.Player;

public final class DefaultNametagProvider implements NametagProvider {

    private final Spotify plugin;

    public DefaultNametagProvider(Spotify plugin) {
        this.plugin = plugin;
    }

    @Override
    public Nametag getNametag(Player player, Player target) {
        Profile profile = plugin.getProfileHandler().getProfile(target.getUniqueId());
        if (profile == null) return new Nametag("def-colors", plugin.getRankHandler().getDefault().getColors());
        String s = "nt_" + target.getName();
        if (s.length() > 16) s = s.substring(0, 16);
        return new Nametag(s, profile.getColors());
    }
}