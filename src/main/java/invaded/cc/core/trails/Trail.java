package invaded.cc.core.trails;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Getter
public enum Trail {

    FIREWORKS("fireworksSpark", "Fireworks Trail", 70, Material.FIREWORK),
    NOTE("note", "Music Note", 70, Material.NOTE_BLOCK),
    FLAME("flame", "Flame Trail", 70, Material.FIRE),
    LAVA_POP("lava", "Lava Trail", 70, Material.LAVA_BUCKET),
    SMOKE("smoke", "Smoke Trail", 70, Material.COAL),
    CLOUD("cloud", "Cloud Trail", 70, Material.FEATHER),
    SLIME("slime", "Slime Trail", 70, Material.SLIME_BALL),
    HEARTS("heart", "Hearts Trail", 150, Material.REDSTONE),
    EXPLODE("explode", "Explode Trail", 70, Material.TNT),
    WATER("dripWater", "Water Trail", 70, Material.WATER_BUCKET),
    ENCHANTMENT_GLYPH("enchantmenttable", "Enchant Particles Trail", 70, Material.ENCHANTMENT_TABLE);

    @Getter
    @Setter
    private static final Map<Entity, Trail> toDisplay = new HashMap<>();

    private final String id, display;
    @Setter
    private int price;
    private Material material;

    public static Trail getById(String activeTrail) {
        for (Trail t : values())
            if (t.getId().equalsIgnoreCase(activeTrail)) return t;


        return null;
    }
}
