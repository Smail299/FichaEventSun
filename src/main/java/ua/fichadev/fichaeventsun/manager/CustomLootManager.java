package ua.fichadev.fichaeventsun.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import ua.fichadev.fichaeventsun.FichaEventSun;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CustomLootManager {

    private final FichaEventSun plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    public CustomLootManager(FichaEventSun plugin) {
        this.plugin = plugin;
        setupFile();
    }

    private void setupFile() {
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("не удалось создать data.yml");
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public void save() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("не удалось сохранить data.yml");
        }
    }

    public void reload() {
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    public boolean addItem(String id, ItemStack item) {
        if (dataConfig.contains("items." + id)) {
            return false;
        }
        dataConfig.set("items." + id, item);
        save();
        return true;
    }

    public boolean removeItem(String id) {
        if (!dataConfig.contains("items." + id)) {
            return false;
        }
        dataConfig.set("items." + id, null);
        save();
        return true;
    }

    public ItemStack getItem(String id) {
        return dataConfig.getItemStack("items." + id);
    }

    public List<String> getItemIds() {
        if (!dataConfig.contains("items")) {
            return new ArrayList<>();
        }
        Set<String> keys = dataConfig.getConfigurationSection("items").getKeys(false);
        return new ArrayList<>(keys);
    }

    public List<ItemStack> getAllItems() {
        List<ItemStack> items = new ArrayList<>();
        for (String id : getItemIds()) {
            ItemStack item = getItem(id);
            if (item != null) {
                items.add(item.clone());
            }
        }
        return items;
    }

    public ItemStack getRandomItem() {
        List<ItemStack> items = getAllItems();
        if (items.isEmpty()) return null;
        int index = (int) (Math.random() * items.size());
        return items.get(index);
    }
}