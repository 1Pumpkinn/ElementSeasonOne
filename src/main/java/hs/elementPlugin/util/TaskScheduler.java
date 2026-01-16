package hs.elementPlugin.util;

import hs.elementPlugin.ElementPlugin;
import hs.elementPlugin.core.Constants;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

/**
 * Utility for common scheduling patterns
 */
public class TaskScheduler {
    private final ElementPlugin plugin;

    public TaskScheduler(ElementPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Run task after delay in ticks
     */
    public BukkitTask runLater(Runnable task, long delayTicks) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskLater(plugin, delayTicks);
    }

    /**
     * Run task after delay in seconds
     */
    public BukkitTask runLaterSeconds(Runnable task, int seconds) {
        return runLater(task, seconds * Constants.TICKS_PER_SECOND);
    }

    /**
     * Run repeating task
     */
    public BukkitTask runTimer(Runnable task, long delayTicks, long periodTicks) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                task.run();
            }
        }.runTaskTimer(plugin, delayTicks, periodTicks);
    }

    /**
     * Run repeating task every second
     */
    public BukkitTask runTimerSeconds(Runnable task, int delaySeconds, int periodSeconds) {
        return runTimer(task,
                delaySeconds * Constants.TICKS_PER_SECOND,
                periodSeconds * Constants.TICKS_PER_SECOND
        );
    }

    /**
     * Common pattern: Run after player fully loads
     */
    public BukkitTask runAfterPlayerLoad(Runnable task) {
        return runLater(task, Constants.HALF_SECOND);
    }

    /**
     * Common pattern: Cleanup task
     */
    public BukkitTask runCleanup(Runnable task) {
        return runLater(task, Constants.TAP_CLEANUP_DELAY);
    }
}