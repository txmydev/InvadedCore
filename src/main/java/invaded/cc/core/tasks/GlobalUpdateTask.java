package invaded.cc.core.tasks;

import org.bukkit.scheduler.BukkitRunnable;

public class GlobalUpdateTask extends BukkitRunnable {
    @Override
    public void run() {

    }

  /*  @Override
    public void run() {
        try {
            Common.getOnlinePlayers().forEach(GlobalUpdateTask::performUpdate);
        }catch(Exception ex) {
            cancel();
            ex.printStackTrace();
        }
    }

    public static void performUpdate(Player player) {
        JsonChain jsonChain = new JsonChain();

        User globalPlayer = Core.getInstance().getServerHandler().find(player.getName());
        Profile profile = Core.getInstance().getProfileHandler().getProfile(player.getUniqueId());

        String disguiseData = !profile.isDisguised() ? "" :
                profile.getFakeName() + ";"
                + profile.getFakeSkin().getTexture() +","
                + profile.getFakeSkin().getSignature() + ";"
                + profile.getFakeRank().getName() + ";";

        jsonChain
                .addProperty("name", player.getName())
                .addProperty("uuid", player.getUniqueId().toString())
                .addProperty("lastUpdate", System.currentTimeMillis())
                .addProperty("switchingServer", globalPlayer != null && globalPlayer.isSwitchingServer())
                .addProperty("rank", profile.getHighestRank().getName())
                .addProperty("lastServer", Core.getInstance().getServerName())
                .addProperty("disguised", profile.isDisguised())
                .addProperty("disguiseData",disguiseData);

        Core.getInstance().getDb().getRedisManager().getPlayerUpdatePublisher().write(jsonChain.get());
    }*/

}
