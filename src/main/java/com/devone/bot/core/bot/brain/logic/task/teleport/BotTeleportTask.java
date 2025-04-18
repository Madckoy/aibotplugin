package com.devone.bot.core.bot.brain.logic.task.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.brain.logic.task.BotTaskAutoParams;
import com.devone.bot.core.bot.brain.logic.task.IBotTaskParameterized;
import com.devone.bot.core.bot.brain.logic.task.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;
import com.devone.bot.core.bot.brain.logic.utils.world.BotWorldHelper;

public class BotTeleportTask extends BotTaskAutoParams<BotTeleportTaskParams> {

    private BotLocation target;

    public BotTeleportTask(Bot bot, Player player) {
        super(bot, player, BotTeleportTaskParams.class);
        // Загружаем дефолтные параметры из файла
        setParams(new BotTeleportTaskParams());
    }

    @Override
    public IBotTaskParameterized<BotTeleportTaskParams> setParams(BotTeleportTaskParams params) {
        super.setParams(params);

        this.target = params.getLocation();
        setIcon(params.getIcon());
        setObjective(params.getObjective());

        if (this.target == null) {
            BotLogger.debug("❌", this.isLogging(), bot.getId() + " Целевая точка не задана. Остановка задачи.");
            this.stop();
        }

        return this;
    }

    @Override
    public void execute() {
        if (this.target == null) {
            BotLogger.debug("❌", this.isLogging(), bot.getId() + " Нет координат для телепортации.");
            stop();
            return;
        }

        setObjective(params.getObjective() + " to: " + target);

        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            Location baseLocation = BotWorldHelper.getWorldLocation(target);
            Location safeOffset = baseLocation.clone().add(0.5, 0, 0.5);

            bot.getNPCEntity().teleport(safeOffset);
            bot.getState().setStuck(false);
            bot.getBrain().getMemory().teleportUsedIncrease();

            BotLogger.debug("⚡", this.isLogging(),
                    bot.getId() + " Телепорт с " + baseLocation.toVector() + " → " + safeOffset.toVector());
        });

        stop();
    }
}
