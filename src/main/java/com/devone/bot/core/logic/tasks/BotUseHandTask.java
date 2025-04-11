package com.devone.bot.core.logic.tasks;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.core.logic.tasks.params.BotUseHandTaskParams;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.utils.BotBlockData;
import com.devone.bot.utils.BotCoordinate3D;
import com.devone.bot.utils.BotLogger;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.BotWorldHelper;


public class BotUseHandTask extends BotTask {

    private BotBlockData target;
    private double damage = 5.0;

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
    public BotUseHandTask configure(IBotTaskParams params) {

        super.configure((BotTaskParams) params);

        if(params instanceof BotUseHandTaskParams) {
            BotUseHandTaskParams useHandParams = (BotUseHandTaskParams) params;
 
            this.damage = useHandParams.getDamage();

        } else {
            BotLogger.info(this.isLogged(),bot.getId() + " ❌ Некорректные параметры для `BotUseHandTask`!");
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

        setObjective("Hitting: " + BotUtils.getBlockName(faceBlock)+" at "+faceTarget);
    
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

        BotLogger.info(this.isLogged(), "🔄 TURNING: " + bot.getId() + " to look at the target: " + target);
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
