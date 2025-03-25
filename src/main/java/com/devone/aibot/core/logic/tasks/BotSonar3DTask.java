package com.devone.aibot.core.logic.tasks;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.destruction.BotBreakTask;
import com.devone.aibot.utils.Bot3DBioScan;
import com.devone.aibot.utils.Bot3DGeoScan;
import com.devone.aibot.utils.Bot3DGeoScan.ScanMode;
import com.devone.aibot.utils.BotLogger;


public class BotSonar3DTask extends BotTask {

    private BotTask parent;
    private int radius;
    private int height;

    private ScanMode scanMode = ScanMode.FULL;

    public BotSonar3DTask(Bot bot, BotTask caller, int radius, int height) {
        super(bot, "ᯤ"); // ᯤ
        parent = caller;
        this.radius = radius;
        this.height = height;
        this.scanMode = Bot3DGeoScan.ScanMode.FULL;

        setObjective("Scan Signatures");
    }

    // Метод конфигурации для установки ScanMode
    @Override
    public BotTask configure(Object... params) {
    super.configure(params);

        if (params.length >= 1 && params[0] instanceof ScanMode) {
            scanMode = (ScanMode) params[0];  // Устанавливаем scanMode из параметров
        }

        BotLogger.debug(isLogging(),"⚙️ BotTaskSonar3D сконфигурирован с режимом сканирования: " + scanMode);
        return this;
    }


    @Override
    public void executeTask() {

        setObjective("Scanning Signatures");
        
        if (parent instanceof BotExploreTask || parent instanceof BotBreakTask) {
            Map<Location, Material> geo = Bot3DGeoScan.scan3D(bot, radius, height, scanMode);
            parent.setGeoMap(geo);
            BotLogger.debug(isLogging(),"🛰️ Geo scan complete. Blocks: " + geo.size());
        }
    
        if (parent instanceof BotHuntMobsTask) {
            List<LivingEntity> bio = Bot3DBioScan.scan3D(bot, radius);
            parent.setBioEntities(bio);
            BotLogger.debug(isLogging(),"🧬 Bio scan complete. Entities: " + bio.size());
        }
    
        isDone = true;
    }

}