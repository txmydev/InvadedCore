package invaded.cc.core.database.punishments;

import invaded.cc.core.profile.Profile;
import invaded.cc.core.punishment.Punishment;

import java.util.UUID;

public interface PunishmentStorage {

    void punish(UUID id, String name, Punishment punishment);
    void pardon(UUID uuid, Punishment punishment);
    void load(Profile profile);

    void move(UUID id, String name, Punishment punishment);
    void move(Profile profile, Punishment punishment);



}
