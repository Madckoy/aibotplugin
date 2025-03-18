package com.devone.aibot.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.devone.aibot.utils.BotLogger;

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

           
    public void pickupAll(Boolean shouldPickup, Boolean autoPickupEnabled) {

        logInventory();

        if (!shouldPickup || !autoPickupEnabled || !bot.isNPCSpawned() || bot.getNPC() == null) {
            BotLogger.debug("🛒 " + bot.getId()+" Не будет подбирать материал! Параметры подбора: " + shouldPickup + " | " + autoPickupEnabled );
            return;
        }

        BotLogger.debug("🛒 " + bot.getId()+" Будет подобирать материал! Параметры подбора: " + shouldPickup + " | " + autoPickupEnabled );

        //BotScanEnv.logScanNatural(bot, 20.0);

        if(autoPickupEnabled) {
            pullAllItemsinRadius(2.0);
        }

        Location botLocation = bot.getNPCCurrentLocation();
        List<Entity> nearbyEntities = botLocation.getWorld().getEntities();
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Item) {
                Item item = (Item) entity;
                if (botLocation.distance(item.getLocation()) < 2.0) {
                    Material material = item.getItemStack().getType();
                    int amount = item.getItemStack().getAmount();
                    
                    addItem(material, amount); // Передаём два параметра в инвентарь
                    
                    item.remove(); // Удаляем предмет с земли
                    BotLogger.debug("🛒 " + bot.getId() +  " Подобрал " + amount + " x " + material);
                }
            }
        }
    }
    
    public static boolean hasEnoughBlocks(Bot bot, Set<Material> targetMaterials, int maxBlocksPerMaterial) {

        if(bot.getInventory().getNPCInventory() == null) {
            BotLogger.debug("🛒 " + bot.getId()+" Has no inventory yet!");
            return true;
        } //not yet created

        Map<Material, Integer> collectedCounts = new HashMap<>();

        // Считаем количество каждого целевого материала в инвентаре
        for (ItemStack item : bot.getInventory().getNPCInventory().getContents()) {
            if (item != null && targetMaterials.contains(item.getType())) {
                collectedCounts.put(item.getType(), collectedCounts.getOrDefault(item.getType(), 0) + item.getAmount());
            }
        }

        // Проверяем, достигнуто ли нужное количество для любого из целевых материалов
        for (Material material : targetMaterials) {
            int count = collectedCounts.getOrDefault(material, 0);
            BotLogger.debug("📦 " + bot.getId() + " | " + material + ": ( " + count + "/" + maxBlocksPerMaterial+")");

            if (count >= maxBlocksPerMaterial) {
                return true; // Достигнута цель по какому-то материалу → завершаем задачу
            }
        }

        return false; // Ещё не набрали нужное количество
    }

    public static boolean hasFreeInventorySpace(Bot bot, Set<Material> targetMaterials) {

        if(bot.getInventory().getNPCInventory() == null) {
            BotLogger.debug("🛒 " + bot.getId()+" Has no inventory yet!");
            return true;
        } //not yet created

        for (ItemStack item : bot.getInventory().getNPCInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return true; // Есть свободный слот
            }
    
            // Проверяем, есть ли место в существующем стаке для любого из целевых материалов
            if (targetMaterials.contains(item.getType()) && item.getAmount() < item.getMaxStackSize()) {
                return true; // В этом слоте можно добавить ещё
            }
        }
        return false; // Нет места ни в одном слоте
    }
    
    public static void dropAllItems(Bot bot) {
        Inventory inventory = bot.getInventory().getNPCInventory();
        if( inventory == null) {
            BotLogger.debug("🛒 " + bot.getId()+" Has no inventory yet!");
            return;
        } //not yet created
        
        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                bot.getNPCEntity().getWorld().dropItemNaturally(bot.getNPCEntity().getLocation(), item);
            }
        }
        
        inventory.clear(); // Полностью очищаем инвентарь после выброса
        BotLogger.debug("🚮 " + bot.getId() + " Выбросил все предметы из инвентаря!");
    }

    public void pullAllItemsinRadius(double radius) {
        List<Entity> nearbyItems = bot.getNPCEntity().getNearbyEntities(radius, radius, radius);
        
        for (Entity entity : nearbyItems) {
            if (entity instanceof Item) {
                    entity.teleport(bot.getNPCEntity().getLocation()); // Притягиваем предмет
                    BotLogger.debug("🛒 " + bot.getId()+" Pulled up a near item!");
            }
        }
    }

    public void logInventory() {
        Inventory inventory = getNPCInventory();
        
        if (inventory == null) {
            return;
        }

        List<String> rows = new ArrayList<>();
        int columns = 9; // Количество слотов в строке (как в GUI)

        StringBuilder row = new StringBuilder("| ");
        int count = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack item = inventory.getItem(i);
            if (item != null && item.getType() != Material.AIR) {
                String material = item.getType().toString();
                int amount = item.getAmount();
                row.append(String.format("%2d x %-15s ", amount, material)); // Количество -> Материал
            } else {
                row.append(String.format("-- x %-15s ", "------")); // Пустой слот
            }

            count++;
            if (count == columns) {
                rows.add(row.toString() + "|");
                row = new StringBuilder("| ");
                count = 0;
            }
        }

        // Вывод в логи
        BotLogger.trace("🎁 " + bot.getId() + "Инвентарь:");
        for (String r : rows) {
            BotLogger.trace(r);
        }
    }


}



