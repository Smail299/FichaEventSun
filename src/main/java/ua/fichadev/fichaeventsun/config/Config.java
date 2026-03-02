package ua.fichadev.fichaeventsun.config;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import ua.fichadev.fichaeventsun.FichaEventSun;

public class Config {

    private final FichaEventSun plugin;

    public Config(FichaEventSun plugin) {
        this.plugin = plugin;
    }

    public Location getCoreLocation() {
        String worldName = plugin.getConfig().getString("core.world", "spawn");
        World world = Bukkit.getWorld(worldName);
        if (world == null) return null;
        double x = plugin.getConfig().getDouble("core.x", -121);
        double y = plugin.getConfig().getDouble("core.y", 48);
        double z = plugin.getConfig().getDouble("core.z", -28);
        return new Location(world, x, y, z);
    }

    public int getRadius() {
        return plugin.getConfig().getInt("core.radius", 12);
    }

    public int getRewardIntervalTicks() {
        return plugin.getConfig().getInt("rewards.interval-seconds", 5) * 20;
    }

    public double getMoneyMin() {
        return plugin.getConfig().getDouble("rewards.money-min", 8500.0);
    }

    public double getMoneyMax() {
        return plugin.getConfig().getDouble("rewards.money-max", 9700.0);
    }

    public int getExpMin() {
        return plugin.getConfig().getInt("rewards.exp-min", 20);
    }

    public int getExpMax() {
        return plugin.getConfig().getInt("rewards.exp-max", 40);
    }

    public String getMoneyMessage() {
        return plugin.getConfig().getString("messages.money-received", "&6▶ &fВаш баланс пополнен на &6%amount% ¤");
    }

    public int getPhaseChangeTicks() {
        return plugin.getConfig().getInt("phase.change-minutes", 40) * 60 * 20;
    }

    public int getLootDropIntervalTicks() {
        return plugin.getConfig().getInt("loot.drop-interval-seconds", 16) * 20;
    }

    public String getLootSource() {
        return plugin.getConfig().getString("loot.source", "custom");
    }

    public int getLootLevel() {
        return plugin.getConfig().getInt("loot.lootmanager.level", 1);
    }

    public int getLootCount() {
        return plugin.getConfig().getInt("loot.lootmanager.count", 1);
    }

    public String getLootCategory() {
        return plugin.getConfig().getString("loot.lootmanager.category", "");
    }

    public int getCustomLootCount() {
        return plugin.getConfig().getInt("loot.custom.count", 1);
    }

    public String getEnterMessage() {
        return plugin.getConfig().getString("messages.enter", "&b▶ &fВы вошли на территорию &6Ядра Солнца&f. Находитесь в радиусе &b%radius% блоков&f, чтобы получать %reward%&f.");
    }

    public String getHologramTitle() {
        return plugin.getConfig().getString("hologram.title", "&6Ядро Солнца");
    }

    public String getHologramPhaseLine() {
        return plugin.getConfig().getString("hologram.phase-line", "&fСейчас выдаёт: %phase%");
    }

    public String getHologramTimerLine() {
        return plugin.getConfig().getString("hologram.timer-line", "&fЛут через &x&0&0&D&0&F&8%time% сек.");
    }

    public double getHologramOffset() {
        return plugin.getConfig().getDouble("hologram.y-offset", 1.5);
    }
}