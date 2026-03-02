package ua.fichadev.fichaeventsun.task;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.dimasik.lootmanager.LootAPI;
import ua.fichadev.fichaeventsun.FichaEventSun;

import java.util.ArrayList;
import java.util.List;

public class LootDropTask {

    private final FichaEventSun plugin;

    public LootDropTask(FichaEventSun plugin) {
        this.plugin = plugin;
    }

    public void start() {
        int interval = plugin.getCfg().getLootDropIntervalTicks();
        new BukkitRunnable() {
            @Override
            public void run() {
                dropLoot();
                plugin.getHologramManager().setLootCountdown(interval / 20);
            }
        }.runTaskTimer(plugin, interval, interval);
    }

    private void dropLoot() {
        Location coreLoc = plugin.getCfg().getCoreLocation();
        if (coreLoc == null || coreLoc.getWorld() == null) return;

        List<Player> players = plugin.getRewardManager().getPlayersInRadius();
        if (players.isEmpty()) return;

        String source = plugin.getCfg().getLootSource();
        List<ItemStack> items = new ArrayList<>();

        if (source.equalsIgnoreCase("lootmanager") || source.equalsIgnoreCase("both")) {
            items.addAll(getLootManagerItems());
        }

        if (source.equalsIgnoreCase("custom") || source.equalsIgnoreCase("both")) {
            items.addAll(getCustomItems());
        }

        if (items.isEmpty()) return;

        Location dropLoc = coreLoc.clone().add(0.5, 1.0, 0.5);
        for (ItemStack item : items) {
            if (item != null) {
                coreLoc.getWorld().dropItemNaturally(dropLoc, item);
            }
        }
    }

    private List<ItemStack> getLootManagerItems() {
        int level = plugin.getCfg().getLootLevel();
        int count = plugin.getCfg().getLootCount();
        String category = plugin.getCfg().getLootCategory();

        LootAPI lootAPI = new LootAPI();
        List<ItemStack> items;

        if (category != null && !category.isEmpty()) {
            items = lootAPI.getRandomItemsByCategoryByLvL(count, category, level);
        } else {
            items = lootAPI.getRandomItemsByExactLevel(count, level);
        }

        return items != null ? items : new ArrayList<>();
    }

    private List<ItemStack> getCustomItems() {
        List<ItemStack> result = new ArrayList<>();
        int count = plugin.getCfg().getCustomLootCount();

        for (int i = 0; i < count; i++) {
            ItemStack item = plugin.getCustomLootManager().getRandomItem();
            if (item != null) {
                result.add(item);
            }
        }

        return result;
    }
}
