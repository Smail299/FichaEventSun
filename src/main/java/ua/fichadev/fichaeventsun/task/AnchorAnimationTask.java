package ua.fichadev.fichaeventsun.task;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.scheduler.BukkitRunnable;
import ua.fichadev.fichaeventsun.FichaEventSun;

public class AnchorAnimationTask {

    private final FichaEventSun plugin;
    private int tickCounter;

    public AnchorAnimationTask(FichaEventSun plugin) {
        this.plugin = plugin;
        this.tickCounter = 0;
    }

    public void start() {
        int lootInterval = plugin.getCfg().getLootDropIntervalTicks();
        int stageInterval = lootInterval / 4;

        new BukkitRunnable() {
            @Override
            public void run() {
                tickCounter += 20;

                Location coreLoc = plugin.getCfg().getCoreLocation();
                if (coreLoc == null || coreLoc.getWorld() == null) return;

                Block block = coreLoc.getBlock();
                if (block.getType() != Material.RESPAWN_ANCHOR) return;

                RespawnAnchor anchor = (RespawnAnchor) block.getBlockData();
                int stage = Math.min(tickCounter / stageInterval, 4);
                anchor.setCharges(stage);
                block.setBlockData(anchor, false);

                int remaining = (lootInterval - tickCounter) / 20;
                if (remaining < 0) remaining = 0;
                plugin.getHologramManager().setLootCountdown(remaining);

                if (tickCounter >= lootInterval) {
                    tickCounter = 0;
                    anchor.setCharges(0);
                    block.setBlockData(anchor, false);
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
