package invaded.cc.core.network.server;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class Server {

    private String name;
    private boolean onlineState, testing, maintenance;
    private int online;
    private long lastUpdate;

    public Server(String name){
        this.name = name;
    }

    public boolean isOnline(){
        return System.currentTimeMillis() - lastUpdate <= 5000L;
    }

}
