package ua.fichadev.fichaeventsun.manager;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import ua.fichadev.fichaeventsun.FichaEventSun;
import ua.fichadev.fichaeventsun.model.Phase;
import ua.fichadev.fichaeventsun.util.ColorUtils;

import java.util.ArrayList;
import java.util.List;

public class HologramManager {

    private final FichaEventSun plugin;
    private final List<ArmorStand> lines = new ArrayList<>();
    private int lootCountdown;

    public HologramManager(FichaEventSun plugin) {
        this.plugin = plugin;
        this.lootCountdown = plugin.getCfg().getLootDropIntervalTicks() / 20;
    }

    public void spawn() {
        removeAll();
        cleanupOrphaned();

        Location base = plugin.getCfg().getCoreLocation();
        if (base == null || base.getWorld() == null) return;

        double yOffset = plugin.getCfg().getHologramOffset();
        Location loc = base.clone().add(0.5, yOffset, 0.5);

        lines.add(createLine(loc.clone().add(0, 0.75, 0), formatTitle()));
        lines.add(createLine(loc.clone().add(0, 0.25, 0), formatPhaseLine()));
        lines.add(createLine(loc, formatTimerLine()));
    }

    public void cleanupOrphaned() {
        Location base = plugin.getCfg().getCoreLocation();
        if (base == null || base.getWorld() == null) return;

        for (Entity entity : base.getWorld().getNearbyEntities(base, 3, 5, 3)) {
            if (entity.getType() != EntityType.ARMOR_STAND) continue;
            ArmorStand stand = (ArmorStand) entity;
            if (!stand.isVisible() && stand.isMarker() && stand.isCustomNameVisible()) {
                stand.remove();
            }
        }
    }

    private ArmorStand createLine(Location location, String text) {
        ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCustomName(text);
        stand.setCustomNameVisible(true);
        stand.setMarker(true);
        stand.setSmall(true);
        stand.setInvulnerable(true);
        return stand;
    }

    public void update() {
        if (lines.size() < 3) return;
        lines.get(0).setCustomName(formatTitle());
        lines.get(1).setCustomName(formatPhaseLine());
        lines.get(2).setCustomName(formatTimerLine());
    }

    public void setLootCountdown(int seconds) {
        this.lootCountdown = seconds;
        if (lines.size() >= 3) {
            lines.get(2).setCustomName(formatTimerLine());
        }
    }

    public int getLootCountdown() {
        return lootCountdown;
    }

    public void removeAll() {
        for (ArmorStand stand : lines) {
            if (stand != null && !stand.isDead()) {
                stand.remove();
            }
        }
        lines.clear();
    }

    private String formatTitle() {
        return ColorUtils.colorize(
                plugin.getCfg().getHologramTitle());
    }

    private String formatPhaseLine() {
        Phase phase = plugin.getPhaseManager().getCurrentPhase();
        String phaseColor = ColorUtils.colorize(phase.getDisplayName());
        return ColorUtils.colorize(
                plugin.getCfg().getHologramPhaseLine()
                        .replace("%phase%", phaseColor));
    }

    private String formatTimerLine() {
        return ColorUtils.colorize(
                plugin.getCfg().getHologramTimerLine()
                        .replace("%time%", String.valueOf(lootCountdown)));
    }
}
