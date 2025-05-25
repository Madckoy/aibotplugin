package com.devone.bot.core.task.active.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.task.passive.BotTaskAutoParams;
import com.devone.bot.core.task.passive.IBotTaskParameterized;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.utils.blocks.BlockMaterialUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
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

        if (bot.getNPCEntity() == null) {
            BotLogger.debug(icon, this.isLogging(), bot.getId() + " ❌ Проблема с NPC Enitity.");
            stop();
            return;
        }

        setObjective(params.getObjective() + " to: " + target.toCompactString());

        BotLogger.debug(icon, this.isLogging(), bot.getId() + " ⚡ Телепорт в " + target);

        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
        Location baseLocation = BotWorldHelper.botPositionToWorldLocation(target);
        
        // Центр блока по X/Z даже с отрицательными координатами
        double centerX = Math.floor(baseLocation.getX()) + 0.5;
        double centerZ = Math.floor(baseLocation.getZ()) + 0.5;

        // Y по умолчанию — верх блока
        double y = Math.floor(baseLocation.getY());

        Block baseBlock = new Location(baseLocation.getWorld(), centerX, y, centerZ).getBlock();
        BotBlockData blockData = BotWorldHelper.blockToBotBlockData(baseBlock);

        // Если блок — ковер/плита/воздух — ставим на текущую Y
        if (BlockMaterialUtils.isCover(blockData) || BlockMaterialUtils.isAir(blockData)) {
            y = baseLocation.getY(); // сохраняем как есть
        } else {
            y = Math.floor(baseLocation.getY()) + 1.0; // ставим на верх блока
        }

        // Чуть поднимаем, чтобы не "тонул" в блок (не более 0.01)
        Location aligned = new Location(baseLocation.getWorld(), centerX, y + 0.01, centerZ);

        bot.getNPCEntity().teleport(aligned);
        BotMemoryV2Utils.incrementCounter(bot, "teleportUsed");            
        bot.getTaskManager().getActiveTask().stop();

        BotLogger.debug(icon, isLogging(),
            bot.getId() + " ⚡ Телепорт завершен с " + baseLocation.toVector() + " в " + aligned.toVector());
        
        stop();
    });
        }
}
