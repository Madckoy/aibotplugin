package com.devone.bot.core.task.active.fishing;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.fishing.params.BotFishingTaskParams;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

import org.bukkit.Bukkit;
import org.bukkit.Material;

import java.util.Random;

public class BotFishingTask extends BotTaskAutoParams<BotFishingTaskParams> {

    public BotFishingTask(Bot bot) {
        super(bot, BotFishingTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotFishingTaskParams> setParams(BotFishingTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        return this;
    }

    @Override
    public void execute() {
        String id = bot.getId();
        BotLogger.debug(icon, isLogged(), id + " 🎣 Начал ловить рыбу...");

        BotUtils.swingMainHand(bot);

        int delayTicks = 20 * (5 + new Random().nextInt(26)); // 5–30 сек

        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {

            Material[] fishTypes = {
                Material.COD,
                Material.SALMON,
                Material.TROPICAL_FISH,
                Material.PUFFERFISH
            };

            Material caught = fishTypes[new Random().nextInt(fishTypes.length)];
            
            bot.getInventory().addItem(caught, 1);            

            BotLogger.debug(icon, isLogged(), id + " 🐟 Поймал рыбу: " + caught.name());

            stop();
        }, delayTicks);
    }
}
