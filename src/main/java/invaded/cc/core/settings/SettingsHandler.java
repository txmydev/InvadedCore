package invaded.cc.core.settings;

import invaded.cc.core.settings.impl.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SettingsHandler {

    @Getter
    private final List<SettingsOption> settings = new ArrayList<>();

    public SettingsHandler() {
        add(new BossbarSetting());
        add(new FlySetting());
        add(new LunarBorderSetting());
        add(new LunarPrefixOption());
        add(new MessageSoundSetting());
        add(new PrivateMessageSetting());
        add(new ScoreboardTypeOption());
    }

    public void add(SettingsOption option) {
        settings.add(option);
    }

}
