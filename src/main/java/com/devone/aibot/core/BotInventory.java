package com.devone.aibot.core;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class BotInventory {

    private final Bot bot;
    public BotInventory(Bot bot) {
        this.bot = bot;
        getNPCInventory();
    }

    public Inventory getNPCInventory() {
        if (this.bot.getNPCEntity() instanceof InventoryHolder) {
            return ((InventoryHolder) this.bot.getNPCEntity()).getInventory();
        }
        return null; // NPC не имеет инвентаря
    }
    
    public int getAmount(Material material) {
        Inventory inv = getNPCInventory();
        if (inv == null) return 0; // Если инвентарь недоступен, возвращаем 0

        int total = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material) {
                total += item.getAmount();
            }
        }
        return total;
    }    

    public void addItem(Material material, int amount) {
        Inventory inv = getNPCInventory();
        if (inv == null) return; // Нет инвентаря - выходим
    
        inv.addItem(new ItemStack(material, amount));
    }
    
    public boolean removeItem(Material material, int amount) {
        Inventory inv = getNPCInventory();
        if (inv == null) return false; // Нет инвентаря - не можем удалить
    
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material) {
                int itemAmount = item.getAmount();
                if (itemAmount > amount) {
                    item.setAmount(itemAmount - amount);
                    return true;
                } else {
                    inv.remove(item);
                    amount -= itemAmount;
                    if (amount <= 0) return true;
                }
            }
        }
        return false;
    }
    

}



