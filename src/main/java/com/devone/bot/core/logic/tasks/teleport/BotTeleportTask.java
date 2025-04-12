package com.devone.bot.core.logic.tasks.teleport;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.core.logic.tasks.teleport.config.BotTeleportTaskConfig;
import com.devone.bot.core.logic.tasks.teleport.params.BotTeleportTaskParams;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

public class BotTeleportTask extends BotTask {
    private BotCoordinate3D target;

    public BotTeleportTask(Bot bot, Player player) {
        super(bot, player, "🗲");

        config = new BotTeleportTaskConfig();
        this.isLogged = config.isLogged();
        
        setObjective("Teleport");
    }

    @Override
    public BotTeleportTask configure(IBotTaskParams params) {

        super.configure((BotTaskParams) params);
        
        if (params instanceof BotTeleportTaskParams) {
            BotTeleportTaskParams teleportParams = (BotTeleportTaskParams) params;
            BotCoordinate3D target = teleportParams.getTarget();

            if (target != null) {
                bot.getRuntimeStatus().setTargetLocation(target);
            } else {
                BotLogger.info(this.isLogged(), bot.getId() + " ❌ Некорректные параметры для `BotTeleportTask`!");
                this.stop();
            }
        } else {
            BotLogger.info(this.isLogged(), bot.getId() + " ❌ Некорректные параметры для `BotTeleportTask`!");
            this.stop();
        }
        return this;
    }

    @Override
    public void execute() {
        setObjective("Teleporting...");

        if (this.target == null) {
            BotLogger.warn(isLogged(), bot.getId() + " ❌ Целевая точка телепортации не задана.");
            stop();
            return;
        }

        BotCoordinate3D targetLocation = bot.getRuntimeStatus().getTargetLocation();
        // Телепортация в основном потоке
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            bot.getNPCEntity().teleport(BotWorldHelper.getWorldLocation(targetLocation));
            bot.getRuntimeStatus().setStuck(false);
            BotLogger.info(isLogged(), bot.getId() + " 🗲 Телепорт с " + bot.getNPCEntity().getLocation().toVector() + " → " + targetLocation);
        });

       stop();

    }

    public void stop() {
        isDone = true;
    }

}
