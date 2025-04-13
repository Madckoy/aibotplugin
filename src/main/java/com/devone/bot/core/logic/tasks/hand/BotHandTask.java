package com.devone.bot.core.logic.tasks.hand;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.core.logic.tasks.hand.params.BotHandTaskParams;
import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.blocks.BotCoordinate3DHelper;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;


public class BotHandTask extends BotTask {

    private BotBlockData target;
    private double damage = 5.0;
    private boolean isLogged = true;
    private UUID lastTargetUUID = null;
    private BukkitTask bukkitTask;

    public BotHandTask(Bot bot) {
        super(bot, "✋🏻");
        setObjective("Hit the target");
        this.isLogged = true;
    }

    public BotHandTask(Bot bot, String name) {
        super(bot,name);
        setObjective("Hit the target");
        this.isLogged = true;
    }

    @Override
    public BotHandTask configure(IBotTaskParams params) {

        super.configure((BotTaskParams) params);

        if(params instanceof BotHandTaskParams) {
            BotHandTaskParams useHandParams = (BotHandTaskParams) params;
 
            this.damage = useHandParams.getDamage();
            this.target = useHandParams.getTarget();
            this.isLogged = useHandParams.isLogged();
            bot.getRuntimeStatus().setTargetLocation(target.getCoordinate3D());

        } else {
            BotLogger.info(this.isLogged(),bot.getId() + " ❌ Некорректный тип параметров для `BotUseHandTask`!");
            this.stop();
        }    
        return this;
    }


    @Override
    public void execute() {
        if (target == null) {
            BotLogger.info(this.isLogged(), bot.getId() + " ❌ Цель для BotHandTask не задана");
            this.stop();
            return;
        }

        BotCoordinate3D faceTarget = target;
        Block faceBlock = BotWorldHelper.getBlockAt(faceTarget);
        setObjective("Hitting: " + BotUtils.getBlockName(faceBlock)+" at "+faceTarget);

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isDone || bot.getNPCEntity() == null) {
                    stop();
                    cancel();
                    return;
                }

                turnToTarget(target);
                animateHand(); 

                if (target.uuid != null) {
                    Entity entity = Bukkit.getEntity(target.uuid);
                    if (!(entity instanceof LivingEntity living) || living.isDead()) {
                        BotLogger.info(isLogged, bot.getId() + " ✅ Цель мертва или недоступна. Завершаем.");
                        stop();
                        cancel();
                        return;
                    }


                    double distance = bot.getNPCEntity().getLocation().distance(living.getLocation());

                    if (distance > 3.0) {
                        if (lastTargetUUID == null || !lastTargetUUID.equals(living.getUniqueId())) {

                            turnToTarget(new BotCoordinate3D(BotCoordinate3DHelper.convertFrom(living.getLocation())));

                            animateHand();                  

                            bot.getNPCNavigator().setTarget(living.getLocation());
                            lastTargetUUID = living.getUniqueId();
                    
                            BotLogger.info(isLogged, bot.getId() + " 🚶 Цель далеко (" + String.format("%.2f", distance) + "), двигаемся.");
                        }
                        return;
                    }

                    living.damage(damage, bot.getNPCEntity());

                    if (living.getHealth() - damage <= 0) {

                        bot.getRuntimeStatus().killedMobsIncrease();

                        BotLogger.info(isLogged, bot.getId() + " 💀 Моб убит: " + living.getType());
                    }

                    BotLogger.info(isLogged, bot.getId() + " ✋🏻 Ударил моба: " + living.getType());

                } else {

                    Block block = BotWorldHelper.getBlockAt(target);
                    if (block == null || block.getType() == Material.AIR) {
                        BotLogger.info(isLogged, bot.getId() + " ✅ Блок разрушен. Завершаем.");
                        stop();
                        cancel();
                        return;
                    }

                    BotUtils.playBlockBreakEffect(block.getLocation());
                    block.breakNaturally();
                    bot.getRuntimeStatus().brokenBlocksIncrease();

                    BotLogger.info(isLogged, bot.getId() + " ✋🏻 Ударил блок: " + block.getType());
                }
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 10L);
    }

    public void stop() {
        isDone = true;
        if(bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }  
    }

}
