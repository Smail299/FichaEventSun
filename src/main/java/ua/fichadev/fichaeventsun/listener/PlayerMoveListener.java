package ua.fichadev.fichaeventsun.listener;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import ua.fichadev.fichaeventsun.FichaEventSun;
import ua.fichadev.fichaeventsun.model.Phase;
import ua.fichadev.fichaeventsun.util.ColorUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerMoveListener implements Listener {

    private final FichaEventSun plugin;
    private final Set<UUID> insideZone = new HashSet<>();

    public PlayerMoveListener(FichaEventSun plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (event.getTo() == null) return;
        if (event.getFrom().getBlockX() == event.getTo().getBlockX()
                && event.getFrom().getBlockY() == event.getTo().getBlockY()
                && event.getFrom().getBlockZ() == event.getTo().getBlockZ()) {
            return;
        }
        checkZone(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        if (event.getTo() == null) return;
        checkZone(event.getPlayer(), event.getTo());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            checkZone(player, player.getLocation());
        }, 5L);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        insideZone.remove(event.getPlayer().getUniqueId());
    }

    private void checkZone(Player player, Location to) {
        Location coreLoc = plugin.getCfg().getCoreLocation();
        if (coreLoc == null || coreLoc.getWorld() == null) return;
        if (to.getWorld() == null || !to.getWorld().equals(coreLoc.getWorld())) {
            insideZone.remove(player.getUniqueId());
            return;
        }

        int radius = plugin.getCfg().getRadius();
        int radiusSq = radius * radius;
        boolean isInside = to.distanceSquared(coreLoc) <= radiusSq;
        UUID uuid = player.getUniqueId();

        if (isInside && !insideZone.contains(uuid)) {
            insideZone.add(uuid);
            Phase phase = plugin.getPhaseManager().getCurrentPhase();
            String message = plugin.getCfg().getEnterMessage()
                    .replace("%radius%", String.valueOf(radius))
                    .replace("%reward%", phase.getRewardName());
            player.sendMessage(ColorUtils.colorize(message));
        } else if (!isInside && insideZone.contains(uuid)) {
            insideZone.remove(uuid);
        }
    }
}