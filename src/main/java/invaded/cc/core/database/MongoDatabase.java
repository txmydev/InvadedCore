package invaded.cc.core.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import lombok.Getter;

public class MongoDatabase {

    @Getter
    private MongoClient client;
    @Getter
    private com.mongodb.client.MongoDatabase database;

    public MongoDatabase(MongoSettings settings) {
        this.connect(settings);
    }

    private void connect(MongoSettings settings) {
        ServerAddress address = new ServerAddress(settings.getAddress(), settings.getPort());

        if (settings.isAuth()) {
            client = new MongoClient(address, MongoCredential.createCredential(settings.getUsername(), settings.getLoginDatabase(), settings.getPassword().toCharArray()), MongoClientOptions.builder().build());
        } else {
            client = new MongoClient(address);
        }

        this.database = client.getDatabase(settings.getDatabase());

    }

}
