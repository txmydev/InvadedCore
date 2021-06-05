package invaded.cc.core.punishment;

import invaded.cc.core.Spotify;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.Task;
import jodd.http.HttpResponse;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonElement;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter
public class PunishmentHandler {

    public void punish(UUID id, String name, Punishment punishment) {
        Map<String, Object> punishmentBody = new HashMap<>();

        punishmentBody.put("cheaterName", name);
        punishmentBody.put("cheaterUuid", id.toString());
        punishmentBody.put("staffName", punishment.getStaffName());
        punishmentBody.put("reason", punishment.getReason());
        punishmentBody.put("silent", punishment.isSilent());
        punishmentBody.put("type", punishment.getType().name());
        punishmentBody.put("punishedAt", punishment.getPunishedAt());
        punishmentBody.put("expire", punishment.getExpire());

        HttpResponse response = RequestHandler.post("/activePunishments", punishmentBody);
        response.close();

        if(punishment.getType() == Punishment.Type.MUTE || punishment.getType() ==  Punishment.Type.TEMPORARY_MUTE)return;

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        profileHandler.getProfiles().remove(punishment.getCheaterUuid());
    }

    public void pardon(UUID uuid, Punishment punishment) {
        Task.async(() -> {
            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
            Optional<Profile> profileOptional= Optional.ofNullable(profileHandler.getProfile(uuid));
            Profile profile1 = profileOptional.orElse(profileHandler.load(uuid, punishment.getCheaterName(), false));
            profile1.setBan(null);
            move(profile1, punishment);
        });
    }

    public void load(Profile profile) {
        Map<String, Object> query = new HashMap<>();

        query.put("cheaterUuid", profile.getId().toString());
        query.put("cheaterName", profile.getName());

        HttpResponse response = RequestHandler.get("/activePunishments", query);

        if(response.statusCode() == 200) {
            System.out.println();
            JsonArray jsonArray = new JsonParser().parse(response.bodyText()).getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                long expire = jsonObject.get("expire").getAsLong();

                if(expire < System.currentTimeMillis() && jsonObject.get("type").getAsString().contains("TEMPORARY")) {
                    move(profile, Spotify.GSON.fromJson(jsonObject.toString(), Punishment.class));
                    continue;
                }

                switch (jsonObject.get("type").getAsString()) {
                    case "BAN":
                    case "TEMPORARY_BAN":
                    case "BLACKLIST":
                        profile.setBan(Spotify.GSON.fromJson(jsonObject.toString(), Punishment.class));
                        break;
                    default:
                        profile.setMute(Spotify.GSON.fromJson(jsonObject.toString(), Punishment.class));
                        break;
                }
            }
        }

        response.close();
    }

    private void move(Profile profile, Punishment punishment) {
        move(profile.getId(), profile.getName(), punishment);
    }

    private void move(UUID id, String name, Punishment punishment) {
        Map<String, Object> punishmentBody = new HashMap<>();

        punishmentBody.put("cheaterName", name);
        punishmentBody.put("cheaterUuid", id.toString());
        punishmentBody.put("staffName", punishment.getStaffName());
        punishmentBody.put("reason", punishment.getReason());
        punishmentBody.put("silent", punishment.isSilent());
        punishmentBody.put("type", punishment.getType().name());
        punishmentBody.put("punishedAt", punishment.getPunishedAt());
        punishmentBody.put("removedBy", punishment.getRemovedBy());
        punishmentBody.put("removedAt", punishment.getRemovedAt());

        HttpResponse post = RequestHandler.post("/punishments", punishmentBody);
        post.close();

        System.out.println("Moved a punishment from activePunishments to punishments (holder " + name+ ")");

        Map<String, Object> deleteQuery = new HashMap<>();

        deleteQuery.put("cheaterUuid", id.toString());
        deleteQuery.put("cheaterName", name);
        deleteQuery.put("type", punishment.getType().name());
        deleteQuery.put("expire", punishment.getExpire());
        deleteQuery.put("staffName", punishment.getStaffName());
        deleteQuery.put("punishedAt", punishment.getPunishedAt());

        HttpResponse delete = RequestHandler.delete("/activePunishments", deleteQuery);
        delete.close();

        System.out.println(delete.statusCode() + " code from deleting activePUnishment");
    }
}
