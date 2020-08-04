package invaded.cc.profile;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class User {

    private UUID uuid;

    private String name;
    private String rank;

    private long lastUpdate = 0L;
    private boolean switchingServer = false;

    private String lastServer;

    private boolean disguised;
    private String disguiseInfo;

}
