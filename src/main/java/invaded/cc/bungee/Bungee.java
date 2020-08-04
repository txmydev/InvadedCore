package invaded.cc.bungee;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Bungee extends Plugin implements Listener {

    public static Bungee INSTANCE;

    @Override
    public void onEnable() {
        INSTANCE = this;

        getProxy().registerChannel("invaded-channel");
        getProxy().getPluginManager().registerListener(this, this);

        new Announcer();
    }

    @Override
    public void onDisable() {
    }

    @EventHandler
    public void onServerList(net.md_5.bungee.api.event.ProxyPingEvent event) {
        ServerPing response = event.getResponse();

        String str = "&bInvaded Network - &cIn Beta\n" +
                "&bCurrently with UHC & Meetup.";

        response.setDescription(ChatColor.translateAlternateColorCodes('&', str));

        int online = response.getPlayers().getOnline();
        response.setPlayers(new ServerPing.Players(300, online, new ServerPing.PlayerInfo[0]));
    }


    private class Announcer implements Runnable {

        public Announcer() {
            getProxy().getScheduler().schedule(Bungee.INSTANCE, this, 1, 3, TimeUnit.MINUTES);
        }

        private final List<String> messages = Arrays.asList("&bFollow us on twitter to don't miss any game &f@InvadedNetwork"
                , "&bYou can visit our store at &fstore.invaded.cc",
                "&bYour gap didn't chugged? Be more patient or leave.",
                "&bWe're currently in &cbeta&b,if you find any bugs please report them at &f/helpop&b."
                , "&bThink before blaming the host, he isn't guilt of your dead!"
        , "&bYou aren't finding diamonds? Look into the smallest spaces in the cave."
        , "&bYou died cause a bug and don't have proofs? We &cwon't &brespawn you."
        ,"&BYou can change your IGN color by doing &f/color&b!");

        private int index = 0;

        @Override
        public void run() {
            if (index >= messages.size()) index = 0;

            String message = messages.get(index);
            getProxy().broadcast(new TextComponent(" "));
            getProxy().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes('&', message)));
            getProxy().broadcast(new TextComponent(" "));
            index++;
        }
    }
}
