package com.devone.bot.core.bot.task.active.hand.excavate;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.task.active.hand.BotHandTask;
import com.devone.bot.core.bot.task.active.hand.excavate.params.BotHandExcavateTaskParams;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotHandExcavateTask extends BotHandTask<BotHandExcavateTaskParams> {

    private BukkitTask bukkitTask;
    private BotBlockData target;

    public BotHandExcavateTask(Bot bot) {
        super(bot, BotHandExcavateTaskParams.class);
    }

    public BotHandExcavateTask setParams(BotHandExcavateTaskParams params) {
        super.setParams(params); // вызовет BotHandTask.setParams()
        this.target = params.getTarget();
        bot.getNavigation().setTarget(target);

        return this;
    }

    @Override
    public void execute() {
        super.execute();

        if (target == null) {
            BotLogger.debug("❌", isLogging(), bot.getId() + " BotHandExcavateTask: Target is null.");
            this.stop();
            return;
        }

        BotLogger.debug("🔶", isLogging(), bot.getId() + " Executing BotHandExcavateTask");

        setObjective(params.getObjective() + " " + target.getType() +" at "+target.getLocation());
        BotHandExcavateTask heTask = this;

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (done) {
                    stop();
                    cancel();
                    return;
                }

                Block block = BotWorldHelper.getBlockAt(target);
                if (block == null || block.getType() == Material.AIR) {
                    BotLogger.debug(icon, isLogging(), bot.getId() + " ✅ Block already excavated.");
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
                    BotLogger.debug(icon, isLogging(), bot.getId() + " ⚠️ Block changed type before excavation. Skipping.");
                    stop();
                    cancel();
                    return;
                }

                animateHand(heTask, bot);

                BotUtils.playBlockBreakEffect(heTask, bot, block.getLocation());

                block.breakNaturally();
                bot.getBrain().getMemory().brokenBlocksIncrease(target.getType());
                BotBlockData bl = BotWorldHelper.worldBlockToBotBlock(block);
                BotLogger.debug(icon, isLogging(), bot.getId() + " 🪨 Block is excavated: " + bl);
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 10L);
    }

    @Override
    public void stop() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }

        bot.getNavigation().setTarget(null);

        super.stop();
    }
}
