package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskTeleportConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;

import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class BotTaskTeleport extends BotTask {
    
    private BotTaskTeleportConfig config;

    public BotTaskTeleport(Bot bot, Player player) {
        super(bot, player, "👯");

        config = new BotTaskTeleportConfig();
    }

     public void configure(Object... params) {
        super.configure(params);

        if (params.length >= 1 && params[0] instanceof Location) {
            targetLocation = (Location) params[0];
        }

        BotLogger.debug("⚙️ BotTaskTeleport сконфигурирована: " + BotStringUtils.formatLocation(targetLocation));
    }

    @Override
    public void executeTask() {
       //do nothing
                // Телепортация в основном потоке
                Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
                    
                    bot.getNPCEntity().teleport(targetLocation);

                    //bot.getNPCEntity().teleport();
                });

       isDone = true;

    }

}
