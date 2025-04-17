package com.devone.bot.core.logic.task.hand.excavate;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.hand.BotHandTask;
import com.devone.bot.core.logic.task.hand.excavate.params.BotHandExcavateTaskParams;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

public class BotHandExcavateTask extends BotHandTask<BotHandExcavateTaskParams> {

    private BukkitTask bukkitTask;

    public BotHandExcavateTask(Bot bot) {
        super(bot, BotHandExcavateTaskParams.class);
    }

    public BotHandExcavateTask setParams(BotHandExcavateTaskParams params) {
        super.setParams(params); // вызовет BotHandTask.setParams()
        return this;
    }

    @Override
    public void execute() {
        super.execute();

        BotBlockData target = getTarget();
        if (target == null) {
            BotLogger.info("❌", isLogging(), bot.getId() + " BotHandExcavateTask: Target is null.");
            this.stop();
            return;
        }

        BotLogger.info("🔶", isLogging(), bot.getId() + " Executing BotHandExcavateTask");
        setObjective(params.getObjective() + ": " + target);

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isDone) {
                    stop();
                    cancel();
                    return;
                }

                Block block = BotWorldHelper.getBlockAt(target);
                if (block == null || block.getType() == Material.AIR) {
                    BotLogger.info("✅", isLogging(), bot.getId() + " Block already excavated.");
                    stop();
                    cancel();
                    return;
                }

                Material actualMaterial = block.getType(); // org.bukkit.Material
                String expectedType = target.getType();    // String
                
                if (expectedType == null && actualMaterial == null) {
                    return; // оба отсутствуют — считаем, что всё ок
                }
                
                if (expectedType == null || actualMaterial == null || !actualMaterial.name().equalsIgnoreCase(expectedType)) {
                    BotLogger.info("⚠️", isLogging(), bot.getId() + " Block changed type before excavation. Skipping.");
                    stop();
                    cancel();
                    return;
                }

                animateHand();
                BotUtils.playBlockBreakEffect(block.getLocation());
                block.breakNaturally();
                bot.getBrain().brokenBlocksIncrease(target.getType());

                BotLogger.info("🪨", isLogging(), bot.getId() + " Block excavated: " + block);
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 10L);
    }

    @Override
    public void stop() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }

        bot.getBrain().setTargetLocation(null);

        super.stop();
    }
}
