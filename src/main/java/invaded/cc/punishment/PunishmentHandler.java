package invaded.cc.punishment;

import invaded.cc.Core;
import invaded.cc.manager.RequestHandler;
import invaded.cc.profile.Profile;
import jodd.http.HttpResponse;
import lombok.Getter;
import net.minecraft.util.com.google.gson.JsonArray;
import net.minecraft.util.com.google.gson.JsonElement;
import net.minecraft.util.com.google.gson.JsonObject;
import net.minecraft.util.com.google.gson.JsonParser;
import org.bukkit.Bukkit;

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
    }

    public void pardon(UUID uuid, Punishment punishment) {
        Map<String, Object> query = new HashMap<>();

        query.put("type", punishment.getType().name());
        query.put("cheaterUuid", uuid.toString());
        query.put("punishedAt", punishment.getPunishedAt());

        HttpResponse response = RequestHandler.delete("/activePunishments", query);
        response.close();

        if(response.statusCode() != 200) {
            Bukkit.getLogger().info("Request Handler - Failed to pardon " + punishment.getCheaterName() + " with response: " + response.bodyText());
            return;
        }

        Optional<Profile> op = Optional.of(Core.getInstance().getProfileHandler().getProfile(uuid));
        if(!punishment.isBan() && op.isPresent()) op.get().setMute(null);
    }

    public void load(Profile profile) {
        Map<String, Object> query = new HashMap<>();

        query.put("cheaterUuid", profile.getId().toString());
        query.put("cheaterName", profile.getName());

        HttpResponse response = RequestHandler.get("/activePunishments", query);

        if(response.statusCode() == 200) {
            JsonArray jsonArray = new JsonParser().parse(response.bodyText()).getAsJsonArray();

            for (JsonElement jsonElement : jsonArray) {
                JsonObject jsonObject = jsonElement.getAsJsonObject();

                long expire = jsonObject.get("expire").getAsLong();

                if(expire < System.currentTimeMillis() && jsonObject.get("type").getAsString().contains("TEMPORARY")) {
                    move(profile, Core.GSON.fromJson(jsonObject.toString(), Punishment.class));
                    continue;
                }

                System.out.println("Active punishment on user" + profile.getName());

                switch (jsonObject.get("type").getAsString()) {
                    case "BAN":
                    case "TEMPORARY_BAN":
                    case "BLACKLIST":
                        profile.setBan(Core.GSON.fromJson(jsonObject.toString(), Punishment.class));
                        break;
                    default:
                        profile.setMute(Core.GSON.fromJson(jsonObject.toString(), Punishment.class));
                        break;
                }
            }
        }

        response.close();
    }

    private void move(Profile profile, Punishment punishment) {
        Map<String, Object> punishmentBody = new HashMap<>();

        punishmentBody.put("cheaterName", profile.getName());
        punishmentBody.put("cheaterUuid", profile.getId().toString());
        punishmentBody.put("staffName", punishment.getStaffName());
        punishmentBody.put("reason", punishment.getReason());
        punishmentBody.put("silent", punishment.isSilent());
        punishmentBody.put("type", punishment.getType().name());
        punishmentBody.put("punishedAt", punishment.getPunishedAt());
        punishmentBody.put("removedBy", punishment.getRemovedBy());
        punishmentBody.put("removedAt", punishment.getRemovedAt());

        HttpResponse post = RequestHandler.post("/punishments", punishmentBody);
        post.close();

        System.out.println("Moved a punishment from activePunishments to punishments (holder " + profile.getName()+ ")");

        Map<String, Object> deleteQuery = new HashMap<>();

        deleteQuery.put("cheaterUuid", profile.getId().toString());
        deleteQuery.put("cheaterName", profile.getName());
        deleteQuery.put("type", punishment.getType().name());
        deleteQuery.put("expire", punishment.getExpire());
        deleteQuery.put("staffName", punishment.getStaffName());
        deleteQuery.put("punishedAt", punishment.getPunishedAt());

        HttpResponse delete = RequestHandler.delete("/activePunishments", deleteQuery);
        delete.close();
    }
}
