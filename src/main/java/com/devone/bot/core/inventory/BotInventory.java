package com.devone.bot.core.inventory;

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

import com.devone.bot.core.bot.Bot;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.scene.BotScanNatural;
import com.devone.bot.utils.world.BotWorldHelper;

public class BotInventory {

    private final Bot bot;

    public BotInventory(Bot bot) {
        this.bot = bot;
        getNPCInventory();
    }

    public Inventory getNPCInventory() {

        if (this.bot.getNPCEntity() == null) {
            return null;
        }

        if (this.bot.getNPCEntity() instanceof InventoryHolder) {
            return ((InventoryHolder) this.bot.getNPCEntity()).getInventory();
        }
        return null; // NPC не имеет инвентаря
    }

    public int getAmount(Material material) {

        if (this.bot.getNPCEntity() == null) {
            return 0;
        }

        Inventory inv = getNPCInventory();
        if (inv == null)
            return 0; // Если инвентарь недоступен, возвращаем 0

        int total = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material) {
                total += item.getAmount();
            }
        }
        return total;
    }

    public void addItem(Material material, int amount) {
        if (bot.getInventory().getNPCInventory() == null) {
            return;
        }

        Inventory inv = getNPCInventory();
        if (inv == null)
            return; // Нет инвентаря - выходим

        inv.addItem(new ItemStack(material, amount));
    }

    public boolean removeItem(Material material, int amount) {
        if (bot.getInventory().getNPCInventory() == null) {
            return false;
        }

        Inventory inv = getNPCInventory();
        if (inv == null)
            return false; // Нет инвентаря - не можем удалить

        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == material) {
                int itemAmount = item.getAmount();
                if (itemAmount > amount) {
                    item.setAmount(itemAmount - amount);
                    return true;
                } else {
                    inv.remove(item);
                    amount -= itemAmount;
                    if (amount <= 0)
                        return true;
                }
            }
        }
        return false;
    }

    public void pickupAll(Boolean shouldPickup) {
        BotLogger.info("🛒", true, bot.getId() + " Pickup parameters: " + shouldPickup +" | "+bot.getRuntimeStatus().getAutoPickupItems());

        if (!bot.isNPCSpawned() || bot.getNPC() == null) {
            BotLogger.info("🛒", true, bot.getId() + " NPC issue! Can't pickup items. Pickup parameters");
            return;
        }

        logInventory();


        if (!shouldPickup && !bot.getRuntimeStatus().getAutoPickupItems() ) {
            BotLogger.info("🛒", true, bot.getId() + " Will not pickup items. Pickup parameters: " + shouldPickup +" | "+bot.getRuntimeStatus().getAutoPickupItems());
            return;
        }

        BotLogger.info("🛒", true, bot.getId() + " Pickup parameters:: " + shouldPickup);

        BotScanNatural.logScanNatural(bot, BotConstants.DEFAULT_SCAN_RANGE);

        if (bot.getRuntimeStatus().getAutoPickupItems()) {
            pullAllItemsinRadius(2.0);
        }



        try {
            Location botLocation = BotWorldHelper.getWorldLocation(bot.getRuntimeStatus().getCurrentLocation());

            List<Entity> nearbyEntities = BotWorldHelper.getWorld().getEntities();

            for (Entity entity : nearbyEntities) {
                if (entity instanceof Item) {
                    Item item = (Item) entity;
                    if (botLocation.distance(item.getLocation()) < 2.0) {
                        Material material = item.getItemStack().getType();
                        int amount = item.getItemStack().getAmount();

                        addItem(material, amount); // Передаём два параметра в инвентарь

                        item.remove(); // Удаляем предмет с земли
                        BotLogger.info("🛒", true, bot.getId() + " Подобрал " + amount + " x " + material);
                    }
                }
            }

        } catch (Exception e) {
            BotLogger.info("🛒", true, bot.getId() + " " + e.getMessage());
        }

    }

    public static boolean hasEnoughBlocks(Bot bot, Set<Material> targetMaterials, int maxBlocksPerMaterial) {

        if (bot.getInventory().getNPCInventory() == null) {
            BotLogger.info("🛒", true, bot.getId() + " Has no inventory yet!");
            return true;
        } // not yet created

        Map<Material, Integer> collectedCounts = new HashMap<>();

        if (targetMaterials != null) {
            // Считаем количество каждого целевого материала в инвентаре
            for (ItemStack item : bot.getInventory().getNPCInventory().getContents()) {
                if (item != null && targetMaterials.contains(item.getType())) {
                    collectedCounts.put(item.getType(),
                            collectedCounts.getOrDefault(item.getType(), 0) + item.getAmount());
                }
            }
        } else {
            // Считаем количество каждого материала в инвентаре
            for (ItemStack item : bot.getInventory().getNPCInventory().getContents()) {
                if (item != null) {
                    collectedCounts.put(item.getType(),
                            collectedCounts.getOrDefault(item.getType(), 0) + item.getAmount());
                }
            }
        }
        if (targetMaterials != null) {
            // Проверяем, достигнуто ли нужное количество для любого из целевых материалов
            for (Material material : targetMaterials) {
                int count = collectedCounts.getOrDefault(material, 0);
                BotLogger.info("📦 ", true, 
                        bot.getId() + " | " + material + ": ( " + count + "/" + maxBlocksPerMaterial + ")");

                if (count >= maxBlocksPerMaterial) {
                    return true; // Достигнута цель по какому-то материалу → завершаем задачу
                }
            }
        }

        return false; // Ещё не набрали нужное количество
    }

    public static boolean hasFreeInventorySpace(Bot bot, Set<Material> targetMaterials) {

        if (bot.getInventory().getNPCInventory() == null) {
            BotLogger.info("🛒", true,  bot.getId() + " Has no inventory yet!");
            return true;
        } // not yet created

        for (ItemStack item : bot.getInventory().getNPCInventory().getContents()) {
            if (item == null || item.getType() == Material.AIR) {
                return true; // Есть свободный слот
            }
            if (targetMaterials != null) {
                // Проверяем, есть ли место в существующем стаке для любого из целевых
                // материалов
                if (targetMaterials.contains(item.getType()) && item.getAmount() < item.getMaxStackSize()) {
                    return true; // В этом слоте можно добавить ещё
                }
            } else {
                if (item.getAmount() < item.getMaxStackSize()) {
                    return true; // В этом слоте можно добавить ещё
                }
            }
        }
        return false; // Нет места ни в одном слоте
    }

    public static void dropAllItems(Bot bot) {

        Inventory inv = bot.getInventory().getNPCInventory();
        if (inv == null) {
            return;
        } // Если инвентарь недоступен, возвращаем 0

        Inventory inventory = bot.getInventory().getNPCInventory();
        if (inventory == null) {
            BotLogger.info("🛒 ", true,  bot.getId() + " Has no inventory yet!");
            return;
        } // not yet created

        for (ItemStack item : inventory.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                bot.getNPCEntity().getWorld().dropItemNaturally(bot.getNPCEntity().getLocation(), item);
            }
        }

        inventory.clear(); // Полностью очищаем инвентарь после выброса
        BotLogger.info("🚮", true, bot.getId() + " Выбросил все предметы из инвентаря!");
    }

    public void pullAllItemsinRadius(double radius) {
        List<Entity> nearbyItems = bot.getNPCEntity().getNearbyEntities(radius, radius, radius);

        for (Entity entity : nearbyItems) {
            if (entity instanceof Item) {
                entity.teleport(bot.getNPCEntity().getLocation()); // Притягиваем предмет
                BotLogger.info("🛒", true, bot.getId() + " Pulled up a near item!");
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
        // BotLogger.info("📝 " + bot.getId() + " Инвентарь:");
        // for (String r : rows) {
        // BotLogger.info(r);
        // }
    }

    public static int getTotalItemCount(Bot bot) {
        Inventory inv = bot.getInventory().getNPCInventory();
        if (inv == null)
            return 0;

        int total = 0;
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() != Material.AIR) {
                total += item.getAmount();
            }
        }
        return total;
    }

    public static boolean hasToolFor(Bot bot, Material blockType) {
        Inventory inv = bot.getInventory().getNPCInventory();
        if (inv == null)
            return false; // Если инвентаря нет – инструмента точно нет!

        Material requiredTool = getRequiredTool(blockType);
        if (requiredTool == null)
            return true; // Если инструмент не нужен – всё норм, можно ломать

        // Проверяем, есть ли нужный инструмент в инвентаре
        for (ItemStack item : inv.getContents()) {
            if (item != null && item.getType() == requiredTool) {
                return true; // Инструмент найден
            }
        }

        return false; // Инструмента нет
    }

    public static boolean equipRequiredTool(Bot bot, Material blockType) {
        Inventory inv = bot.getInventory().getNPCInventory();
        if (inv == null)
            return false;

        Material requiredTool = getRequiredTool(blockType);
        if (requiredTool == null)
            return true; // Инструмент не нужен

        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() == requiredTool) {
                bot.getPlayer().getInventory().setItemInMainHand(item);
                BotLogger.info("🤖 ", true, "Взял в руку инструмент: " + requiredTool);
                return true;
            }
        }

        BotLogger.info("🧰", true, "Инструмент " + requiredTool + " не найден в инвентаре");
        return false;
    }

    private static Material getRequiredTool(Material blockType) {
        return switch (blockType) {
            case STONE, COBBLESTONE, IRON_ORE, GOLD_ORE, DIAMOND_ORE, DEEPSLATE -> Material.WOODEN_PICKAXE; // ❗
                                                                                                            // Минимально
                                                                                                            // нужна
                                                                                                            // деревянная
                                                                                                            // кирка
            case OBSIDIAN -> Material.DIAMOND_PICKAXE; // ❗ Только алмазная кирка!
            default -> null; // ❗ Если `null`, значит, инструмент не нужен (можно ломать руками)
        };
    }

}
