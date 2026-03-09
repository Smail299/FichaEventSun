package ua.fichadev.fichaeventsun.core;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import ua.fichadev.fichaeventsun.FichaEventSun;

public class SunCore {

    private final FichaEventSun plugin;

    public SunCore(FichaEventSun plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        Location loc = plugin.getCfg().getCoreLocation();
        if (loc == null || loc.getWorld() == null) {
            plugin.getLogger().warning("Некорректная локация ядра");
            return;
        }

        Block block = loc.getBlock();
        if (block.getType() != Material.RESPAWN_ANCHOR) {
            block.setType(Material.RESPAWN_ANCHOR);
        }

        plugin.getHologramManager().spawn();
        plugin.getPhaseManager().startCycle();
    }

    public void shutdown() {
        plugin.getPhaseManager().stop();
        plugin.getHologramManager().removeAll();
        plugin.getHologramManager().cleanupOrphaned();
    }
}
