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
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;

public class PacketRequestHelp extends SpotifyPacket {

    private Profile profile;
    private String message;
    private String server;

    public PacketRequestHelp(Profile profile, String message, String server) {
        super("packet-request-help");

        this.profile = profile;
        this.message = message;
        this.server = server;
    }

    @Override
    public JsonObject toJson() {
        return new JsonChain()
                .addProperty("packet-id", getPacketId())
                .addProperty("id", profile.getId().toString())
                .addProperty("name", profile.getName())
                .addProperty("message", message)
                .addProperty("server", server)
                .get();
    }

    @Getter
    public static class Listener extends PacketListener {

        public Listener(String packet) {
            super(packet);
        }

        @Override
        public void onReceivePacket(JsonObject packet) {
            System.out.println("recevied packet " + packet.toString());

            String server = packet.get("server").getAsString();
            String id = packet.get("id").getAsString();
            String name = packet.get("name").getAsString();
            String message = packet.get("message").getAsString();

            ComponentBuilder builder = new ComponentBuilder(CC.BLUE + "[Helpop] " + CC.GRAY + "[" + server + "] " + CC.AQUA + name + CC.GRAY + " asked for help: ");
            ComponentBuilder reasonBuilder = new ComponentBuilder("   " +CC.BLUE + "Reason: " + CC.GRAY + message);

            if(Spotify.SERVER_NAME.equalsIgnoreCase(server)) {
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp " + name));
                builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.GREEN + "Click to teleport to " + name).create()));
            } else {
                builder.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/join " + server));
                builder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(CC.BLUE + "Click to be sent to " + server + "\n  " + CC.BD_RED + "Warning:" + CC.RED + " This will switch your server!").create()));
            }

            Common.getOnlinePlayers().forEach(player -> {
                if(Permission.test(player, PermLevel.STAFF)) {
                    player.spigot().sendMessage(builder.create());
                    player.spigot().sendMessage(reasonBuilder.create());
                }
            });
            Bukkit.getConsoleSender().sendMessage(TextComponent.toPlainText(builder.create()));
        }
    }


}
