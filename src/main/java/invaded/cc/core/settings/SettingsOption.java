package invaded.cc.core.settings;

import invaded.cc.core.profile.Profile;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;
import java.util.function.Function;

@Data
public class SettingsOption {



    private final String name;
    private final Function<Profile, ItemStack> stack;
    private final Consumer<Profile> click;

    public SettingsOption(String name, Function<Profile, ItemStack> stack, Consumer<Profile> click) {
        this.name = name;
        this.stack = stack;
        this.click = click;

    }

}
