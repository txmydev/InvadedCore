package invaded.cc.core.server;

import invaded.cc.core.profile.User;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class Server {

    private String serverId;
    private int maxPlayers;
    private int onlinePlayers;
    private boolean whitelist;
    private boolean maintenance;
    private boolean joineable;
    private List<User> players;
    private long lastUpdate;
    private String motd;

    public Server(String serverId) {
        this.serverId = serverId;
        this.players = new ArrayList<>();
    }

    public boolean containsPlayer(String name) {
        for (User player : players) {
            if (player.getName().equalsIgnoreCase(name)) return true;
        }
        return false;
    }
}
