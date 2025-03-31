package com.devone.aibot.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.util.Vector;

import com.devone.aibot.core.Bot;

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
            BotLogger.info(true, "⚠️ Эффект разрушения отменён: блок уже AIR " + BotStringUtils.formatLocation(location));
            return;
        }
    
        location.getWorld().spawnParticle(
            org.bukkit.Particle.BLOCK_CRACK,
            location.clone().add(0.5, 0.5, 0.5), // Центр блока
            20, // Кол-во частиц
            0.25, 0.25, 0.25, // Разброс
            location.getBlock().getBlockData() // Тип блока для эффекта
        );
    
        BotLogger.info(true, "🎇 Эффект разрушения воспроизведён на " + BotStringUtils.formatLocation(location));
    }

    public static boolean requiresTool(Material blockType) {
        return switch (blockType) {
            case IRON_ORE, GOLD_ORE, DIAMOND_ORE, DEEPSLATE, OBSIDIAN -> true; // ❗ Только эти блоки требуют инструмент
            default -> false; // Всё остальное можно ломать руками
        };
    }
    
    public static Location getFallbackLocation() {
        World world = Bukkit.getWorlds().get(0);
        return world.getSpawnLocation();
    }

    public static boolean isBreakableBlock(Location location) {
        if (location == null || location.getWorld() == null) return false;
    
        Material type = location.getBlock().getType();
    
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
    public static void lookAt(Bot bot, Location target) {
        Location from = bot.getNPCEntity().getLocation();
        Location to = target.clone().add(0.5, 0.5, 0.5); // центр блока

        Vector direction = to.toVector().subtract(from.toVector());

        float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));
        float pitch = (float) Math.toDegrees(-Math.atan2(direction.getY(),
                Math.sqrt(direction.getX() * direction.getX() + direction.getZ() * direction.getZ())));

        Location newLook = from.clone();
        newLook.setYaw(yaw);
        newLook.setPitch(pitch);

        bot.getNPCEntity().teleport(newLook);
    }

}