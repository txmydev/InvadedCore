package invaded.cc.core.database;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor @Data
public class MongoSettings {

    private final String address;
    private final int port;
    private final boolean auth;
    private final String username, database, password, loginDatabase;

}
