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
import com.devone.aibot.utils.BotLogger;


public class BotSonar3DTask extends BotTask {

    private BotTask parent;
    private int radius;
    private int height;

    public BotSonar3DTask(Bot bot, BotTask caller, int radius, int height) {
        super(bot, "𖣠"); // ᯤ
        parent = caller;
        this.radius = radius;
        this.height = height;

        setObjective("Scan Signatures");
    }

    // Метод конфигурации для установки ScanMode
    @Override
    public BotTask configure(Object... params) {
        super.configure(params);
        return this;
    }


    @Override
    public void execute() {

        setObjective("Scanning Signatures");
        
        if (parent instanceof BotExploreTask || parent instanceof BotBreakTask) {
            Map<Location, Material> geo = Bot3DGeoScan.scan3D(bot, radius, height);
            parent.setGeoMap(geo);
            BotLogger.info(this.isLogged(),"🛰️ Geo scan complete. Blocks: " + geo.size());
        }
    
        if (parent instanceof BotHuntMobsTask) {
            List<LivingEntity> bio = Bot3DBioScan.scan3D(bot, radius);
            parent.setBioEntities(bio);
            BotLogger.info(this.isLogged(),"🧬 Bio scan complete. Entities: " + bio.size());
        }
    
        this.stop();
    }

    @Override
    public void stop() {
        this.isDone = true;
    }

}