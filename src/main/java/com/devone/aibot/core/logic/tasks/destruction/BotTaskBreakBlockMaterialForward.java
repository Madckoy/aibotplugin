package com.devone.aibot.core.logic.tasks.destruction;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotGeo3DScan.ScanMode;
import org.bukkit.Material;

import java.util.Set;

public class BotTaskBreakBlockMaterialForward extends BotTaskBreakBlock {

    public BotTaskBreakBlockMaterialForward(Bot bot, Set<Material> targetMaterials) {
        super(bot);
        setName("⛏");
        setTargetMaterials(targetMaterials);

        // Устанавливаем направление сканирования: вперёд по взгляду бота
        setScanMode(ScanMode.FORWARD_HEMISPHERE);

        // Опционально можно указать "вперед" как текущую точку, но это не обязательно
        bot.getRuntimeStatus().setTargetLocation(bot.getRuntimeStatus().getCurrentLocation());
    }

    @Override
    public void executeTask() {
        super.executeTask();
    }
}
