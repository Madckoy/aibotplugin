package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.utils.*;
import org.bukkit.Location;
import com.devone.aibot.core.Bot;

public class BotTaskMove extends BotTask {

    public BotTaskMove(Bot bot) {
        super(bot, "MOVE");
        this.bot = bot;
    }

    @Override
    public void configure(Object... params) {
        super.configure(params);
        if (params.length == 1 && params[0] instanceof Location) {
            this.targetLocation = (Location) params[0];
        } else {
            BotLogger.error(bot.getId() + " ❌ Некорректные параметры для `BotTaskMove`!");
            isDone = true; // Завершаем задачу, если переданы неправильные параметры
        }
    }

    @Override
    public void executeTask() {

        if (isDone ||
                isPaused ||
                targetLocation == null

        )
        
        bot.getNPCNavigator().setTarget(targetLocation);

        if(bot.getNPCNavigator().isNavigating()) {
            return; //let him finish his movement
        } else {
            // Проверяем, достиг ли бот цели
            if (BotNavigation.hasReachedTarget(bot, targetLocation, 4.0)) {

                bot.resetTargetLocation();

                isDone = true;
                return;
            } else {
                BotNavigation.navigateTo(bot, targetLocation, 8); 
            }

        }

    }

}
