package ua.fichadev.fichaeventsun;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Location;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ua.fichadev.fichaeventsun.command.LootCommand;
import ua.fichadev.fichaeventsun.config.Config;
import ua.fichadev.fichaeventsun.core.SunCore;
import ua.fichadev.fichaeventsun.listener.PlayerMoveListener;
import ua.fichadev.fichaeventsun.manager.CustomLootManager;
import ua.fichadev.fichaeventsun.manager.HologramManager;
import ua.fichadev.fichaeventsun.manager.PhaseManager;
import ua.fichadev.fichaeventsun.manager.RewardManager;
import ua.fichadev.fichaeventsun.task.AnchorAnimationTask;
import ua.fichadev.fichaeventsun.task.LootDropTask;
import ua.fichadev.fichaeventsun.task.RewardTask;

public final class FichaEventSun extends JavaPlugin {

    private static FichaEventSun instance;
    private Economy economy;
    private Config cfg;
    private PhaseManager phaseManager;
    private RewardManager rewardManager;
    private HologramManager hologramManager;
    private CustomLootManager customLootManager;
    private SunCore sunCore;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        if (!setupEconomy()) {
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        cfg = new Config(this);
        customLootManager = new CustomLootManager(this);
        phaseManager = new PhaseManager(this);
        rewardManager = new RewardManager(this);
        hologramManager = new HologramManager(this);
        sunCore = new SunCore(this);

        new org.bukkit.scheduler.BukkitRunnable() {
            private int attempts = 0;

            @Override
            public void run() {
                attempts++;
                Location loc = cfg.getCoreLocation();
                if (loc != null && loc.getWorld() != null) {
                    sunCore.initialize();
                    new RewardTask(FichaEventSun.this).start();
                    new LootDropTask(FichaEventSun.this).start();
                    new AnchorAnimationTask(FichaEventSun.this).start();
                    cancel();
                    return;
                }
                if (attempts >= 30) {
                    cancel();
                }
            }
        }.runTaskTimer(this, 20L, 20L);

        getServer().getPluginManager().registerEvents(new PlayerMoveListener(this), this);

        LootCommand lootCommand = new LootCommand(this);
        getCommand("fichasun").setExecutor(lootCommand);
        getCommand("fichasun").setTabCompleter(lootCommand);
    }

    @Override
    public void onDisable() {
        if (hologramManager != null) {
            hologramManager.removeAll();
        }
        if (sunCore != null) {
            sunCore.shutdown();
        }
        instance = null;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static FichaEventSun getInstance() {
        return instance;
    }

    public Economy getEconomy() {
        return economy;
    }

    public Config getCfg() {
        return cfg;
    }

    public PhaseManager getPhaseManager() {
        return phaseManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public HologramManager getHologramManager() {
        return hologramManager;
    }

    public CustomLootManager getCustomLootManager() {
        return customLootManager;
    }

    public SunCore getSunCore() {
        return sunCore;
    }
}