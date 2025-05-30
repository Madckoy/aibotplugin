package com.devone.bot.core.task.active.hand.excavate;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.memory.BotMemoryItem;
import com.devone.bot.core.brain.memory.BotMemoryPartition;
import com.devone.bot.core.brain.memory.BotMemoryV2Utils;
import com.devone.bot.core.task.active.hand.BotHandTask;
import com.devone.bot.core.task.active.hand.excavate.params.BotHandExcavateTaskParams;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

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

        BotLogger.debug(icon, isLogged(), bot.getId() + " Исполняем подзадачу руки");
        super.execute();
        BotLogger.debug(icon, isLogged(), bot.getId() + " Исполняем подзадачу руки - прверка таргета");
        
        if (getTarget() == null) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " ❌ BotHandExcavateTask: Target is null.");
            this.stop();
            return;
        }

        BotLogger.debug(icon, isLogged(), bot.getId() + " 🔶 Executing BotHandExcavateTask");

        setObjective(params.getObjective() + " " + getTarget().getType() + " at " + getTarget().getPosition().toPositionKey());

        BotHandExcavateTask heTask = this;

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (done) {
                    stop();
                    cancel();
                    return;
                }

                Block block = BotWorldHelper.botPositionToWorldBlock(getTarget().getPosition());
                
                if (block == null || block.getType() == Material.AIR) {
                    BotLogger.debug(icon, isLogged(), bot.getId() + " ✅ Block already excavated.");
                    stop();
                    cancel();
                    return;
                }

                Material actualMaterial = block.getType(); // org.bukkit.Material
                String expectedType = getTarget().getType(); // String

                if (expectedType == null && actualMaterial == null) {
                    return; // оба отсутствуют — считаем, что всё ок
                }

                if (expectedType == null || actualMaterial == null
                        || !actualMaterial.name().equalsIgnoreCase(expectedType)) {
                    BotLogger.debug(icon, isLogged(),
                            bot.getId() + " ⚠️ Block changed type before excavation. Skipping.");
                    stop();
                    cancel();
                    return;
                }

                turnToTargetSync(heTask, getTarget().getPosition());

                animateHandSync(heTask, bot);

                BotUtils.playBlockBreakEffectSync(heTask, bot, block.getLocation());

                block.breakNaturally();

                BotMemoryV2Utils.incrementNestedTotal(bot, BotMemoryPartition.PartitionKey.STATS, 
                                                    BotMemoryPartition.PartitionKey.DESTROYED, 
                                                    getTarget().getType(), 
                                                    BotMemoryItem.ItemKey.TOTAL);                                                              


                BotLogger.debug(icon, isLogged(), bot.getId() + " 🧊 Block is excavated: " + getTarget().getType());
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 1L, 1L);
    }

    @Override
    public void stop() {
        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }

        super.stop();
    }
}
