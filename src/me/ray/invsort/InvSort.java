package me.ray.invsort;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class InvSort extends JavaPlugin {

    public void onEnable() {
        getLogger().info("Plugin made by: Ray");
        getLogger().info("The plugin has been enabled!");
    }

    public void onDisable() {
        getLogger().info("The plugin has been disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String string, String[] args) {
        if(!(sender instanceof Player)) {
            getLogger().info("This command can only be used by a player!");

            return false;
        }

        Player player = (Player) sender;

        if(!cmd.getName().equalsIgnoreCase("sortinv")) {
            return false;
        }

        if(!player.hasPermission("invsort.cmd")) {
            player.sendMessage(Utils.color("&cYou do not have permission to use this command!"));

            return false;
        }

        Inventory inventory = player.getInventory();
        ItemStack[] items = inventory.getContents();

        Map<String, Integer> data = new HashMap<>();

        for(ItemStack itemStack : items) {
            if(itemStack == null) {
                continue;
            }

            Material material = itemStack.getType();
            Byte materialData = itemStack.getData().getData();
            int amount = itemStack.getAmount();
            String key = "" + material.toString() + ", " + materialData;

            if(data.get(key) == null) {
                data.put(key, amount);
            } else {
                int currentAmt = data.get(key);
                data.put(key, currentAmt + amount);
            }
        }

        LinkedList<ItemStack> newItems = new LinkedList<>();

        for(String entry : data.keySet()) {

            String[] entryData = entry.split(", ");

            Material material = Material.valueOf(entryData[0]);
            Byte materialData = Byte.valueOf(entryData[1]);

            int amount = data.get(entry);
            int divider;

            switch (material) {
                case SNOW_BALL:
                case BUCKET:
                case EGG:
                case SIGN:
                case ENDER_PEARL:
                    divider = 16;

                default: divider = 64;
            }

            int stacks = amount / divider;
            int remaining = amount % divider;

            for(int i = 0; i < stacks; i++) {
                ItemStack itemStack = new ItemStack(material, divider, materialData);

                newItems.add(itemStack);
            }

            if(remaining != 0) {
                ItemStack itemStack = new ItemStack(material, remaining, materialData);

                newItems.add(itemStack);
            }
        }

        ItemStack[] itemArray = new ItemStack[newItems.size()];

        boolean sword = false;
        boolean bow = false;

        for(int i = 0; i < itemArray.length; i++) {
            ItemStack item = newItems.get(i);

            String type = item.getType().name().toLowerCase();

            if(type.contains("sword") && !sword) {
                sword = true;

                newItems.remove(i);
                newItems.addFirst(item);
            } else if(type.contains("bow") && !bow) {
                bow = true;

                newItems.remove(i);
                newItems.add(1, item);
            }
        }

        itemArray = newItems.toArray(itemArray);

        player.getInventory().setContents(itemArray);
        player.updateInventory();

        return true;
    }

}
