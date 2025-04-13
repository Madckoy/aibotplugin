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
import com.devone.bot.utils.blocks.BotCoordinate3DHelper;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

public class BotHandAttackTask extends BotHandTask {

    private BotBlockData target;
    private double damage = 5.0;
    private boolean isLogged = true;
    private BukkitTask bukkitTask;
    private BotHandAttackListener listener;

    private int pursuitTicks = 0;
    private final int MAX_PURSUIT_TICKS = 600; // ~30 секунды (если тик каждые 10л)

    public BotHandAttackTask(Bot bot) {
        super(bot);
        setName("⚔️");
        setObjective("Attack the target");
    }

    @Override
    public BotHandAttackTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);

        if (params instanceof BotHandAttackTaskParams handParams) {
            this.damage = handParams.getDamage();
            this.target = handParams.getTarget();
            this.isLogged = handParams.isLogged();

            bot.getRuntimeStatus().setTargetLocation(target.getCoordinate3D());

        } else {
            BotLogger.info(isLogged, bot.getId() + " ❌ Invalid parameters for BotHandTask.");
            this.stop();
        }
        return this;
    }

    @Override
    public void execute() {
        super.execute();

        if (listener == null) {
            listener = new BotHandAttackListener(this);
            Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
        }

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isDone || bot.getNPCEntity() == null) {
                    stop(); cancel(); return;
                }
                
                setObjective("Attacking the target: " + target);

                // 🔍 Работа с мобом по UUID
                if (target.uuid != null) {
                    LivingEntity living = BotWorldHelper.findLivingEntityByUUID(target.uuid);

                    if (living == null || living.isDead()) {
                        BotLogger.info(isLogged, bot.getId() + " ✅ Target is dead or unreachable.");
                        stop(); cancel(); return;
                    }

                    double distance = bot.getNPCEntity().getLocation().distance(living.getLocation());

                    // 🔄 Обновляем targetLocation
                    bot.getRuntimeStatus().setTargetLocation(BotCoordinate3DHelper.convertFrom(living.getLocation()));

                    BotUtils.lookAt(bot, BotCoordinate3DHelper.convertFrom(living.getLocation()));

                    if (distance > 1.0) {
                        bot.getNPCNavigator().getDefaultParameters().speedModifier(2.5F);

                        if (pursuitTicks % 20 == 0) {
                            bot.getNPCNavigator().getDefaultParameters().speedModifier(2.5F);
                            bot.getNPCNavigator().setTarget(living.getLocation());
                        }

                        bot.getNPCNavigator().setTarget(living.getLocation());

                        BotLogger.info(isLogged, bot.getId() + " 🚶 Pursuing mob, distance: " + String.format("%.2f", distance));



                    } else {
                        animateHand();
                        living.damage(damage, bot.getNPCEntity());
                        BotLogger.info(isLogged, bot.getId() + " ✋🏻 Attacked mob: " + living.getType());
                    }

                    if (++pursuitTicks > MAX_PURSUIT_TICKS) {
                        BotLogger.info(isLogged, bot.getId() + " ⏱️ Pursuit timeout reached.");
                        stop(); cancel(); return;
                    }

                }
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 10L);
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
    }

}
