package invaded.cc.trails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor @Getter
public enum Trail {



    FIREWORKS("fireworksSpark", "Fireworks Trail", 100, Material.FIREWORK),
    NOTE("note", "Music Note", 150, Material.NOTE_BLOCK),
    FLAME("flame", "Flame Trail", 200, Material.FIRE),
    LAVA_POP("lava", "Lava Trail", 250, Material.LAVA_BUCKET),
    SMOKE("smoke", "Smoke Trail", 300, Material.COAL),
    CLOUD("cloud", "Cloud Trail", 500, Material.FEATHER),
    SLIME("slime", "Slime Trail", 600, Material.SLIME_BALL),
    HEARTS("heart", "Hearts Trail", 1000, Material.MELON)
    ;

    @Getter @Setter
    private static final Map<Entity, Trail> toDisplay = new HashMap<>();


    private final String id, display;
    @Setter
    private int price;
    private Material material;

    public static Trail getById(String activeTrail) {
        for(Trail t : values())
            if(t.getId().equalsIgnoreCase(activeTrail)) return t;


        return null;
    }
}
