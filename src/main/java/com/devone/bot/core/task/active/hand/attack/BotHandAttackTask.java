package com.devone.bot.core.task.active.hand.attack;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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

    private BotPosition lastKnownTargetPos = null;
    private int stationaryTicks = 0;
    private final int MAX_STATIONARY_TICKS = 40;

    public BotHandAttackTask(Bot bot) {
        super(bot, BotHandAttackTaskParams.class);
        this.attempts = 0;
        this.hits = 0;
        this.startPos = new BotPosition(bot.getNavigator().getPosition());
    }

    public BotHandAttackTask setParams(BotHandAttackTaskParams params) {
        super.setParams(params);
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
            stop();
            return;
        }

        if (listener == null) {
            listener = new BotHandAttackListener(this);
            Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
        }

        final int COOLDOWN_TICKS = 10;
        final int TURN_INTERVAL = 5;
        final int PURSUIT_DISTANCE_TICKS = 20;
        final int[] attackCooldown = {0};

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (done || bot.getNPCEntity() == null) {
                    BotLogger.debug(icon, isLogged(), bot.getId() + " ❌ Task is done or Bot NPC is null.");
                    stop();
                    cancel();
                    return;
                }

                if (getTarget().getUUID() == null) {
                    BotLogger.debug(icon, isLogged(), bot.getId() + " ❌ Target UUID is null.");
                    cleanupTarget();
                    stop();
                    cancel();
                    return;
                }

                LivingEntity living = BotWorldHelper.findLivingEntityByUUID(getTarget().getUUID());
                if (living == null || living.isDead() || living.getHealth() <= 0 || living.isInWater()) {
                    BotLogger.debug(icon, isLogged(), bot.getId() + " 💀 Target is dead or unreachable.");
                    cleanupTarget();
                    stop();
                    cancel();
                    return;
                }

                Location currentLoc = living.getLocation();
                BotPosition currentPos = BotWorldHelper.locationToBotPosition(currentLoc);

                // Проверка движения цели
                if (lastKnownTargetPos != null && currentPos.equals(lastKnownTargetPos)) {
                    stationaryTicks++;
                    if (stationaryTicks > MAX_STATIONARY_TICKS) {
                        BotLogger.debug(icon, isLogged(), bot.getId() + " ⏸️ Target hasn't moved for " + stationaryTicks + " ticks. Canceling.");
                        cleanupTarget();
                        stop();
                        cancel();
                        return;
                    }
                } else {
                    stationaryTicks = 0;
                    lastKnownTargetPos = currentPos;
                }

                BotBlockData bl = BotWorldHelper.blockToBotBlockData(currentLoc.getBlock());

                // Обновление цели для навигации
                bot.getNavigator().setTarget(bl);
                bot.getNPCNavigator().setTarget(currentLoc);
                setObjective(params.getObjective() + " " + living.getType() + " at " + currentPos.toCompactString());

                double distance = bot.getNPCEntity().getLocation().distance(currentLoc);

                if (pursuitTicks % TURN_INTERVAL == 0) {
                    BotUtils.turnToTargetSync(BotHandAttackTask.this, bot, currentPos);
                }

                if (distance > 2.0) {
                    bot.getNPCNavigator().getDefaultParameters().speedModifier(2.5F);

                    if (pursuitTicks % PURSUIT_DISTANCE_TICKS == 0) {
                        bot.getNPCNavigator().setTarget(currentLoc);
                        bot.getNavigator().setTarget(bl);
                        BotLogger.debug(icon, isLogged(), bot.getId() + " 🏃🏻‍➡️ Correcting direction. Distance: " + String.format("%.2f", distance));
                    }

                    BotLogger.debug(icon, isLogged(), bot.getId() + " 🏃🏻‍➡️ Pursuing mob, distance: " + String.format("%.2f", distance));
                } else {
                    if (attackCooldown[0] <= 0) {
                        animateHandSync(BotHandAttackTask.this, bot);
                        living.damage(damage, bot.getNPCEntity());
                        hits++;
                        BotLogger.debug(icon, isLogged(), bot.getId() + " ⚔️ Attacked mob: " + living.getType());
                        attackCooldown[0] = COOLDOWN_TICKS;
                    } else {
                        attackCooldown[0]--;
                    }
                }

                pursuitTicks++;
                attempts++;

                if (pursuitTicks > MAX_PURSUIT_TICKS) {
                    BotLogger.debug(icon, isLogged(), bot.getId() + " ⏱️ Pursuit timeout reached.");
                    cleanupTarget();
                    stop();
                    cancel();
                    return;
                }

                if (attempts > MAX_ATTEMPTS && hits == 0) {
                    BotLogger.debug(icon, isLogged(), bot.getId() + " ⏱️ Too many attempts without success. Canceling.");
                    cleanupTarget();
                    stop();
                    cancel();
                }
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 1L);
    }

    private void cleanupTarget() {
        if (getTarget() != null) {
            getTarget().setUUID(null);
        }
        setTarget(null);
        bot.getNavigator().setTarget(null);
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
