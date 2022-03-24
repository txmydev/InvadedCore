package invaded.cc.core.network.packet;

import invaded.cc.common.library.gson.JsonObject;
import invaded.cc.core.Spotify;
import invaded.cc.core.network.PacketListener;
import invaded.cc.core.network.SpotifyPacket;
import invaded.cc.core.profile.Profile;
import invaded.cc.core.util.CC;
import invaded.cc.core.util.Common;
import invaded.cc.core.util.json.JsonChain;
import invaded.cc.core.util.perms.PermLevel;
import invaded.cc.core.util.perms.Permission;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class PacketReportPlayer extends SpotifyPacket {
    private Profile profile;
    private String message, reported;
    private String server;

    public PacketReportPlayer(Profile profile, String reported, String message, String server) {
        super("packet-report-player");

        this.profile = profile;
        this.reported = reported;
        this.message = message;
        this.server = server;
    }

    @Override
    public JsonObject toJson() {
        return new JsonChain()
                .addProperty("packet-id", getPacketId())
                .addProperty("id", profile.getId().toString())
                .addProperty("name", profile.getName())
                .addProperty("reported", reported)
                .addProperty("message", message)
                .addProperty("server", server)
                .get();
    }

    public static class Listener extends PacketListener {

        @Override
        public void onReceivePacket(JsonObject packet) {
            System.out.println("Received packet " + packet.toString());

            String server = packet.get("server").getAsString();
            String id = packet.get("id").getAsString();
            String name = packet.get("name").getAsString();
            String reported = packet.get("reported").getAsString();
            String message = packet.get("message").getAsString();

            ComponentBuilder builder = new ComponentBuilder(CC.BLUE + "[Report] " + CC.GRAY + "[" + server + "] " + CC.AQUA + name + CC.GRAY + " has reported " + CC.AQUA + reported);
            ComponentBuilder reasonBuilder = new ComponentBuilder("   " +CC.BLUE + "Reason: " + CC.GRAY + message);

            if(Spotify.SERVER_NAME.equalsIgnoreCase(server)) {
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + reported));
                builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.GREEN + "Click to teleport to " + name).create()));
            } else {
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + server));
                builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.BLUE + "Click to be sent to " + server + "\n  &4&lWarning: &cThis will switch your server!").create()));
            }

            Common.getOnlinePlayers().forEach(player ->{
                if(Permission.test(player, PermLevel.STAFF)) {
                    player.spigot().sendMessage(builder.create());
                    player.spigot().sendMessage(reasonBuilder.create());
                }
            });
            Bukkit.getConsoleSender().sendMessage(TextComponent.toPlainText(builder.create()));
        }
    }
}
