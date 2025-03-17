package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import com.devone.aibot.core.Bot;

public class BotTaskMove extends BotTask {

    private Location lastTargetLocation = null;

    public BotTaskMove(Bot bot) {
        super(bot, "MOVE");
        this.bot = bot;
    }

    @Override
    public void configure(Object... params) {
        super.configure(params);

        if (params.length == 1 && params[0] instanceof Location) {
            this.targetLocation = (Location) params[0];
            lastTargetLocation = null;
        }
    }

    @Override
    public void executeTask() {
        if (isDone ||
                isPaused ||
                targetLocation == null

        )
            return;

        // Проверяем, достиг ли бот цели
        if (BotNavigation.hasReachedTarget(bot, targetLocation, 4.0)) {

            bot.resetTargetLocation();

            isDone = true;
            return;
        } else { 
            handleStuck();
        }
    }

}
