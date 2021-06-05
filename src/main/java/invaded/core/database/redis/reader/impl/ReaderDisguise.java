package invaded.core.database.redis.reader.impl;

import invaded.core.Spotify;
import invaded.core.database.redis.reader.Callback;
import invaded.core.manager.DisguiseHandler;
import invaded.core.profile.Profile;
import invaded.core.profile.ProfileHandler;
import invaded.core.rank.RankHandler;
import invaded.core.util.Skin;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;

import java.util.UUID;

public class ReaderDisguise implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String serverId = jsonObject.get("server-id").getAsString();
        String currentServer = Spotify.getInstance().getServerName();

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        RankHandler rankHandler = Spotify.getInstance().getRankHandler();

        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = profileHandler.getProfile(UUID.fromString(profileId));

        if(profile == null) profile = profileHandler.load(UUID.fromString(profileId), jsonObject.get("realName").getAsString());

        String fakeName = jsonObject.get("name").getAsString();
        String[] skin = jsonObject.get("skin").getAsString().split(";");
        Skin fakeSkin = new Skin(skin[0], skin[1]);
        String rank = jsonObject.get("rank").getAsString();

        profile.setFakeName(fakeName);
        profile.setFakeSkin(fakeSkin);
        profile.setFakeRank(rankHandler.getRank(rank));

        GameProfile gameProfile = new GameProfile(profile.getId(), fakeName);
        gameProfile.getProperties().put("textures", new Property("textures", fakeSkin.getTexture(), fakeSkin.getSignature()));

        profile.setFakeProfile(gameProfile);

        if(serverId.equals(currentServer)) {
            profile.disguise();
        }

        String info = profile.getFakeName()+";" + profile.getFakeRank().getName()
                +";" + profile.getFakeSkin().getTexture() +";" + profile.getFakeSkin().getSignature();

        DisguiseHandler.getDisguisedPlayers().put(profile.getId(), info);
        System.out.println("Successfully added " + profile.getName() + " to the map");
    }
}
