package com.devone.aibot.core.logic.tasks;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.destruction.BotTaskBreakBlock;
import com.devone.aibot.utils.BotBio3DScan;
import com.devone.aibot.utils.BotGeo3DScan;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;
import com.devone.aibot.utils.BotLogger;


public class BotTaskSonar3D extends BotTask {

    private BotTask parent;
    private int radius;
    private int height;

    private ScanMode scanMode = ScanMode.FULL;

    public BotTaskSonar3D(Bot bot, BotTask caller, int radius, int height) {
        super(bot, "ᯤ"); // ᯤ
        parent = caller;
        this.radius = radius;
        this.height = height;
        this.scanMode = BotGeo3DScan.ScanMode.FULL;

        setObjective("Scan Signatures");
    }

    // Метод конфигурации для установки ScanMode
    @Override
    public BotTask configure(Object... params) {
    super.configure(params);

        if (params.length >= 1 && params[0] instanceof ScanMode) {
            scanMode = (ScanMode) params[0];  // Устанавливаем scanMode из параметров
        }

        BotLogger.debug("⚙️ BotTaskSonar3D сконфигурирован с режимом сканирования: " + scanMode);
        return this;
    }


    @Override
    public void executeTask() {

        setObjective("Scanning Signatures");
        
        if (parent instanceof BotTaskExplore || parent instanceof BotTaskBreakBlock) {
            Map<Location, Material> geo = BotGeo3DScan.scan3D(bot, radius, height, scanMode);
            parent.setGeoMap(geo);
            BotLogger.debug("🛰️ Geo scan complete. Blocks: " + geo.size());
        }
    
        if (parent instanceof BotTaskHuntMobs) {
            List<LivingEntity> bio = BotBio3DScan.scan3D(bot, radius);
            parent.setBioEntities(bio);
            BotLogger.debug("🧬 Bio scan complete. Entities: " + bio.size());
        }
    
        isDone = true;
    }

}