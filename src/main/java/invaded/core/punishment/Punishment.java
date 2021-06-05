package invaded.core.punishment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor @Getter @Setter
public class Punishment {

    private Type type;

    private long punishedAt;
    private long expire;
    private long removedAt;

    private String cheaterName;
    private UUID cheaterUuid;

    private String staffName;

    private String removedBy;
    private boolean silent;

    private String reason;

    public Punishment(Type type, long punishedAt, long expire,String cheaterName, UUID cheaterUuid, String staffname, boolean silent, String reason) {
        this.type = type;
        this.punishedAt = punishedAt;
        this.expire = expire;
        this.cheaterName =cheaterName;
        this.cheaterUuid = cheaterUuid;
        this.staffName = staffname;
        this.silent = silent;
        this.reason = reason;
        this.removedBy = "";
        this.removedAt = 0L;
    }

    public boolean isBan() {
        return type == Type.BAN || type == Type.BLACKLIST
                || type == Type.TEMPORARY_BAN;
    }

    public enum Type {
        BAN("Ban"),
        BLACKLIST ("Blacklist"),
        WARN ( "Warning"),
        MUTE ("Mute"),
        TEMPORARY_BAN("Temp Ban"),
        TEMPORARY_MUTE("Temp Mute");

        String nice;

        Type(String nice){
            this.nice = nice;
        }

        public String getNice() {
            return nice;
        }
    }

}
