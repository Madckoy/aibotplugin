package com.devone.bot.core.logic.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;
import com.devone.bot.utils.BotLogger;
import com.devone.bot.utils.BotStringUtils;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.BotWorldHelper;

import java.util.Arrays;

public class BotUseHandTask extends BotTask {

    private BotBlockData target;
    private int damage = 5;

    public BotUseHandTask(Bot bot) {
        super(bot, "✋🏻");
        setObjective("Hit the target");
        this.isLogged = config.isLogged();
    }

    public BotUseHandTask(Bot bot, String name) {
        super(bot,name);
        setObjective("Hit the target");
    }

    @Override
    public BotTask configure(Object... params) {
        super.configure(params);

        boolean hasParams = false;

        if (params.length > 0 && params[0] instanceof BotCoordinate3D loc) {
            bot.getRuntimeStatus().setTargetLocation(loc);
            hasParams = true;
        }

        if (params.length > 1 && params[1] instanceof BotBlockData entity) {
            this.target = entity;
            hasParams = true;
        }

        if (params.length > 2 && params[2] instanceof Integer dmg) {
            this.damage = dmg;
            hasParams = true;
        }

        if (!hasParams) {
            BotLogger.info(this.isLogged(), bot.getId() + " ❌ Некорректные параметры для `BotTaskUseHand`: " + Arrays.toString(params));
            this.stop();
        }

        return this;
    }

    @Override
    public void execute() {
        if (bot.getRuntimeStatus().getTargetLocation() == null && target == null) {
            BotLogger.info(this.isLogged(), bot.getId() + " ❌ Нет цели или координат для удара");
            this.stop();
            return;
        }

    
        BotCoordinate3D faceTarget = (target != null) ? target.getCoordinate3D() : bot.getRuntimeStatus().getTargetLocation();

        Block faceBlock = BotWorldHelper.getBlockAt(faceTarget);

        setObjective("Hitting: " + BotUtils.getBlockName(faceBlock)+" at "+BotStringUtils.formatLocation(faceTarget));
    
        turnToBlock(faceTarget);
    
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            
            animateHand();
    
            if (faceBlock != null && faceBlock.getType() != Material.AIR) {
                BotUtils.playBlockBreakEffect(faceBlock.getLocation());

                faceBlock.breakNaturally();

                BotLogger.info(this.isLogged(), bot.getId() + " ✋🏻 Нанесён урон по: " + faceBlock);

            } 

            this.stop();

        });
    }

    private void turnToBlock(BotCoordinate3D target) {
        
        // ✅ Принудительно обновляем положение, если поворот сбрасывается
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {
            BotUtils.lookAt(bot, target);
        }, 1L); // ✅ Через тик, чтобы дать время на обновление

        BotLogger.info(this.isLogged(), "🔄 TURNING: " + bot.getId() + " to look at the target: " + BotStringUtils.formatLocation(target));
    }

    private void animateHand() {
        if (bot.getNPCEntity() instanceof Player playerBot) {
            playerBot.swingMainHand();
            BotLogger.info(this.isLogged(), "✋🏻 Анимация руки выполнена");
        } else {
            BotLogger.info(this.isLogged(), "✋🏻 Анимация не выполнена: бот — не игрок");
        }
    }

    public void stop() {
        isDone = true;
    }

}
