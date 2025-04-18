package com.devone.bot.core.bot.behaviour.task.hand.attack;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.bot.behaviour.task.hand.BotHandTask;
import com.devone.bot.core.bot.behaviour.task.hand.attack.listener.BotHandAttackListener;
import com.devone.bot.core.bot.behaviour.task.hand.attack.params.BotHandAttackTaskParams;
import com.devone.bot.core.bot.brain.logic.utils.BotConstants;
import com.devone.bot.core.bot.brain.logic.utils.BotUtils;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotBlockData;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocation;
import com.devone.bot.core.bot.brain.logic.utils.blocks.BotLocationHelper;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;
import com.devone.bot.core.bot.brain.logic.utils.world.BotWorldHelper;

public class BotHandAttackTask extends BotHandTask<BotHandAttackTaskParams> {

    private BotBlockData target;
    private double damage = BotConstants.DEFAULT_HAND_DAMAGE;
    private BukkitTask bukkitTask;
    private BotHandAttackListener listener;
    private long hits = 0;
    private long attempts = 0;
    private BotLocation startPos = null;
    private int pursuitTicks = 0;

    private final int MAX_PURSUIT_TICKS = 120;
    private final int MAX_ATTEMPTS = 120;

    public BotHandAttackTask(Bot bot) {

        super(bot, BotHandAttackTaskParams.class);

        attempts = 0;
        hits = 0;
        startPos = new BotLocation(bot.getNavigation().getLocation());
    }

    public BotHandAttackTask setParams(BotHandAttackTaskParams params) {
        super.setParams(params); // вызовет BotHandTask.setParams()

        setIcon(params.getIcon());
        setObjective(params.getObjective());

        this.target = params.getTarget();
        this.damage = params.getDamage();

        bot.getNavigation().setTarget(target);

        BotLogger.debug("✅", isLogging(), bot.getId() + " Parameters for BotHandAttackTask set.");
        return this;
    }

    public void execute() {

        super.execute();

        BotLogger.debug("🔶", isLogging(), bot.getId() + " Executing BotHandAttackTask");

        if (target == null) {
            BotLogger.debug("❌", isLogging(), bot.getId() + " BotHandAttackTask: Target is null.");
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
                    BotLogger.debug("❌", isLogging(),
                            bot.getId() + " BotHandAttackTask: Task is done or Bot NPC is null.");
                    stop();
                    cancel();
                    return;
                }

                setObjective(params.getObjective() + " " + target.getType() + " (" + target.getX() + ", "
                        + target.getY() + ", " + target.getZ() + ")");

                attempts++;

                // 🧠 Работа с мобом по UUID
                if (target.getUUID() != null) {
                    LivingEntity living = BotWorldHelper.findLivingEntityByUUID(target.getUUID());

                    if (living == null || living.isDead() || living.getHealth() <= 0) {
                        BotLogger.debug("💀", isLogging(), bot.getId() + " Target is dead or unreachable.");
                        target.setUUID(null);
                        target = null;
                        bot.getNavigation().setTarget(null);
                        stop();
                        cancel();
                        return;
                    }

                    // 🔄 Обновляем targetLocation
                    bot.getNavigation().setTarget(BotLocationHelper.convertFrom(living.getLocation()));
                    BotUtils.lookAt(bot, BotLocationHelper.convertFrom(living.getLocation()));
                    bot.getNPCNavigator().setTarget(living.getLocation());
                    double distance = bot.getNPCEntity().getLocation().distance(living.getLocation());

                    if (distance > 2.0) {
                        bot.getNPCNavigator().getDefaultParameters().speedModifier(2.5F);

                        if (pursuitTicks % 20 == 0) {
                            bot.getNPCNavigator().setTarget(living.getLocation());
                            bot.getNavigation()
                                    .setTarget(BotLocationHelper.convertFrom(living.getLocation()));
                            BotUtils.lookAt(bot, BotLocationHelper.convertFrom(living.getLocation()));
                            BotLogger.debug("🏃🏻‍➡️", isLogging(),
                                    bot.getId() + " Pursuing mob, correcting direction. Distance: "
                                            + String.format("%.2f", distance));
                        }

                        BotLogger.debug("🏃🏻‍➡️", isLogging(),
                                bot.getId() + " Pursuing mob, distance: " + String.format("%.2f", distance));

                    } else {
                        animateHand();
                        living.damage(damage, bot.getNPCEntity());
                        hits++;

                        BotLogger.debug("⚔️", isLogging(), bot.getId() + " Attacked mob: " + living.getType());
                    }

                    if (++pursuitTicks > MAX_PURSUIT_TICKS) {
                        BotLogger.debug("⏱️", isLogging(), bot.getId() + " Pursuit timeout reached.");
                        stop();
                        cancel();
                        return;
                    }

                    if (attempts > MAX_ATTEMPTS) { // застряли
                        BotLocation endPos = bot.getNavigation().getLocation();
                        if (endPos.equals(startPos) && hits == 0) {
                            BotLogger.debug("⏱️", isLogging(), bot.getId() + " ⏱️ Seems like the bot got stuck.");
                            bot.getState().setStuck(true);
                            stop();
                            cancel();
                            return;
                        }
                    }
                }
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 10L);
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

        BotLogger.debug("✍🏼", isLogging(), bot.getId() + " Hits made: " + hits);
        BotLogger.debug("✍🏼", isLogging(), bot.getId() + " Attempts made: " + attempts);
        BotLogger.debug("⛔", isLogging(), bot.getId() + " BotHandAttackTask: Task is stopped");

        super.stop();
    }
}
