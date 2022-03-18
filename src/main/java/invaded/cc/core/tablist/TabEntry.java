package invaded.cc.core.tablist;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.minecraft.util.com.mojang.authlib.GameProfile;

@AllArgsConstructor
public class TabEntry {

    public GameProfile profile;
    public int index, ping;
}
