package com.devone.bot.core.logic.task.move;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.BotTask;
import com.devone.bot.core.logic.task.move.listeners.BotMoveTaskListener;
import com.devone.bot.core.logic.task.move.params.BotMoveTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.*;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.blocks.BotCoordinate3DHelper;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

import net.citizensnpcs.api.ai.event.NavigationCompleteEvent;

public class BotMoveTask extends BotTask {

    private BukkitTask taskHandle;;
    private boolean isMoving = false;
    private BotMoveTaskListener listener;
    private BotMoveTaskParams params = new BotMoveTaskParams();
    private float speed = 1.0F;

    public BotMoveTask(Bot bot) {
        super(bot);
        setIcon(params.getIcon());
        setObjective(params.getObjective());
        speed = params.getSpeed();
    }

    @Override
    public BotTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams)params);

        this.params.copyFrom(params);

        BotCoordinate3D loc = this.params.getTarget();

        this.speed     = this.params.getSpeed();

        bot.getRuntimeStatus().setTargetLocation(loc);

        if (loc != null) {
            bot.getRuntimeStatus().setTargetLocation(loc);
            BotLogger.info("✅", isLogging(),bot.getId() + "Target Location is set for `BotMoveTask`!");
        } else {
            BotLogger.info("❌", isLogging(),bot.getId() + "Target Location is null! Invalid parameter type for `BotMoveTask`!");
            this.stop();
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
            BotLogger.info("⚠️", isLogging(), bot.getId() + "NPC не заспавнен! Ожидаем...");
            this.stop(); // Или можно re-queue
            return;
        }
        
        if (bot.getRuntimeStatus().getTargetLocation()==null) {
            BotLogger.info("❌", isLogging(), bot.getId() + "Нет цели для движения! [ID: " + uuid + "]");
            this.stop();
            return;                 
        }
        
        //---------------------------
        Location targetLocation = BotWorldHelper.getWorldLocation(bot.getRuntimeStatus().getTargetLocation());

        Block targetBlock = BotWorldHelper.getBlockAt(bot.getRuntimeStatus().getTargetLocation());

        String block_name = BotUtils.getBlockName(targetBlock);
        
        BotCoordinate3D tc = bot.getRuntimeStatus().getTargetLocation();

        String tcs = tc != null ? " " + tc.x + ", " + tc.y + ", " + tc.z : "";

        setObjective(params.getObjective() + " to " + block_name + " at:" + tcs);

        if (!isMoving) {

            if (listener == null) {
                listener = new BotMoveTaskListener(this);
                Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
            }
            bot.getNPCNavigator().getDefaultParameters().speedModifier(speed);
            bot.getNPCNavigator().setTarget(targetLocation); // ← ОДИН РАЗ
            
            isMoving = true;
        
            BotLogger.info("🏃🏻‍♂️", this.isLogging(), bot.getId() + "Начал движение к " + targetLocation);
        
            taskHandle = Bukkit.getScheduler().runTaskTimer(AIBotPlugin.getInstance(), () -> {
                
                turnToTarget(new BotCoordinate3D(BotCoordinate3DHelper.convertFrom(targetLocation)));

                if (isDone || bot.getNPCEntity() == null) {
                    stopTaskHandle();
                    return;
                }
            
                long elapsed = System.currentTimeMillis() - startTime;
                if (elapsed > BotConstants.DEFAULT_TASK_TIMEOUT ) {
                    BotLogger.info("⏱️", isLogging(), bot.getId() + "Тайм-аут навигации! Прерываем задачу.");
                    bot.getRuntimeStatus().setStuck(true);  // 
                    stopTaskHandle();
                    this.stop();
                }
            
            }, 0L, 20L); // проверка раз в секунду
        }        
        else {
            BotLogger.info("⏳", this.isLogging(), bot.getId() + "Двигаюсь к " + targetLocation);
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

        this.isMoving = false;
        bot.getRuntimeStatus().setTargetLocation(null);
        BotLogger.info("🛑", this.isLogging(), bot.getId() + "Move task завершён");  
        
        stopTaskHandle();

        if (listener != null) {
            listener.unregister();
            listener = null;
        }
        super.stop();
    }

    @EventHandler
    public void onNavigationComplete(NavigationCompleteEvent event) {
        if (event.getNPC().getId() != bot.getNPC().getId()) return;

        BotLogger.info("✅", this.isLogging(), bot.getId() + "Навигатор сообщил о завершении");
        this.stop(); // Завершаем задачу
    }
}
