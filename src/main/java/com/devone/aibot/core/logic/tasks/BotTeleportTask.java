package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTeleportTaskConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import org.bukkit.entity.Player;

public class BotTeleportTask extends BotTask {

    public BotTeleportTask(Bot bot, Player player) {
        super(bot, player, "💫");

        config = new BotTeleportTaskConfig();
        this.isLogged = config.isLogged();
        
        setObjective("Teleport");
    }

     public BotTask configure(Object... params) {
        super.configure(params);

        if (params.length >= 1 && params[0] instanceof Location) {
            Location loc  = (Location) params[0];
            bot.getRuntimeStatus().setTargetLocation(loc);
        }

        BotLogger.info(this.isLogged(), "⚙️ BotTaskTeleport is configured: " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));

        return this;
    }

    @Override
    public void executeTask() {
        setObjective("Teleporting");

       //do nothing
                // Телепортация в основном потоке
                Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
                    
                    bot.getNPCEntity().teleport(bot.getRuntimeStatus().getTargetLocation());

                    //bot.getNPCEntity().teleport();
                });

       isDone = true;

    }

}
