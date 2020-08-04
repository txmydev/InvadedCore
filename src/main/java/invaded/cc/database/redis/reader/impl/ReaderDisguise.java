package invaded.cc.database.redis.reader.impl;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import invaded.cc.Core;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;
import invaded.cc.profile.ProfileHandler;
import invaded.cc.rank.Rank;
import invaded.cc.rank.RankHandler;
import invaded.cc.util.Skin;

import java.util.UUID;

public class ReaderDisguise implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        String serverId = jsonObject.get("server-id").getAsString();
        String currentServer = Core.getInstance().getServerName();

        ProfileHandler profileHandler = Core.getInstance().getProfileHandler();
        RankHandler rankHandler = Core.getInstance().getRankHandler();

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
    }
}
