package com.devone.bot.core.task.active.fishing;

import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.fishing.params.BotFishingTaskParams;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;

import org.bukkit.Material;

import java.util.Random;

public class BotFishingTask extends BotTaskAutoParams<BotFishingTaskParams> {

    private long endTime; // системное время окончания
    private final Random random = new Random();
    private boolean hasCaughtFish = false;

    public BotFishingTask(Bot bot) {
        super(bot, BotFishingTaskParams.class);
    }

    @Override
    public IBotTaskParameterized<BotFishingTaskParams> setParams(BotFishingTaskParams params) {
        super.setParams(params);
        setIcon(params.getIcon());
        setObjective(params.getObjective());

        // Устанавливаем время завершения
        long timeoutMillis = params.getTimeout();
        this.endTime = System.currentTimeMillis() + timeoutMillis;

        return this;
    }

    @Override
    public void execute() {
        String id = bot.getId();

        if (System.currentTimeMillis() >= endTime) {
            BotLogger.debug(icon, isLogged(), id + " ⏰ Завершил рыбалку");
            stop();
            return;
        }

        // Анимация — каждый тик
        BotUtils.swingMainHand(bot);

        // 25% шанс поймать рыбу
        if (!hasCaughtFish && random.nextFloat() <= 0.25f) {
            Material[] fishTypes = {
                Material.COD,
                Material.SALMON,
                Material.TROPICAL_FISH,
                Material.PUFFERFISH
            };

            Material caught = fishTypes[random.nextInt(fishTypes.length)];
            bot.getInventory().addItem(caught, 1);
            hasCaughtFish = true;

            BotLogger.debug(icon, isLogged(), id + " 🐟 Поймал рыбу: " + caught.name());
        }
    }
}
