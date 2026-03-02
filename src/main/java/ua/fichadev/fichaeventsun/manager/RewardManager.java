package ua.fichadev.fichaeventsun.manager;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import ua.fichadev.fichaeventsun.FichaEventSun;
import ua.fichadev.fichaeventsun.model.Phase;
import ua.fichadev.fichaeventsun.util.ColorUtils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class RewardManager {

    private final FichaEventSun plugin;
    private static final DecimalFormat MONEY_FORMAT;

    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator('.');
        MONEY_FORMAT = new DecimalFormat("#,##0.00", symbols);
    }

    public RewardManager(FichaEventSun plugin) {
        this.plugin = plugin;
    }

    public List<Player> getPlayersInRadius() {
        Location coreLoc = plugin.getCfg().getCoreLocation();
        if (coreLoc == null || coreLoc.getWorld() == null) return new ArrayList<>();

        int radius = plugin.getCfg().getRadius();
        int radiusSq = radius * radius;
        List<Player> nearby = new ArrayList<>();

        for (Player player : coreLoc.getWorld().getPlayers()) {
            if (player.getLocation().distanceSquared(coreLoc) <= radiusSq) {
                nearby.add(player);
            }
        }
        return nearby;
    }

    public void distributeRewards() {
        List<Player> players = getPlayersInRadius();
        if (players.isEmpty()) return;

        Phase phase = plugin.getPhaseManager().getCurrentPhase();
        int count = players.size();
        switch (phase) {
            case COINS: {
                double totalMoney = randomDouble(plugin.getCfg().getMoneyMin(), plugin.getCfg().getMoneyMax());
                double moneyPerPlayer = totalMoney / count;
                for (Player player : players) {
                    plugin.getEconomy().depositPlayer(player, moneyPerPlayer);
                    String msg = plugin.getCfg().getMoneyMessage()
                            .replace("%amount%", MONEY_FORMAT.format(moneyPerPlayer));
                    player.sendMessage(ColorUtils.colorize(msg));
                }
                break;
            }
            case EXPERIENCE: {
                int totalExp = randomInt(plugin.getCfg().getExpMin(), plugin.getCfg().getExpMax());
                int expPerPlayer = Math.max(1, totalExp / count);
                for (Player player : players) {
                    player.giveExp(expPerPlayer);
                }
                break;
            }
        }
    }

    private double randomDouble(double min, double max) {
        if (min >= max) return min;
        return min + ThreadLocalRandom.current().nextDouble() * (max - min);
    }

    private int randomInt(int min, int max) {
        if (min >= max) return min;
        return ThreadLocalRandom.current().nextInt(min, max + 1);
    }
}
