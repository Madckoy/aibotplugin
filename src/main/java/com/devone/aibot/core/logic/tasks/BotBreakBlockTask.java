package com.devone.aibot.core.logic.tasks;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.ZoneManager;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotUtils;
import com.devone.aibot.utils.MaterialDetector;
import com.devone.aibot.AIBotPlugin;

public class BotBreakBlockTask implements BotTask {

    private final Bot bot;
        
    private Location targetLocation;

    private long startTime = System.currentTimeMillis();

    private String name = "BREAK";

    private MaterialDetector materialDetector;

    private int blocksMined = 0;
    private int maxBlocks;
    private int searchRadius; // Радиус поиска блоков
    private int breakProgress = 0;
    private Material mat = Material.DIRT;
    
    private boolean isDone;
    
    private static final Map<Material, Integer> BREAK_TIME_PER_BLOCK = new HashMap<>();
    
    static {
        BREAK_TIME_PER_BLOCK.put(Material.DIRT, 5);  // 5 тиков (0.25 сек)
        BREAK_TIME_PER_BLOCK.put(Material.STONE, 30); // Дольше ломать (1.5 сек)
            //BREAK_TIME_PER_BLOCK.put(Material.LOG, 15);   // Дерево (0.75 сек)
    }
    
    public BotBreakBlockTask(Bot bot) {
        this.bot = bot;
    }
    
    @Override
    public void configure(Object... params) {
        if (params.length >= 1 && params[0] instanceof Material) {
            mat = (Material) params[0];
        }
            
        if (params.length >= 2 && params[1] instanceof Integer) {
            this.searchRadius = (Integer) params[1];
        }
            
        if (params.length >= 3 && params[2] instanceof Integer) {
            this.maxBlocks = (Integer) params[2];
        }        
    
        materialDetector = new MaterialDetector(searchRadius); // Используем новый улучшенный поиск материалов
        BotLogger.debug("🔨 BreakBlockTask сконфигурирована: " + BotUtils.formatLocation(targetLocation));
    }
    
    @Override
    public void update() {
        if (isDone()) return;
    
        if (targetLocation==null) {
            // Ищем случайный блок
            targetLocation = materialDetector.findClosestMaterial(mat, bot.getNPCCurrentLocation());
            if (targetLocation == null) {
                BotLogger.info(bot.getId() + " ❌ Нет доступных блоков для добычи!");
                isDone = true;
                return;
            }
        }

        if(targetLocation!=null) {
            // Проверяем, находится ли блок в запретной зоне
            if (ZoneManager.getInstance().isInProtectedZone(targetLocation)) {
                BotLogger.debug("⛔ Бот " + bot.getId() + " в запретной зоне, НЕ разрушает блок " + BotUtils.formatLocation(targetLocation));
                isDone = true;
                return;
            }
        }

        // Выполняем разрушающую операцию в главном потоке
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            if (targetLocation != null) {
                Material blockType = targetLocation.getBlock().getType();
                int breakTime = BREAK_TIME_PER_BLOCK.getOrDefault(blockType, 10); // По умолчанию 10 тиков (0.5 сек)
                // Эмулируем процесс ломания
                if (breakProgress < breakTime) {
                breakProgress++;
                BotLogger.info(bot.getId() + " ⏳ Ломаем " + blockType + " [" + breakProgress + "/" + breakTime + "]");
                    return;
                }
        
                targetLocation.getBlock().setType(Material.AIR); // Копаем блок
                blocksMined++;
                breakProgress = 0; // Сбрасываем прогресс ломания
                BotLogger.debug("💥 Бот " + bot.getId() + " разрушил блок " + blockType + " на " + BotUtils.formatLocation(targetLocation));
                targetLocation = null;
            }
    
            if (blocksMined >= maxBlocks) {
                isDone = true;   
            }
        });
    }

    @Override
    public boolean isDone() {
        return isDone;
    }

    @Override
    public void setPaused(boolean paused) { 
        BotLogger.debug("⏸ Пауза не поддерживается для " + name);
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
