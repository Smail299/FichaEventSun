package ua.fichadev.fichaeventsun.task;

import org.bukkit.scheduler.BukkitRunnable;
import ua.fichadev.fichaeventsun.FichaEventSun;

public class RewardTask {

    private final FichaEventSun plugin;

    public RewardTask(FichaEventSun plugin) {
        this.plugin = plugin;
    }

    public void start() {
        int interval = plugin.getCfg().getRewardIntervalTicks();
        new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getRewardManager().distributeRewards();
            }
        }.runTaskTimer(plugin, interval, interval);
    }
}
