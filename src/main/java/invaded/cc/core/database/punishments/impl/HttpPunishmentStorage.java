package invaded.cc.core.database.punishments.impl;

import invaded.cc.common.library.gson.JsonArray;
import invaded.cc.common.library.gson.JsonElement;
import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.common.library.gson.JsonParser;
import invaded.cc.core.Spotify;
import invaded.cc.core.database.punishments.PunishmentStorage;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.punishment.Punishment;
import invaded.cc.core.util.Task;
import jodd.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class HttpPunishmentStorage implements PunishmentStorage {
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
        punishmentBody.put("address", punishment.getAddress());

        HttpResponse response = RequestHandler.post("/activePunishments", punishmentBody);
        response.close();

        if (punishment.getType() == Punishment.Type.MUTE || punishment.getType() == Punishment.Type.TEMPORARY_MUTE)
            return;

        ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
        profileHandler.getProfiles().remove(punishment.getCheaterUuid());
    }

    public void pardon(UUID uuid, Punishment punishment) {
        Task.async(() -> {
            ProfileHandler profileHandler = Spotify.getInstance().getProfileHandler();
            Optional<Profile> profileOptional = Optional.ofNullable(profileHandler.getProfile(uuid));
            Profile profile1 = profileOptional.orElse(profileHandler.load(uuid, punishment.getCheaterName(), false));
            profile1.setBan(null);
            move(profile1, punishment);
        });
    }

    public void load(Profile profile) {
        Map<String, Object> query = new HashMap<>();

        query.put("cheaterUuid", profile.getId().toString());

        HttpResponse response = RequestHandler.get("/activePunishments", query);

        if (response.statusCode() == 200) {
            JsonArray jsonArray = new JsonParser().parse(response.bodyText()).getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                long expire = jsonObject.get("expire").getAsLong();

                if (expire < System.currentTimeMillis() && jsonObject.get("type").getAsString().contains("TEMPORARY")) {
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

    public void move(Profile profile, Punishment punishment) {
        move(profile.getId(), profile.getName(), punishment);
    }

    public void move(UUID id, String name, Punishment punishment) {
        Map<String, Object> punishmentBody = new HashMap<>();

        punishmentBody.put("cheaterName", name == null ? "null" : name);
        punishmentBody.put("cheaterUuid", id.toString());
        punishmentBody.put("staffName", punishment.getStaffName());
        punishmentBody.put("reason", punishment.getReason());
        punishmentBody.put("silent", punishment.isSilent());
        punishmentBody.put("type", punishment.getType().name());
        punishmentBody.put("punishedAt", punishment.getPunishedAt());
        punishmentBody.put("removedBy", punishment.getRemovedBy());
        punishmentBody.put("removedAt", punishment.getRemovedAt());
        punishmentBody.put("address", punishment.getAddress());

        HttpResponse post = RequestHandler.post("/punishments", punishmentBody);
        post.close();

        System.out.println("Moved a punishment from activePunishments to punishments (holder " + name + ")");

        Map<String, Object> deleteQuery = new HashMap<>();

        deleteQuery.put("cheaterUuid", id.toString());
        // deleteQuery.put("cheaterName", name);
        deleteQuery.put("type", punishment.getType().name());
        deleteQuery.put("expire", punishment.getExpire());
        deleteQuery.put("staffName", punishment.getStaffName());
        deleteQuery.put("punishedAt", punishment.getPunishedAt());

        HttpResponse delete = RequestHandler.delete("/activePunishments", deleteQuery);
        delete.close();

        System.out.println(delete.statusCode() + " code from deleting activePUnishment");
    }
}
