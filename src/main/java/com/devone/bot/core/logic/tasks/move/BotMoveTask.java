package com.devone.bot.core.logic.tasks.move;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.core.logic.tasks.move.config.BotMoveTaskConfig;
import com.devone.bot.core.logic.tasks.move.listeners.BotMoveTaskListener;
import com.devone.bot.core.logic.tasks.move.params.BotMoveTaskParams;
import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.utils.*;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;

public class BotMoveTask extends BotTask {

    private BukkitTask taskHandle;
    private BotMoveTaskConfig config = new BotMoveTaskConfig();
    @SuppressWarnings("unused")
    private double speedMultiplier = config.getSpeedMultiplier();
    private boolean isMoving = false;
    private BotMoveTaskListener listener;

    public BotMoveTask(Bot bot) {
        super(bot, "🏃🏻‍♂️‍➡️");

        setObjective("Move" );

        isLogged = config.isLogged();

    }

    @Override
    public BotTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams)params);

        if (params instanceof BotMoveTaskParams) {
            BotMoveTaskParams moveParams = (BotMoveTaskParams) params;
            BotCoordinate3D loc = moveParams.getTarget();
            this.speedMultiplier = moveParams.getSpeedMultiplier();
            bot.getRuntimeStatus().setTargetLocation(loc);

            if (loc != null) {
                bot.getRuntimeStatus().setTargetLocation(loc);
            } else {
                BotLogger.info(isLogged(),bot.getId() + " ❌ Некорректные параметры для `BotMoveTask`!");
                this.stop();
            }

        }
        return this;
    }

    @Override
    public void execute() {
        if (taskHandle != null && !taskHandle.isCancelled()) {
            //BotLogger.info(bot.getId() + " ⏳ Таймер уже запущен, жду [ID: " + uuid + "]");
        } else {
            if (isDone || isPaused) return;
        }

        if (!bot.getNPC().isSpawned()) {
            BotLogger.info(isLogged(), bot.getId() + " ⚠️ NPC не заспавнен! Ожидаем...");
            this.stop(); // Или можно re-queue
            return;
        }
        
        if (bot.getRuntimeStatus().getTargetLocation()==null) {
            BotLogger.info(isLogged(), bot.getId() + " ❌ Нет цели для движения! [ID: " + uuid + "]");
            this.stop();
            return;                 
        }
        
        //---------------------------
        Location targetLocation = BotWorldHelper.getWorldLocation(bot.getRuntimeStatus().getTargetLocation());

        Block targetBlock = BotWorldHelper.getBlockAt(bot.getRuntimeStatus().getTargetLocation());

        String block_name = BotUtils.getBlockName(targetBlock);
        
        BotCoordinate3D tc = bot.getRuntimeStatus().getTargetLocation();

        String tcs = tc != null ? " " + tc.x + ", " + tc.y + ", " + tc.z : "";

        setObjective("Moving to " + block_name + " at:" + tcs);

        if (!isMoving) {

            if (listener == null) {
                listener = new BotMoveTaskListener(this);
                Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
            }
            
            bot.getNPCNavigator().setTarget(targetLocation); // ← ОДИН РАЗ
            
            isMoving = true;
        
            BotLogger.info(this.isLogged(), bot.getId() + " 🏃🏻‍♂️ Начал движение к " + targetLocation + " [ID: " + uuid + "]");
        
            taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
                if (isDone || bot.getNPCEntity() == null) {
                    stopTaskHandle();
                    return;
                }
            
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > BotConstants.DEFAULT_TASK_TIMEOUT ) {
                    BotLogger.warn(isLogged(), bot.getId() + " ⏱ Тайм-аут навигации! Прерываем задачу. [ID: " + uuid + "]");
                    bot.getRuntimeStatus().setStuck(true);
                    stopTaskHandle();
                    this.stop();
                }
            
            }, 0L, 20L); // проверка раз в секунду
        }        
        else {
            BotLogger.info(this.isLogged(), bot.getId() + " ⏳ Двигаюсь к " + targetLocation + " [ID: " + uuid + "]");
        }
        
    }

    private void stopTaskHandle() {
        if (taskHandle != null && !taskHandle.isCancelled()) {
            taskHandle.cancel();
            taskHandle = null;
        }
    }

    @Override
    public void stop() {
        this.isDone = true;
        this.isMoving = false;
        bot.getRuntimeStatus().setTargetLocation(null);
        BotLogger.info(this.isLogged(), bot.getId() + " 🛑 Move task завершён [ID: " + uuid + "]");  
        
        stopTaskHandle();

        if (listener != null) {
            listener.unregister();
            listener = null;
        }
    
    }

    @EventHandler
    public void onNavigationComplete(NavigationCompleteEvent event) {
        if (event.getNPC().getId() != bot.getNPC().getId()) return;

        BotLogger.info(this.isLogged(), bot.getId() + " ✅ Навигатор сообщил о завершении [ID: " + uuid + "]");
        this.stop(); // Завершаем задачу
    }
}
