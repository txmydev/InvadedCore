package invaded.cc.database.redis.reader.impl;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import invaded.cc.Core;
import invaded.cc.database.redis.reader.Callback;
import invaded.cc.profile.Profile;
import invaded.cc.rank.Rank;
import invaded.cc.util.Skin;

import java.util.UUID;

public class ReaderDisguise implements Callback<JsonObject> {
    @Override
    public void callback(JsonObject jsonObject) {
        /*String serverId = jsonObject.get("server-id").getAsString();
        String currentServer = Core.getInstance().getServerName();

        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = Profile.getByUuid(UUID.fromString(profileId));

        String fakeName = jsonObject.get("name").getAsString();
        String[] skin = jsonObject.get("skin").getAsString().split(";");
        Skin fakeSkin = new Skin(skin[0], skin[1]);
        String rank = jsonObject.get("rank").getAsString();

        profile.setFakeName(fakeName);
        profile.setFakeSkin(fakeSkin);
        profile.setFakeRank(Rank.getRank(rank));

        GameProfile gameProfile = new GameProfile(profile.getId(), fakeName);
        gameProfile.getProperties().put("textures", new Property("textures", fakeSkin.getTexture(), fakeSkin.getSignature()));

        profile.setFakeProfile(gameProfile);

        if(serverId.equals(currentServer)) {
            profile.disguise();
        }*/
    }
}
