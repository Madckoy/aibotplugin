package com.devone.bot.core.logic.tasks;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.configs.BotTeleportTaskConfig;
import com.devone.bot.utils.BotCoordinate3D;
import com.devone.bot.utils.BotLogger;
import com.devone.bot.utils.BotStringUtils;
import com.devone.bot.utils.BotWorldHelper;

public class BotTeleportTask extends BotTask {

    public BotTeleportTask(Bot bot, Player player) {
        super(bot, player, "🗲");

        config = new BotTeleportTaskConfig();
        this.isLogged = config.isLogged();
        
        setObjective("Teleport");
    }

     public BotTask configure(Object... params) {
        super.configure(params);

        if (params.length >= 1 && params[0] instanceof BotCoordinate3D) {
            BotCoordinate3D loc  = (BotCoordinate3D) params[0];
            bot.getRuntimeStatus().setTargetLocation(loc);
        }

        BotLogger.info(this.isLogged(), "⚙️ BotTaskTeleport is configured: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));

        return this;
    }

    @Override
    public void execute() {
        setObjective("Teleporting");

        BotCoordinate3D targetLocation = bot.getRuntimeStatus().getTargetLocation();
                // Телепортация в основном потоке
                Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
                    
                    bot.getNPCEntity().teleport(BotWorldHelper.getWorldLocation(targetLocation));
                    BotLogger.info(this.isLogged(), "🗲 " + bot.getId() + " Телепортируемся в " + BotStringUtils.formatLocation(targetLocation));
                });

       stop();

    }

    public void stop() {
        isDone = true;
    }

}
