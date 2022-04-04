package invaded.cc.core.punishment;

import invaded.cc.core.Spotify;
import invaded.cc.core.database.punishments.PunishmentStorage;
import invaded.cc.core.manager.RequestHandler;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.profile.ProfileHandler;
import invaded.cc.core.util.Task;
import jodd.http.HttpResponse;
import lombok.Getter;
import invaded.cc.common.library.gson.JsonArray;
import invaded.cc.common.library.gson.JsonElement;
import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.common.library.gson.JsonParser;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Getter @RequiredArgsConstructor
public class PunishmentHandler {

    private final PunishmentStorage storage;

    public void punish(UUID id, String name, Punishment punishment) {
        storage.punish(id, name, punishment);
    }

    public void pardon(UUID uuid, Punishment punishment) {
        storage.pardon(uuid, punishment);
    }

    public void load(Profile profile) {
        storage.load(profile);
    }

    private void move(Profile profile, Punishment punishment) {
        storage.move(profile, punishment);
    }

    private void move(UUID id, String name, Punishment punishment) {
        storage.move(id, name, punishment);
    }
}
