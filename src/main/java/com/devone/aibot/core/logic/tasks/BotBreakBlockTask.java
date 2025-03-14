package com.devone.aibot.core.logic.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.ZoneManager;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.AIBotPlugin;

public class BotBreakBlockTask implements BotTask {
    private final Bot bot;
    private Location targetLocation;
    private boolean isDone = false;
    private long startTime = System.currentTimeMillis();

    private String name = "BREAK_BLOCK";

    public BotBreakBlockTask(Bot bot) {
        this.bot = bot;
    }

    @Override
    public void configure(Object... params) {
        if (params.length == 1 && params[0] instanceof Location) {
            this.targetLocation = (Location) params[0];
            
            isDone = false;
            BotLogger.debug("🔨 BreakBlockTask сконфигурирована: " + formatLocation(targetLocation));
        } else {
            BotLogger.debug("❌ Ошибка конфигурации BreakBlockTask: неверные параметры");
        }
    }

    @Override
    public void update() {
        if (isDone || targetLocation == null) return;

        // Проверяем, находится ли блок в запретной зоне
        if (ZoneManager.getInstance().isInProtectedZone(targetLocation)) {
            BotLogger.debug("⛔ Бот " + bot.getId() + " в запретной зоне, НЕ разрушает блок " + formatLocation(targetLocation));
            isDone = true;
            return;
        }

        // Выполняем разрушающую операцию в главном потоке
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            Block block = targetLocation.getBlock();
            Material type = block.getType();

            if (type != Material.AIR) {
                block.breakNaturally(); // ✅ Теперь ломаем блок в главном потоке
                BotLogger.debug("💥 Бот " + bot.getId() + " разрушил блок " + type + " на " + formatLocation(targetLocation));
            }

            isDone = true; // ✅ Завершаем задачу после разрушения
        });
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void setPaused(boolean paused) {
        // Ничего не делаем, так как эта задача мгновенная
    }

    private String formatLocation(Location loc) {
        return "(" + loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ() + ")";
    }

    @Override
    public String getName() {
        return name;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    @Override
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
}
