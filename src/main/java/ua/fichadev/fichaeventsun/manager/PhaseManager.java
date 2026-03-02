package ua.fichadev.fichaeventsun.manager;

import org.bukkit.scheduler.BukkitRunnable;
import ua.fichadev.fichaeventsun.FichaEventSun;
import ua.fichadev.fichaeventsun.model.Phase;

public class PhaseManager {

    private final FichaEventSun plugin;
    private Phase currentPhase;
    private BukkitRunnable phaseTask;

    public PhaseManager(FichaEventSun plugin) {
        this.plugin = plugin;
        this.currentPhase = Phase.COINS;
    }

    public void startCycle() {
        int interval = plugin.getCfg().getPhaseChangeTicks();
        phaseTask = new BukkitRunnable() {
            @Override
            public void run() {
                currentPhase = currentPhase.next();
                plugin.getHologramManager().update();
            }
        };
        phaseTask.runTaskTimer(plugin, interval, interval);
    }

    public void stop() {
        if (phaseTask != null) {
            phaseTask.cancel();
        }
    }

    public Phase getCurrentPhase() {
        return currentPhase;
    }
}
