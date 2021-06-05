package invaded.core.database.redis;

import lombok.Getter;

@Getter
public class JedisConfiguration {

    private String host;
    private int port;
    private String username, password;
    private boolean auth;

    public JedisConfiguration(String host,
                              int port,
                              String username,
                              String password,
                              boolean auth){
        this.host = host;
        this.port =port;
        this.username = username;
        this.password = password;
        this.auth = auth;
    }

    public JedisConfiguration(String host,
                              int port){
        this(host, port, null, null, false);
    }

}
