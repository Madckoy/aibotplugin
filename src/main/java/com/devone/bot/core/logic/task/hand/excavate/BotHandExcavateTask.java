package com.devone.bot.core.logic.task.hand.excavate;

import org.bukkit.Material;
import org.bukkit.block.Block;


import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.hand.BotHandTask;
import com.devone.bot.core.logic.task.hand.excavate.params.BotHandExcavateTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

public class BotHandExcavateTask extends BotHandTask {

    public BotHandExcavateTask(Bot bot) {
        super(bot);
        setName("⛏");
        setObjective("Excavate block");
    }

    private BotBlockData target;
    private boolean isLogged = true;
    private BukkitTask bukkitTask;


    @Override
    public BotHandExcavateTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);

        if (params instanceof BotHandExcavateTaskParams handParams) {
            this.target = handParams.getTarget();
            this.isLogged = handParams.isLogged();
            bot.getRuntimeStatus().setTargetLocation(target.getCoordinate3D());
            BotLogger.info(isLogged, bot.getId() + " ✅ Parameters for BotHandExcavateTask set.");
        } else {
            BotLogger.info(isLogged, bot.getId() + " ❌ Invalid parameters for BotHandExcavateTask.");
            //this.stop();
        }
        return this;
    }

    public void execute() {

        super.execute();

        BotLogger.info(isLogged, bot.getId() + " 🔶 Executing BotHandExcavateTask");

        setObjective("Excavating block: " + target);

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {

                if (isDone) {
                    stop(); 
                    cancel(); 
                    return;
                }
            
                // 🧱 Работа с блоком
                Block block = BotWorldHelper.getBlockAt(target);
                if (block == null || block.getType() == Material.AIR) {
                    BotLogger.info(isLogged, bot.getId() + " ✅ Block already excavated.");
                    stop(); cancel(); return;
                }

                animateHand();

                BotUtils.playBlockBreakEffect(block.getLocation());

                block.breakNaturally();

                bot.getRuntimeStatus().brokenBlocksIncrease(target.type);

                BotLogger.info(isLogged, bot.getId() + " 🧱 Block excavated: " + block);
                
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 10L);
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
