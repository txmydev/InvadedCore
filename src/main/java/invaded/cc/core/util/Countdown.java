package invaded.cc.core.util;

import invaded.cc.core.Spotify;
import lombok.NoArgsConstructor;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.function.Consumer;

@NoArgsConstructor
public class Countdown {

    public static Countdown build() {
        return new Countdown();
    }


    private int timer;
    private long startDelay, period;
    private Consumer<Integer> tick;
    private Runnable onFinish;
    private boolean async = true;

    public Countdown(int timer, long startDelay, long period) {
        this.timer = timer;
        this.startDelay = startDelay;
        this.period = period;
    }

    public Countdown(int timer, long startDelay, long period, Consumer<Integer> tick, Runnable onFinish) {
        this.timer = timer;
        this.startDelay = startDelay;
        this.period = period;
        this.tick = tick;
        this.onFinish = onFinish;
    }

    public Countdown setAsync(boolean async) {
        this.async = async;
        return this;
    }

    public Countdown setTimer(int timer) {
        this.timer = timer;
        return this;
    }

    public Countdown setStartDelay(long startDelay) {
        this.startDelay = startDelay;
        return this;
    }

    public Countdown setPeriod(long period) {
        this.period = period;
        return this;
    }


    public Countdown setTick(Consumer<Integer> tick) {
        this.tick = tick;
        return this;
    }

    public Countdown setFinish(Runnable runnable) {
        this.onFinish = runnable;
        return this;
    }

    public void execute() {
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                if(timer <= 0) {
                    cancel();
                    onFinish.run();
                    return;
                }

                tick.accept(timer--);
            }
        };

        Spotify plugin = Spotify.getInstance();
        if(this.async) task.runTaskTimerAsynchronously(plugin, startDelay, period);
        else task.runTaskTimer(plugin, startDelay, period);
    }
}
