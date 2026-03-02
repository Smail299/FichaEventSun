package ua.fichadev.fichaeventsun.command;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ua.fichadev.fichaeventsun.FichaEventSun;
import ua.fichadev.fichaeventsun.util.ColorUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class LootCommand implements CommandExecutor, TabCompleter {

    private final FichaEventSun plugin;

    public LootCommand(FichaEventSun plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) return true;

        switch (args[0].toLowerCase()) {
            case "addloot":
                return handleAddLoot(sender, args);
            case "removeloot":
                return handleRemoveLoot(sender, args);
            case "list":
                return handleList(sender);
            default:
                return true;
        }
    }

    private boolean handleAddLoot(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ColorUtils.colorize("это консоль"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(ColorUtils.colorize("/fichasun addloot <ids>"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand == null || hand.getType() == Material.AIR) {
            sender.sendMessage(ColorUtils.colorize("возьмите предмет в руку"));
            return true;
        }

        String id = args[1].toLowerCase();

        if (plugin.getCustomLootManager().addItem(id, hand)) {
            String itemName = getItemDisplayName(hand);
            sender.sendMessage(ColorUtils.colorize("предмет " + itemName + " добавлен в лут с id: " + id));
        } else {
            sender.sendMessage(ColorUtils.colorize("предмет уже существует с таким id"));
        }

        return true;
    }

    private boolean handleRemoveLoot(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ColorUtils.colorize("/fichasun removeloot <id>"));
            return true;
        }

        String id = args[1].toLowerCase();

        if (plugin.getCustomLootManager().removeItem(id)) {
            sender.sendMessage(ColorUtils.colorize("предмет с id " + id + " удален из лута"));
        } else {
            sender.sendMessage(ColorUtils.colorize("предмет с iD " + id + " не найден"));
        }

        return true;
    }

    private boolean handleList(CommandSender sender) {
        List<String> ids = plugin.getCustomLootManager().getItemIds();

        if (ids.isEmpty()) {
            sender.sendMessage(ColorUtils.colorize("список лута пуст"));
            return true;
        }

        sender.sendMessage(ColorUtils.colorize("custom лут (&b" + ids.size() + "&f):"));
        for (String id : ids) {
            ItemStack item = plugin.getCustomLootManager().getItem(id);
            if (item == null) continue;
            String name = getItemDisplayName(item);
            int amount = item.getAmount();
            sender.sendMessage(ColorUtils.colorize(name + " &7- " + amount + " шт."));
        }

        return true;
    }

    private String getItemDisplayName(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                return meta.getDisplayName();
            }
        }
        return item.getType().name();
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterStartsWith(Arrays.asList("addloot", "removeloot", "list"), args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("removeloot")) {
            return filterStartsWith(plugin.getCustomLootManager().getItemIds(), args[1]);
        }
        return new ArrayList<>();
    }

    private List<String> filterStartsWith(List<String> list, String prefix) {
        String lower = prefix.toLowerCase();
        return list.stream()
                .filter(s -> s.toLowerCase().startsWith(lower))
                .collect(Collectors.toList());
    }
}