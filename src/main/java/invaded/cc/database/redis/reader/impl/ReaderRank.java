package invaded.cc.database.redis.reader.impl;

import invaded.cc.database.redis.reader.Callback;
import net.minecraft.util.com.google.gson.JsonObject;

public class ReaderRank implements Callback<JsonObject> {

    @Override
    public void callback(JsonObject jsonObject) {
    /*    String serverId = jsonObject.get("server-id").getAsString();
        String currentServer = Core.getInstance().getServerName();

        String profileId = jsonObject.get("profileId").getAsString();
        Profile profile = Profile.getByUuid(UUID.fromString(profileId));

        String rank = jsonObject.get("rank").getAsString();
        String granter = jsonObject.get("granter").getAsString();

        GrantOld grant = new GrantOld(profile, rank, granter);

        if(serverId.equals(currentServer)) {
            profile.getGrant().removeAttachment(Bukkit.getPlayer(profile.getId()));
            grant.setupAttachment(Bukkit.getPlayer(profile.getId()));
        }

        profile.setGrant(grant);*/
    }

}
