package invaded.cc.core.tablist;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import invaded.cc.core.tablist.entry.TabElement;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TabUpdater implements Runnable {

	private final invaded.cc.core.tablist.TabHandler handler;
	private final ScheduledThreadPoolExecutor executor;
	private final ScheduledFuture<?> updater;

	/**
	 * Constructor to make a new {@link TabUpdater}
	 *
	 * @param handler the handler to register it to
	 * @param ticks   the amount it should update
	 */
	public TabUpdater(TabHandler handler, long ticks) {
		this.handler = handler;

		this.executor = new ScheduledThreadPoolExecutor(1,
				new ThreadFactoryBuilder().setNameFormat("Tab Thread %s").setDaemon(true).build());
		this.executor.setRemoveOnCancelPolicy(true);

		this.updater = this.executor.scheduleAtFixedRate(this, 20L, ticks, TimeUnit.MILLISECONDS);
	}

	@Override
	public void run() {
		try {
			for (Player player : Bukkit.getOnlinePlayers()) {
				sendUpdate(player);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update the tablist for a player
	 * 
	 * @param player the player to update it for
	 */
	public void sendUpdate(Player player) {
		final TabElement element = this.handler.getHandler().getElement(player);
		final TabAdapter adapter = this.handler.getAdapter(player);

		if (adapter != null) {
			adapter.handleElement(player, element).sendHeaderFooter(player, element.getHeader(),
					element.getFooter());
		}

	}

	/**
	 * Stop tab thread
	 */
	public void cancelExecutors() {
		if (updater != null) {
			updater.cancel(true);
		}

		if (executor != null) {
			executor.shutdown();
		}
	}
}
