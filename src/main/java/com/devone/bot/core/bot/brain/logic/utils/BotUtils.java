package com.devone.bot.core.bot.brain.logic.utils;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;
import com.devone.bot.core.bot.brain.logic.utils.world.BotWorldHelper;

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

    public static void playBlockBreakEffect(Location location) {
        if (location == null || location.getWorld() == null) return;
    
        Material blockType = location.getBlock().getType();
    
        // ✅ Проверяем, что блок не AIR (иначе эффект не сработает)
        if (blockType == Material.AIR) {
            BotLogger.debug("⚠️", true, "Эффект разрушения отменён: блок уже AIR " + location.toString());
            return;
        }
    
        location.getWorld().spawnParticle(
            org.bukkit.Particle.BLOCK_CRACK,
            location.clone().add(0.5, 0.5, 0.5), // Центр блока
            20, // Кол-во частиц
            0.25, 0.25, 0.25, // Разброс
            location.getBlock().getBlockData() // Тип блока для эффекта
        );
    
        BotLogger.debug("🎇", true, "Эффект разрушения воспроизведён на " + location.toString());
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

    public static BotLocation getFallbackCoordinate3D() {
        BotLocation coord = new BotLocation(getFallbackPos().getBlockX(), getFallbackPos().getBlockY(), getFallbackPos().getBlockZ());
        return coord;
    }

    public static boolean isBreakableBlock(Block block) {
        if (block == null ) return false;
    
        Material type = block.getType();
    
        return switch (type) {
            case AIR, CAVE_AIR, VOID_AIR,
                 BEDROCK, BARRIER,
                 END_PORTAL, END_PORTAL_FRAME,
                 STRUCTURE_BLOCK, STRUCTURE_VOID,
                 COMMAND_BLOCK, CHAIN_COMMAND_BLOCK, REPEATING_COMMAND_BLOCK -> false;
            default -> true;
        };
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
    
}