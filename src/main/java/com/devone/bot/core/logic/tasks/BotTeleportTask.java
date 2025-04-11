package com.devone.bot.core.logic.tasks;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.configs.BotTeleportTaskConfig;
import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.core.logic.tasks.params.BotTeleportTaskParams;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.utils.BotCoordinate3D;
import com.devone.bot.utils.BotLogger;
import com.devone.bot.utils.BotWorldHelper;

public class BotTeleportTask extends BotTask {

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
            BotCoordinate3D loc = teleportParams.getTarget();

            if (loc != null) {
                bot.getRuntimeStatus().setTargetLocation(loc);
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
        setObjective("Teleporting");

        BotCoordinate3D targetLocation = bot.getRuntimeStatus().getTargetLocation();
                // Телепортация в основном потоке
                Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
                    bot.getNPCEntity().teleport(BotWorldHelper.getWorldLocation(targetLocation));
                    BotLogger.info(this.isLogged(), "🗲 " + bot.getId() + " Телепортируемся в " + targetLocation);
                });

       stop();

    }

    public void stop() {
        isDone = true;
    }

}
