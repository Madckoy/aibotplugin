package com.devone.bot.core.task.active.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotTeleportTask extends BotTaskAutoParams<BotTeleportTaskParams> {

    private BotPosition target;

    public BotTeleportTask(Bot bot, Player player) {
        super(bot, player, BotTeleportTaskParams.class);
        // Загружаем дефолтные параметры из файла

        if (player != null) {

            BotTeleportTaskParams params = new BotTeleportTaskParams();
            BotPosition loc = BotWorldHelper.locationToBotPosition(player.getLocation());
            params.setPosition(loc);
        }

        setParams(new BotTeleportTaskParams());
    }

    @Override
    public IBotTaskParameterized<BotTeleportTaskParams> setParams(BotTeleportTaskParams params) {
        super.setParams(params);

        this.target = params.getPosition();
        setIcon(params.getIcon());
        setObjective(params.getObjective());

        if (this.target == null) {
            BotLogger.debug(icon, this.isLogging(), bot.getId() + " ❌ Целевая точка не задана. Остановка задачи.");
            this.stop();
        }

        return this;
    }

    @Override
    public void execute() {
        if (this.target == null) {
            BotLogger.debug(icon, this.isLogging(), bot.getId() + " ❌ Нет координат для телепортации.");
            stop();
            return;
        }

        setObjective(params.getObjective() + " to: " + target);

        BotLogger.debug(icon, this.isLogging(), bot.getId() + " ⚡ Телепорт в " + target);

        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            Location baseLocation = BotWorldHelper.botPositionToWorldLocation(target);
            Location safeOffset = baseLocation.clone().add(-0.5, 1.0, -0.5);

            bot.getNPCEntity().teleport(safeOffset);
            bot.getBrain().getMemory().teleportUsedIncrease();

            bot.getTaskManager().getActiveTask().stop();

            BotLogger.debug(icon, this.isLogging(),
                    bot.getId() + " ⚡ Телепорт завершен с " + baseLocation.toVector() + " в " + safeOffset.toVector());
            stop();
        });
        stop();
    }
}
