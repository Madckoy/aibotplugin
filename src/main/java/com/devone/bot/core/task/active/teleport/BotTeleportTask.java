package com.devone.bot.core.task.active.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.sonar.BotSonar3DTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.utils.blocks.BotLocation;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotTeleportTask extends BotTaskAutoParams<BotTeleportTaskParams> {

    private BotLocation target;

    public BotTeleportTask(Bot bot, Player player) {
        super(bot, player, BotTeleportTaskParams.class);
        // Загружаем дефолтные параметры из файла

        if (player != null) {

            BotTeleportTaskParams params = new BotTeleportTaskParams();
            BotLocation loc = BotWorldHelper.worldLocationToBotLocation(player.getLocation());
            params.setLocation(loc);
        }

        setParams(new BotTeleportTaskParams());
    }

    @Override
    public IBotTaskParameterized<BotTeleportTaskParams> setParams(BotTeleportTaskParams params) {
        super.setParams(params);

        this.target = params.getLocation();
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
            Location baseLocation = BotWorldHelper.getWorldLocation(target);
            Location safeOffset = baseLocation.clone().add(0.5, 0, 0.5);

            bot.getNPCEntity().teleport(safeOffset);
            bot.getBrain().getMemory().teleportUsedIncrease();

            bot.getTaskManager().getActiveTask().stop();

            BotSonar3DTask sonar = new BotSonar3DTask(bot);
            sonar.execute();
            bot.getNavigator().calculate(bot.getBrain().getMemory().getSceneData());

            BotLogger.debug(icon, this.isLogging(),
                    bot.getId() + " ⚡ Телепорт завершен с " + baseLocation.toVector() + " в " + safeOffset.toVector());

            stop();
        });

    }
}
