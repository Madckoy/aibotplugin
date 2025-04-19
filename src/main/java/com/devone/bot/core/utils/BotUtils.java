package com.devone.bot.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.passive.BotTask;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BotUtils {

    public static String getBlockName(Block bl) {

        String text = bl.toString();

        String result = "";
        // Regular expression to capture the value of type
        String pattern = "type=([A-Za-z0-9_]+)";

        // Create a Pattern object
        Pattern r = Pattern.compile(pattern);

        // Create a matcher object
        Matcher m = r.matcher(text);

        // Check if the pattern is found
        if (m.find()) {
            result = m.group(1);
        }

        return result; 
    }

    public static void playBlockBreakEffect(BotTask<?> task, Bot bot, Location location) {
        if (location == null || location.getWorld() == null) return;
    
        Material blockType = location.getBlock().getType();
    
        // ✅ Проверяем, что блок не AIR (иначе эффект не сработает)
        if (blockType == Material.AIR) {
            BotLogger.debug(task.getIcon(), true, bot.getId()+" ⚠️ Эффект разрушения отменён: блок уже AIR " + location.toString());
            return;
        }
    
        location.getWorld().spawnParticle(
            org.bukkit.Particle.BLOCK_CRACK,
            location.clone().add(0.5, 0.5, 0.5), // Центр блока
            20, // Кол-во частиц
            0.25, 0.25, 0.25, // Разброс
            location.getBlock().getBlockData() // Тип блока для эффекта
        );
    
        BotLocation loc = BotWorldHelper.worldLocationToBotLocation(location);
        BotLogger.debug(task.getIcon(), true, bot.getId()+" 🎇 Эффект разрушения воспроизведён на " + loc);
    }

    public static boolean requiresTool(Material blockType) {
        return switch (blockType) {
            case IRON_ORE, GOLD_ORE, DIAMOND_ORE, DEEPSLATE, OBSIDIAN -> true; // ❗ Только эти блоки требуют инструмент
            default -> false; // Всё остальное можно ломать руками
        };
    }
    
    private static Location getFallbackPos() {
        World world = BotWorldHelper.getWorld();
        return world.getSpawnLocation();
    }

    public static BotLocation getFallbackLocation() {
        BotLocation coord = new BotLocation(getFallbackPos().getBlockX(), getFallbackPos().getBlockY(), getFallbackPos().getBlockZ());
        return coord;
    }
    
/**
     * Поворачивает бота лицом к целевой точке, используя teleport с сохранением координат.
     * Подходит для обхода запрета на setRotation().
     *
     * @param bot     Бот (CraftPlayer или NPC, поддерживающий teleport)
     * @param target  Цель, к которой нужно повернуть лицо
     */
    public static void lookAt(Bot bot, BotLocation target) {

        Location tgt = BotWorldHelper.getWorldLocation(target);

        Location from = bot.getNPCEntity().getLocation();
        Location to = tgt.clone().add(0.5, 0.5, 0.5); // центр блока

        Vector direction = to.toVector().subtract(from.toVector());

        float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
        float pitch = (float) Math.toDegrees(-Math.atan2(direction.getY(),
                Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ())));

        Location newLook = from.clone();
        newLook.setYaw(yaw);
        newLook.setPitch(pitch);

        bot.getNPCEntity().teleport(newLook);
    }

    public static String formatTime(long milliseconds) {
        long hours   = TimeUnit.MILLISECONDS.toHours(milliseconds);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }

    public static void logMemoryUsage(String context) {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = runtime.totalMemory() - runtime.freeMemory();
        long maxMemory = runtime.maxMemory();
    
        String usedMB = String.format("%.2f", usedMemory / 1024.0 / 1024.0);
        String maxMB = String.format("%.2f", maxMemory / 1024.0 / 1024.0);
    
        BotLogger.debug("📦", true, context + " — Использовано памяти: " + usedMB + " MB / " + maxMB + " MB");
    }

    public static long getRemainingTime(long start) {
        long elapsed = System.currentTimeMillis() - start;
        long diff = (BotConstants.DEFAULT_TASK_TIMEOUT - elapsed)/1000;
        return diff;
    }
    
    public static void clearTasks(Bot bot) {
        bot.getLifeCycle().getTaskStackManager().clearTasks();
    }

    public static void pushTask(Bot bot, BotTask<?> task) {
        if (bot == null || task == null) return;
        bot.getLifeCycle().getTaskStackManager().pushTask(task);
    }

    public static void turnToTarget(BotTask<?> task, Bot bot, BotLocation target) {
        
        // ✅ Принудительно обновляем положение, если поворот сбрасывается
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {
            BotUtils.lookAt(bot, target);
        }, 1L); // ✅ Через тик, чтобы дать время на обновление
    
        BotLogger.debug(task.getIcon(), true, bot.getId() + " 🔄 Turned to look at the target: " + target);
    }

    public static void animateHand(BotTask<?> task, Bot bot) {
        if (bot.getNPCEntity() instanceof Player playerBot) {
            playerBot.swingMainHand();
            BotLogger.debug(task.getIcon(), true,bot.getId() + " ✋🏻 Анимация руки выполнена");
        } else {
            BotLogger.debug(task.getIcon(), true, bot.getId() +" ✋🏻 Анимация не выполнена: бот — не игрок");
        }
    }

}