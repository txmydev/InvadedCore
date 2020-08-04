package invaded.cc.database.redis.handlers;

import com.google.gson.JsonObject;
import invaded.cc.Core;
import invaded.cc.profile.User;
import invaded.cc.server.Server;
import invaded.cc.database.redis.JedisHandler;
import invaded.cc.manager.ServerHandler;

import java.util.UUID;

public class DataSubscriptionHandler implements JedisHandler {
    @Override
    public void handle(String channel, JsonObject jsonObject) {
        if (!channel.equalsIgnoreCase("player-channel")) return;

        ServerHandler serverHandler = Core.getInstance().getServerHandler();
        if (serverHandler == null) return;

        User globalPlayer = serverHandler.find(jsonObject.get("name").getAsString());

        if (globalPlayer == null) {
            globalPlayer = new User();
            globalPlayer.setName(jsonObject.get("name").getAsString());

            for (Server server : serverHandler.getServers().values()) {
                if (!server.containsPlayer(jsonObject.get("name").getAsString())) {
                    server.getPlayers().add(globalPlayer);
                }
            }
        }

        globalPlayer.setUuid(UUID.fromString(jsonObject.get("uuid").getAsString()));
        globalPlayer.setLastUpdate(jsonObject.get("lastUpdate").getAsLong());
        globalPlayer.setRank(jsonObject.get("rank").getAsString());
        globalPlayer.setLastServer(jsonObject.get("lastServer").getAsString());
        globalPlayer.setSwitchingServer(jsonObject.get("switchingServer").getAsBoolean());
        globalPlayer.setDisguised(jsonObject.get("disguised").getAsBoolean());
        globalPlayer.setDisguiseInfo(jsonObject.get("disguiseData").getAsString());

/*
        Profile profile = Profile.getByUuid(globalPlayer.getUuid());

        if (profile != null) {
            if (globalPlayer.isDisguised() != profile.isDisguised()) {
                if(!globalPlayer.isDisguised()) {
                    profile.setFakeName(null);
                    profile.setFakeRank(null);
                    profile.setFakeProfile(null);
                    profile.setFakeSkin(null);
                    System.out.println("[Disguise] Player " + globalPlayer.getName() + " was undisguised (defined by User) globally but not in server " + Core.getInstance().getServerName() + ", so we undisguised him!");
                }else{
                    String[] data = globalPlayer.getDisguiseInfo().split(";");
                    String[] skinData = data[1].split(",");
                    String t = skinData[0];
                    String s = skinData[1];
                    String fakeName = data[0];
                    String rank = data[2];

                    GameProfile gameProfile = new GameProfile(profile.getId(), fakeName);
                    gameProfile.getProperties().put("textures", new Property("textures", t, s));

                    Skin skin = new Skin(t, s);

                    profile.setFakeProfile(gameProfile);
                    profile.setFakeName(fakeName);
                    profile.setFakeRank(Rank.getRank(rank));
                    profile.setFakeSkin(skin);

                    System.out.println("[Disguise] Player " + globalPlayer.getName() + " was disguised (defined by User) globally but not in server " + Core.getInstance().getServerName() + ", so we disguised him!");
                }
            }
        }*/

        for (Server server : serverHandler.getServers().values())
            server.getPlayers().forEach(player -> {
                if(System.currentTimeMillis() - player.getLastUpdate() >= 7000L) {
                    server.getPlayers().remove(player);
                }
            });
    }
}
