package com.devone.bot.core.bot.brain.memory;

import com.devone.bot.core.bot.brain.BotBrain;
import com.devone.bot.core.bot.brain.memory.scene.BotSceneData;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotLocation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ArrayList;
import java.util.EnumMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

public class BotMemory {
    
    protected transient BotSceneData sceneData;

    private ArrayList<String> killedMobs;
    private ArrayList<String> brokenBlocks;
    private long teleportUsed;
    private transient BotBrain brain;

    // Карта для хранения типов памяти, каждый тип имеет свой набор посещённых мест
    private final Map<MemoryType, Map<BotLocation, BotMemoryItem>> memoryMap;

    public BotMemory(BotBrain brain) {
        this.brain = brain;
        this.sceneData = null;
        this.killedMobs   = new ArrayList<String>();
        this.brokenBlocks = new ArrayList<String>();
        this.teleportUsed = 0;

        memoryMap = new EnumMap<>(MemoryType.class); // Используем EnumMap для лучшей производительности при работе с перечислениями

        for (MemoryType type : MemoryType.values()) {
            memoryMap.put(type, new HashMap<>());
        }
    }

    public void killedMobsIncrease(String mobName) {
        this.killedMobs.add(mobName);
    }

    public void brokenBlocksIncrease(String blockName) {
        
        this.brokenBlocks.add(blockName);
    }

    public ArrayList<String> getMobsKilled() {
        return killedMobs;
    }

    public ArrayList<String> getBlocksBroken() {
        return brokenBlocks;
    }

    public void teleportUsedIncrease() {
        this.teleportUsed = teleportUsed + 1;
    }

    public long getTeleportUsed() {
        return teleportUsed;
    }

    public void setSceneData(BotSceneData sceneData) {
        this.sceneData = sceneData;
    }

    public BotSceneData getSceneData() {
        return this.sceneData;
    }

    // Запоминаем блок для конкретного типа памяти
    public void memorize(BotBlockData block, MemoryType memoryType) {
        if(block==null) return;
        BotMemoryItem item = new BotMemoryItem(block);
        memoryMap.get(memoryType).put(block.getLocation(), item);
    }

    // Проверка, был ли блок запомнен для определённого типа памяти
    public boolean isMemorized(BotBlockData block, MemoryType memoryType) {
        boolean res = false;

        if(block!=null) {

            Map<BotLocation, BotMemoryItem> map = memoryMap.get(memoryType);

            if(map!=null) {
                BotMemoryItem m_item = map.get(block.getLocation());
                if(m_item != null) {
                    return true;
                }  
            }
        }
        return res;
    }

    // Очистка устаревших записей по всем типам памяти
    public long cleanup(long expirationMillis) {
        long removed = 0;
        long currentTime = System.currentTimeMillis();

        // Перебираем все типы памяти
        for (Map<BotLocation, BotMemoryItem> memory : memoryMap.values()) {
            Iterator<Map.Entry<BotLocation, BotMemoryItem>> it = memory.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<BotLocation, BotMemoryItem> entry = it.next();
                BotMemoryItem item = entry.getValue();
                
                // Если элемент устарел, удаляем его
                if (item.isExpired(expirationMillis, currentTime)) {
                    it.remove();
                    removed++;
                }
            }
        }

        return removed;
    }

    public void cleanup() {
 
        memoryMap.clear();

    }
    
    // Получить все данные для конкретного типа памяти
    public Map<BotLocation, BotMemoryItem> getMemoryForType(MemoryType memoryType) {
        return memoryMap.get(memoryType);
    }

    public JsonObject toJson() {
        JsonObject memoryJson = new JsonObject();

        // Убитые мобы
        JsonArray mobsArray = new JsonArray();
        for (String mob : killedMobs) {
            mobsArray.add(mob);
        }
        memoryJson.add("killedMobs", mobsArray);

        // Разрушенные блоки
        JsonArray blocksArray = new JsonArray();
        for (String block : brokenBlocks) {
            blocksArray.add(block);
        }
        memoryJson.add("brokenBlocks", blocksArray);

        // Количество телепортов
        memoryJson.addProperty("teleportUsed", teleportUsed);

        // Размеры памяти по типам
        JsonObject types = new JsonObject();
        for (MemoryType type : memoryMap.keySet()) {
            types.addProperty(type.name(), memoryMap.get(type).size());
        }
        memoryJson.add("memoryTypes", types);

        return memoryJson;
    }

}
