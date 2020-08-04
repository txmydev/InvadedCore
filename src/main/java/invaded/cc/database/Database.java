package invaded.cc.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import invaded.cc.Core;
import invaded.cc.database.redis.JedisManager;
import invaded.cc.util.ConfigFile;
import invaded.cc.util.ConfigTracker;
import lombok.Getter;
import org.bson.Document;

@Getter
public class Database {



    private final JedisManager redisManager;

    private final String host;
    private final String username;
    private final String password;
    private final String database;
    private final int port;
    private final boolean auth;

    private MongoDatabase mongoDatabase;
    private MongoClient client;

    public Database(){
        ConfigFile configFile = Core.getInstance().getDatabaseConfig();
        ConfigTracker configTracker = new ConfigTracker(configFile, "mongo");

        this.host = configTracker.getString("host");
        this.port = configTracker.getInt("port");
        this.username = configTracker.getString("username");
        this.password = configTracker.getString("password");
        this.database = configTracker.getString("database");
        this.auth = configTracker.getBoolean("authentication");

        this.redisManager = new JedisManager();
    }

    public boolean open(){
        try{
            MongoClientURI uri = new MongoClientURI("mongodb://" + (auth ? username + ":" + password + "@" + host  + ":" + port : host + ":" + port));
            client = new MongoClient(uri);

            this.mongoDatabase = client.getDatabase(database);

            if(!exists("core_playerData")) mongoDatabase.createCollection("core_playerData");
            if(!exists("core_punishments")) mongoDatabase.createCollection("core_punishments");
            if(!exists("core_activePunishments")) mongoDatabase.createCollection("core_activePunishments");
            if(!exists("core_ranks")) mongoDatabase.createCollection("core_ranks");

            return true;
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }

    public void close(){
        try { redisManager.globalClose(); Core.getInstance().getServerHandler().close(); } catch(Exception ex) { ex.printStackTrace(); }

        if(client == null) return;
        client.close();
    }

    public MongoCollection<Document> getCollection(String collection){
        return mongoDatabase.getCollection("core_" + collection);
    }

    private boolean exists(String collection) {
        for(String s : mongoDatabase.listCollectionNames()){
            if(collection.equalsIgnoreCase(s))
                return true;
        }

        return false;
    }

}
