package com.devone.bot.core.logic.task.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.core.logic.task.teleport.params.BotTeleportTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

public class BotTeleportTask extends BotTask {
    private BotCoordinate3D target;
    private BotTeleportTaskParams params = new BotTeleportTaskParams();

    public BotTeleportTask(Bot bot, Player player) {
        super(bot, player);
        setIcon(params.getIcon());  
        setObjective(params.getObjective());
    }

    @Override
    public BotTeleportTask configure(IBotTaskParams params) {

        super.configure((BotTaskParams) params);
    

        this.params.copyFrom(params);

        BotCoordinate3D tpTarget = ((BotTeleportTaskParams)params).getTarget();

        if (tpTarget != null) {
            target = tpTarget;
        } else {
            BotLogger.info("❌", this.isLogging(), bot.getId() + "Некорректные параметры для `BotTeleportTask`!");
            this.stop();
        }
    
        return this;
    }

    @Override
    public void execute() {

        setObjective(params.getObjective() + " to: " + target);
    
        if (this.target == null) {
            BotLogger.info("❌", this.isLogging(), bot.getId() + "Целевая точка телепортации не задана.");
            stop();
            return;
        }
    
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            Location baseLocation = BotWorldHelper.getWorldLocation(target);
            
            // 💡 Добавим небольшое смещение по X и Z, чтобы не встать "внутрь" сущности
            Location safeOffset = baseLocation.clone().add(0.5, 0, 0.5);
    
            bot.getNPCEntity().teleport(safeOffset);
            bot.getRuntimeStatus().setStuck(false);
            bot.getRuntimeStatus().teleportUsedIncrease();
    
            BotLogger.info("⚡", this.isLogging(), bot.getId() + "Телепорт с " + baseLocation.toVector() + " → " + safeOffset.toVector());
        });
    
        stop();
    }

}
