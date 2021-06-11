package invaded.cc.core.network.server;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Server {

    private String name;
    private boolean recentlyCreated, testing, maintenance;
    private int online;
    private long lastUpdate;
    private String extraInfo = "";

    public Server(String name){
        this.name = name;
    }

}
