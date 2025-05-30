package com.devone.bot.core.task.active.hand.attack;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.Bot;
import com.devone.bot.core.task.active.hand.BotHandTask;
import com.devone.bot.core.task.active.hand.attack.listener.BotHandAttackListener;
import com.devone.bot.core.task.active.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.BotUtils;
import com.devone.bot.core.utils.blocks.BotBlockData;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.world.BotWorldHelper;

public class BotHandAttackTask extends BotHandTask<BotHandAttackTaskParams> {

    private double damage = BotConstants.DEFAULT_HAND_DAMAGE;
    private BukkitTask bukkitTask;
    private BotHandAttackListener listener;
    private long hits = 0;
    private long attempts = 0;
    private BotPosition startPos = null;
    private int pursuitTicks = 0;

    private final int MAX_PURSUIT_TICKS = 120;
    private final int MAX_ATTEMPTS = 120;

    public BotHandAttackTask(Bot bot) {

        super(bot, BotHandAttackTaskParams.class);

        attempts = 0;
        hits = 0;
        startPos = new BotPosition(bot.getNavigator().getPosition());
    }

    public BotHandAttackTask setParams(BotHandAttackTaskParams params) {
        super.setParams(params); // вызовет BotHandTask.setParams()

        setIcon(params.getIcon());
        setObjective(params.getObjective());
        this.damage = params.getDamage();        

        BotLogger.debug(icon, isLogged(), bot.getId() + " ✅ Parameters for BotHandAttackTask set.");
        return this;
    }

    public void execute() {
        super.execute();
        BotLogger.debug(icon, isLogged(), bot.getId() + " 🔶 Executing BotHandAttackTask");

        if (getTarget() == null) {
            BotLogger.debug(icon, isLogged(), bot.getId() + " ❌ Target is null.");
            this.stop();
            return;
        }

        if (listener == null) {
            listener = new BotHandAttackListener(this);
            Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
        }

        BotHandAttackTask haTask = this;

        final int COOLDOWN_TICKS = 10; // задержка между атаками
        final int TURN_INTERVAL = 5;
        final int PURSUIT_DISTANCE_TICKS = 20;

        final int[] attackCooldown = {0}; // оборачиваем, чтобы использовать в лямбде

        bukkitTask = new BukkitRunnable() {
            
            @Override
            public void run() {
                if (done || bot.getNPCEntity() == null) {
                    BotLogger.debug(icon, isLogged(), bot.getId() + " ❌ Task is done or Bot NPC is null.");
                    stop();
                    cancel();
                    return;
                }

                setObjective(params.getObjective() + " " + getTarget().getType() + " at " + getTarget().getPosition().toCompactString());
                attempts++;

                // Работаем через UUID
                if (getTarget().getUUID() != null) {
                    LivingEntity living = BotWorldHelper.findLivingEntityByUUID(getTarget().getUUID());

                    if (living == null || living.isDead() || living.getHealth() <= 0) {
                        BotLogger.debug(icon, isLogged(), bot.getId() + " 💀 Target is dead or unreachable.");
                        getTarget().setUUID(null);
                        setTarget(null);
                        bot.getNavigator().setTarget(null);
                        stop();
                        cancel();
                        return;
                    }

                    BotPosition  pos = BotWorldHelper.locationToBotPosition(living.getLocation());
                    BotBlockData bl = BotWorldHelper.blockToBotBlockData(living.getLocation().getBlock());

                    // Устанавливаем цель для логики навигации и NPC
                    bot.getNavigator().setTarget(bl);
                    bot.getNPCNavigator().setTarget(living.getLocation());

                    double distance = bot.getNPCEntity().getLocation().distance(living.getLocation());

                    // ⏱️ Проверка поворота к цели
                    if (pursuitTicks % TURN_INTERVAL == 0) {
                        BotUtils.turnToTargetSync(haTask, bot, pos);
                    }

                    // 🏃 Преследование
                    if (distance > 2.0) {
                        bot.getNPCNavigator().getDefaultParameters().speedModifier(2.5F);

                        if (pursuitTicks % PURSUIT_DISTANCE_TICKS == 0) {
                            bot.getNPCNavigator().setTarget(living.getLocation());
                            bot.getNavigator().setTarget(BotWorldHelper.blockToBotBlockData(living.getLocation().getBlock()));

                            BotLogger.debug(icon, isLogged(),
                                    bot.getId() + " 🏃🏻‍➡️ Correcting direction. Distance: " + String.format("%.2f", distance));
                        }

                        BotLogger.debug(icon, isLogged(),
                                bot.getId() + " 🏃🏻‍➡️ Pursuing mob, distance: " + String.format("%.2f", distance));
                    } else {
                        // ⚔️ Удар с задержкой
                        if (attackCooldown[0] <= 0) {
                            animateHandSync(haTask, bot);
                            living.damage(damage, bot.getNPCEntity());
                            hits++;

                            BotLogger.debug(icon, isLogged(), bot.getId() + " ⚔️ Attacked mob: " + living.getType());
                            attackCooldown[0] = COOLDOWN_TICKS;
                        } else {
                            attackCooldown[0]--;
                        }
                    }

                    // ❌ Условия остановки
                    if (++pursuitTicks > MAX_PURSUIT_TICKS) {
                        BotLogger.debug(icon, isLogged(), bot.getId() + " ⏱️ Pursuit timeout reached.");
                        stop();
                        cancel();
                    } else if (attempts > MAX_ATTEMPTS) {
                        BotPosition endPos = bot.getNavigator().getTarget().getPosition();
                        if (endPos.equals(startPos) && hits == 0) {
                            BotLogger.debug(icon, isLogged(), bot.getId() + " ⏱️ Seems like bot is stuck.");
                            stop();
                            cancel();
                        }
                    }
                }
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 1L);
    }


    @Override
    public void stop() {
        if (listener != null) {
            listener.unregister();
            listener = null;
        }

        if (bukkitTask != null) {
            bukkitTask.cancel();
            bukkitTask = null;
        }

        BotLogger.debug(icon, isLogged(), bot.getId() + " ✍🏼 Hits made: " + hits);
        BotLogger.debug(icon, isLogged(), bot.getId() + " ✍🏼 Attempts made: " + attempts);
        BotLogger.debug(icon, isLogged(), bot.getId() + " ⛔ BotHandAttackTask: Task is stopped");

        super.stop();
    }
}
