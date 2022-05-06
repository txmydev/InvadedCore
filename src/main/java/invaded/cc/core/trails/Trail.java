package invaded.cc.core.trails;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.Material;
import org.bukkit.entity.Entity;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum Trail {

    FIREWORKS(EnumParticle.FIREWORKS_SPARK, "Fireworks Trail", 70, Material.FIREWORK),
    NOTE(EnumParticle.NOTE, "Music Note", 70, Material.NOTE_BLOCK),
    FLAME(EnumParticle.FLAME, "Flame Trail", 70, Material.FIRE),
    LAVA_POP(EnumParticle.LAVA, "Lava Trail", 70, Material.LAVA_BUCKET),
    SMOKE(EnumParticle.SMOKE_NORMAL, "Smoke Trail", 70, Material.COAL),
    CLOUD(EnumParticle.CLOUD, "Cloud Trail", 70, Material.FEATHER),
    SLIME(EnumParticle.SLIME, "Slime Trail", 70, Material.SLIME_BALL),
    HEARTS(EnumParticle.HEART, "Hearts Trail", 150, Material.REDSTONE),
    EXPLODE(EnumParticle.EXPLOSION_NORMAL, "Explode Trail", 70, Material.TNT),
    WATER(EnumParticle.DRIP_WATER, "Water Trail", 70, Material.WATER_BUCKET),
    ENCHANTMENT_GLYPH(EnumParticle.ENCHANTMENT_TABLE, "Enchant Particles Trail", 70, Material.ENCHANTMENT_TABLE);

    @Getter
    @Setter
    private static final Map<Entity, Trail> toDisplay = new HashMap<>();

    private final EnumParticle particle;
    private final String display;
    @Setter
    private int price;
    private Material material;

    Trail(EnumParticle particle, String display, int price, Material material) {
        this.particle = particle;
        this.display = display;
        this.price = price;
        this.material = material;
    }

    public String getId() {
        return name().toLowerCase();
    }

    public static Trail getById(String activeTrail) {
        return Trail.valueOf(activeTrail);
    }
}
