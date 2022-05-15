package invaded.cc.core.tablist;

import invaded.cc.core.Spotify;
import invaded.cc.core.tablist.entry.TabElementHandler;

public class TablistManager {

    private TabElementHandler adapter;
    private TabHandler handler;
    private long ticks = 50L;

    public void setAdapter(TabElementHandler adapter) {
        this.adapter = adapter;

        if(handler == null) {
            handler = new TabHandler(new TabAdapter_v1_8_R3(), adapter, Spotify.getInstance(), ticks);
        } else {
            handler.setHandler(adapter);
        }
    }

}
