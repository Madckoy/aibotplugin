package com.devone.bot.core.logic.task.hand.attack;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.task.hand.BotHandTask;
import com.devone.bot.core.logic.task.hand.attack.listener.BotHandAttackListener;
import com.devone.bot.core.logic.task.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.core.logic.task.params.IBotTaskParams;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotCoordinate3D;
import com.devone.bot.utils.blocks.BotCoordinate3DHelper;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

public class BotHandAttackTask extends BotHandTask {

    private BotBlockData target;
    private double damage = 5.0;
    private boolean isLogged = true;
    private BukkitTask bukkitTask;
    private BotHandAttackListener listener;
    private long hits = 0;
    private long attempts = 0;
    private BotCoordinate3D startPos = null;

    private int pursuitTicks = 0;
    private final int MAX_PURSUIT_TICKS = 120;

    public BotHandAttackTask(Bot bot) {
        super(bot);
        setName("⚔️");
        setObjective("Attack the target");
        attempts = 0;
        hits  = 0;
        startPos = new BotCoordinate3D(bot.getRuntimeStatus().getCurrentLocation());
    }

    @Override
    public BotHandAttackTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);

        if (params instanceof BotHandAttackTaskParams handParams) {
            this.damage = handParams.getDamage();
            this.target = handParams.getTarget();
            this.isLogged = handParams.isLogged();

            bot.getRuntimeStatus().setTargetLocation(target.getCoordinate3D());

            BotLogger.info(isLogged, bot.getId() + " ✅ Parameters for BotHandAttackTask set.");

        } else {
            BotLogger.info(isLogged, bot.getId() + " ❌ Invalid parameters for BotHandAttackTask.");
            //this.stop();
        }
        return this;
    }

    public void execute() {

        super.execute();

        BotLogger.info(isLogged, bot.getId() + " 🔶 Executing BotHandAttachTask");

        if (target == null) {
            BotLogger.info(isLogged, bot.getId() + " ❌ BotHandAttackTask: Target is null.");
            this.stop();
            return;
        }

        if (listener == null) {
            listener = new BotHandAttackListener(this);
            Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
        }

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isDone || bot.getNPCEntity() == null) {
                    BotLogger.info(isLogged, bot.getId() + " ❌ BotHandAttackTask: Task is done or Bot NPC is null.");
                    stop(); cancel(); return;
                }

                setObjective("Attacking: " + target.type + "("+target.x+", "+target.y+", "+target.z+")");

                attempts = attempts+1;

                // 🔍 Работа с мобом по UUID
                if (target.uuid != null) {
                    LivingEntity living = BotWorldHelper.findLivingEntityByUUID(target.uuid);

                    if (living == null || living.isDead() || living.getHealth() <= 0) {
                        BotLogger.info(isLogged, bot.getId() + " ✅ Target is dead or unreachable.");

                        target.uuid = null;
                        target = null;
                        
                        bot.getRuntimeStatus().setTargetLocation(null);

                        stop(); cancel(); return;
                    }

                    // 🔄 Обновляем targetLocation
                    bot.getRuntimeStatus().setTargetLocation(BotCoordinate3DHelper.convertFrom(living.getLocation()));
                    BotUtils.lookAt(bot, BotCoordinate3DHelper.convertFrom(living.getLocation()));
                    bot.getNPCNavigator().setTarget(living.getLocation());
                    double distance = bot.getNPCEntity().getLocation().distance(living.getLocation());
                    
                    if (distance > 2.0) {
                        bot.getNPCNavigator().getDefaultParameters().speedModifier(2.5F);

                        if (pursuitTicks % 20 == 0) {
                            bot.getNPCNavigator().getDefaultParameters().speedModifier(2.5F);
                            bot.getNPCNavigator().setTarget(living.getLocation());
                            bot.getRuntimeStatus().setTargetLocation(BotCoordinate3DHelper.convertFrom(living.getLocation()));
                            BotUtils.lookAt(bot, BotCoordinate3DHelper.convertFrom(living.getLocation()));
                            BotLogger.info(isLogged, bot.getId() + " 🚶 Pursuing mob, correcting direction. Ddistance: " + String.format("%.2f", distance));
                        }

                        BotLogger.info(isLogged, bot.getId() + " 🚶 Pursuing mob, distance: " + String.format("%.2f", distance));

                    } else {
                        animateHand();

                        living.damage(damage, bot.getNPCEntity());
                     
                        hits++;
                        
                        BotLogger.info(isLogged, bot.getId() + " ✋🏻 Attacked mob: " + living.getType());
                    }

                    if (++pursuitTicks > MAX_PURSUIT_TICKS) {
                        BotLogger.info(isLogged, bot.getId() + " ⏱️ Pursuit timeout reached.");
                        stop(); cancel(); return;
                    }

                }
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 10L);

        BotLogger.info(isLogged, bot.getId() + " ⏱️ Hits made: " + hits);
        BotLogger.info(isLogged, bot.getId() + " ⏱️ Attempts made: " + attempts);

        BotCoordinate3D endPos = bot.getRuntimeStatus().getCurrentLocation();
        if(endPos.equals(startPos) && hits == 0) {
                // consider the bot is stuck
                bot.getRuntimeStatus().setStuck(true);
        }
    }

    @Override
    public void stop() {
        super.stop();

        if (listener != null) {
            listener.unregister();
            listener = null;
        }

        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }
        BotLogger.info(isLogged, bot.getId() + " ⛔ BotHandAttackTask: Task is stopped");
    }

}
