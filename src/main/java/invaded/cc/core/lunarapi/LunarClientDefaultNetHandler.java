package invaded.cc.core.lunarapi;

import invaded.cc.core.lunarapi.nethandler.client.LCPacketClientVoice;
import invaded.cc.core.lunarapi.nethandler.client.LCPacketVoiceChannelSwitch;
import invaded.cc.core.lunarapi.nethandler.client.LCPacketVoiceMute;
import invaded.cc.core.lunarapi.nethandler.server.LCNetHandlerServer;
import invaded.cc.core.lunarapi.nethandler.server.LCPacketStaffModStatus;
import invaded.cc.core.lunarapi.nethandler.shared.LCPacketEmoteBroadcast;
import invaded.cc.core.lunarapi.nethandler.shared.LCPacketWaypointAdd;
import invaded.cc.core.lunarapi.nethandler.shared.LCPacketWaypointRemove;

/**
 * An empty implementation of the server nethandler.
 */
public class LunarClientDefaultNetHandler implements LCNetHandlerServer {

    /**
     * Called when a player changes their staff mode state.
     * Not currently implemented.
     * @param lcPacketStaffModStatus All the status of the players staff mode.
     */
    @Override
    public void handleStaffModStatus(LCPacketStaffModStatus lcPacketStaffModStatus) {

    }

    /**
     * Called when the client adds or edits a waypoint when the server controls waypoints.
     * A waypoint that is edited will call handleRemoveWaypoint before creating the new waypoint.
     * See {@link invaded.cc.core.lunarapi.nethandler.client.obj.ServerRule} for how to control waypoints.
     *
     * @param lcPacketWaypointAdd The waypoint added by the client.
     */
    @Override
    public void handleAddWaypoint(LCPacketWaypointAdd lcPacketWaypointAdd) {

    }

    /**
     * Called when a player removes a waypoint. See above (handleAddWaypoint) for more detail.
     * @param lcPacketWaypointRemove The waypoint name and world removed by the player.
     */
    @Override
    public void handleRemoveWaypoint(LCPacketWaypointRemove lcPacketWaypointRemove) {

    }

    // Do not implement these, they do nothing and will not work.


    /**
     * Deprecated entirely from a time when Emotes went through the server.
     */
    @Override
    @Deprecated
    public void handleEmote(LCPacketEmoteBroadcast lcPacketEmoteBroadcast) {
        // DO NOT ATTEMPT TO IMPLEMENT
    }

    /**
     * Deprecated entirely, may return once a solution for voice is found.
     *
     * Not currently a work in progress.
     */
    @Override
    @Deprecated
    public void handleVoice(LCPacketClientVoice lcPacketClientVoice) {
        // DO NOT ATTEMPT TO IMPLEMENT
    }

    /**
     * Deprecated entirely, may return once a solution for voice is found.
     *
     * Not currently a work in progress.
     */
    @Override
    @Deprecated
    public void handleVoiceMute(LCPacketVoiceMute lcPacketVoiceMute) {
        // DO NOT ATTEMPT TO IMPLEMENT
    }

    /**
     * Deprecated entirely, may return once a solution for voice is found.
     *
     * Not currently a work in progress.
     */
    @Override
    @Deprecated
    public void handleVoiceChannelSwitch(LCPacketVoiceChannelSwitch lcPacketVoiceChannelSwitch) {
        // DO NOT ATTEMPT TO IMPLEMENT
    }
}
