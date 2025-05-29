package com.devone.bot.core.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.move.BotMoveTask;
import com.devone.bot.core.task.active.move.params.BotMoveTaskParams;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.passive.BotTaskManager;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
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
        if (location == null || location.getWorld() == null)
            return;

        Material blockType = location.getBlock().getType();

        // ✅ Проверяем, что блок не AIR (иначе эффект не сработает)
        if (blockType == Material.AIR) {
            BotLogger.debug(task.getIcon(), task.isLogged(),
                    bot.getId() + " ⚠️ Эффект разрушения отменён: блок уже AIR " + location.toString());
            return;
        }

        location.getWorld().spawnParticle(
                org.bukkit.Particle.BLOCK_CRACK,
                location.clone().add(0.0, 0.0, 0.0), // Центр блока
                20, // Кол-во частиц
                0.25, 0.25, 0.25, // Разброс
                location.getBlock().getBlockData() // Тип блока для эффекта
        );

        BotPosition pos = BotWorldHelper.locationToBotPosition(location);
        BotLogger.debug(task.getIcon(), task.isLogged(), bot.getId() + " 🎇 Эффект разрушения воспроизведён на " + pos);
    }

    public static boolean requiresTool(Material blockType) {
        return switch (blockType) {
            case IRON_ORE, GOLD_ORE, DIAMOND_ORE, DEEPSLATE, OBSIDIAN -> true; // ❗ Только эти блоки требуют инструмент
            default -> false; // Всё остальное можно ломать руками
        };
    }

    public static Location getFallbackLocation() {
        World world = BotWorldHelper.getWorld();
        return world.getSpawnLocation();
    }

    public static BotPosition getFallbackPosition() {
        BotPosition coord = new BotPosition(getFallbackLocation().getBlockX(), getFallbackLocation().getBlockY(),
                getFallbackLocation().getBlockZ());
        return coord;
    }

    /**
     * Поворачивает бота лицом к целевой точке, используя teleport с сохранением
     * координат.
     * Подходит для обхода запрета на setRotation().
     *
     * @param bot    Бот (CraftPlayer или NPC, поддерживающий teleport)
     * @param target Цель, к которой нужно повернуть лицо
     */
    private static void lookAt(Bot bot, BotPosition target) {
        if (bot.getNPCEntity() == null) return;

        Location from = bot.getNPCEntity().getLocation();
        Location to = BotWorldHelper.botPositionToWorldLocation(target).clone().add(0.5, 0.5, 0.5); // Центр блока

        // Если доступен метод faceLocation:
        bot.getNPC().faceLocation(to);

        // Или fallback: телепорт с новым поворотом

        Vector dir = to.toVector().subtract(from.toVector());
        float yaw = (float) Math.toDegrees(Math.atan2(dir.getZ(), dir.getX())) - 90f;

        // float pitch = (float) Math.toDegrees(-Math.atan2(dir.getY(), Math.sqrt(dir.getX() * dir.getX() + dir.getZ() * dir.getZ())));

        /*
        Location newLoc = from.clone();
        newLoc.setYaw(yaw);
        newLoc.setPitch(pitch);
        bot.getNPCEntity().teleport(newLoc);
        */

        bot.getBrain().notifyYawChanged(yaw);
    }


    public static String formatTime(long milliseconds) {
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds);
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

        BotLogger.debug("📦", AIBotPlugin.getInstance().isLogged(), context + " — Использовано памяти: " + usedMB + " MB / " + maxMB + " MB");
    }

    public static long getRemainingTime(long start, long timeout) {
        long elapsed = System.currentTimeMillis() - start;
        long diff = (timeout - elapsed) / 1000;
        return diff;
    }

    public static void turnToTarget(BotTask<?> task, Bot bot, BotPosition target) {

        // ✅ Принудительно обновляем положение, если поворот сбрасывается
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {

            BotLogger.debug(task.getIcon(), task.isLogged(),
                    bot.getId() + " Поворачивает голову в сторону: " + target);

            BotUtils.lookAt(bot, target);

        }, 1L); // ✅ Через тик, чтобы дать время на обновление
    }

    public static void animateHand(BotTask<?> task, Bot bot) {
        if (bot.getNPCEntity() instanceof Player playerBot) {
            playerBot.swingMainHand();
            BotLogger.debug(task.getIcon(), task.isLogged(), bot.getId() + " 👋🏻 Анимация руки выполнена");
        } else {
            BotLogger.debug(task.getIcon(), task.isLogged(), bot.getId() + " 🖐🏻 Анимация не выполнена: бот — не игрок");
        }
    }

    // под вопросом, стоит ли перенести в BotUtils или в BotInventory
    public void checkAndSelfMove(Bot bot, Location target) {
        double pickupRadius = 2.0; // Радиус, в котором проверяем предметы
        List<Entity> nearbyItems = bot.getNPCEntity().getNearbyEntities(pickupRadius, pickupRadius, pickupRadius);

        // Если есть дроп в радиусе 2 блоков — бот остается на месте
        if (!nearbyItems.isEmpty()) {
            BotLogger.debug("🤖", bot.isLogged(),
                    bot.getId() + " 🔍 В радиусе " + pickupRadius + " блоков от есть предметы, остаюсь на месте.");
            return;
        }

        // Если предметов рядом нет, двигаем бота к последнему разрушенному блоку
        BotPosition pos = BotWorldHelper.locationToBotPosition(target);
        BotLogger.debug("🤖", bot.isLogged(), bot.getId() + " 📦 Дроп подобран. Двигается к цели:" + pos);

        BotMoveTask mv_task = new BotMoveTask(bot);
        BotMoveTaskParams mv_taskParams = new BotMoveTaskParams(pos);
        mv_task.setParams(mv_taskParams);

        BotTaskManager.push(bot, mv_task);
    }

    public static String getActiveTaskIcon(Bot bot) {
        String icon = "🤖";
        try {
            BotTask<?> task = bot.getActiveTask();
            icon = task.getIcon();
        } catch (Exception ex) {
        }
        return icon;
    }

    public static float getBotYaw__(Bot bot) {

        Location botLocation = bot.getNPC().getStoredLocation();
        float botYaw = botLocation.getYaw();

        return botYaw;
    }
    
    public static float getBotPitch__(Bot bot) {

        Location botLocation = bot.getNPC().getStoredLocation();
        float botPitch = botLocation.getPitch();

        return botPitch;
    }

    public static String getObjective(Bot bot) {
        try {
            return bot.getActiveTask().getObjective();
        } catch (Exception ex) {
            return "";
        }
    }

    public static boolean isTaskReactive(Bot bot) {
        try {
            return bot.getActiveTask().isReactive();
        } catch (Exception ex) {
            return false;
        }
    }

    public static String getActiveTaskSimpleName(Bot bot) {
        try {
            return bot.getActiveTask().getClass().getSimpleName();
        } catch (Exception ex) {
            return "N/A";
        }
    }

    public static long getActiveTaskElapsed(Bot bot) {
        try {
            return bot.getActiveTask().getElapsedTime();
        } catch (Exception ex) {
            return 0;
        }
    }

    public static void rotate(BotTask<?> task, Bot bot, float degrees) {
        if (bot == null || bot.getNavigator().getPosition() == null || bot.getNPCEntity() == null) return;

        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {

            float currentYaw = bot.getNavigator().getPosition().getYaw();
            float newYaw = (currentYaw + degrees) % 360.0f;

            // Без смещения по координатам
            Location loc = bot.getNPCEntity().getLocation();
            loc.setYaw(newYaw);
            loc.setPitch(0);

            bot.getNPCEntity().teleport(loc); // или setRotation(newYaw, loc.getPitch());

            bot.getBrain().notifyYawChanged(newYaw);

            BotLogger.debug("↻", bot.isLogged(), bot.getId() + " rotated " + degrees + "° → yaw=" + newYaw);

        }, 1L);
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static <T> T readJson(File file, Class<T> clazz) {
        try (Reader reader = new FileReader(file)) {
            return gson.fromJson(reader, clazz);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read JSON from " + file.getName(), e);
        }
    }

    public static void writeJson(File file, Object obj) {
        try (FileWriter writer = new FileWriter(file)) {
            gson.toJson(obj, writer);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write JSON to " + file.getName(), e);
        }
    }

    public static void swingMainHand(Bot bot) {
        Player player = bot.getPlayer();
        if (player == null || !player.isOnline()) return;

        // Отправляем анимацию руки игрока (бота)
        player.swingMainHand();
    }

}