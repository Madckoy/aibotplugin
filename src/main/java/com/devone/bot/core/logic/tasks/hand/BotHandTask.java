package com.devone.bot.core.logic.tasks.hand;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;

import org.bukkit.entity.LivingEntity;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.bot.Bot;
import com.devone.bot.core.logic.tasks.BotTask;
import com.devone.bot.core.logic.tasks.hand.listeners.BotKillListener;
import com.devone.bot.core.logic.tasks.hand.params.BotHandTaskParams;
import com.devone.bot.core.logic.tasks.params.BotTaskParams;
import com.devone.bot.core.logic.tasks.params.IBotTaskParams;
import com.devone.bot.utils.BotUtils;
import com.devone.bot.utils.blocks.BotBlockData;
import com.devone.bot.utils.blocks.BotCoordinate3DHelper;
import com.devone.bot.utils.logger.BotLogger;
import com.devone.bot.utils.world.BotWorldHelper;

public class BotHandTask extends BotTask {

    private BotBlockData target;
    private double damage = 5.0;
    private boolean isLogged = true;
    private BukkitTask bukkitTask;
    private BotKillListener listener;

    private int pursuitTicks = 0;
    private final int MAX_PURSUIT_TICKS = 60; // ~3 секунды (если тик каждые 10л)

    public BotHandTask(Bot bot) {
        super(bot, "✋🏻");
        setObjective("Hit the target");
    }

    public BotHandTask(Bot bot, String name) {
        super(bot, name);
        setObjective("Hit the target");
    }

    @Override
    public BotHandTask configure(IBotTaskParams params) {
        super.configure((BotTaskParams) params);

        if (params instanceof BotHandTaskParams handParams) {
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
        if (target == null) {
            BotLogger.info(isLogged, bot.getId() + " ❌ Target is null.");
            this.stop();
            return;
        }

        if (listener == null) {
            listener = new BotKillListener(this);
            Bukkit.getPluginManager().registerEvents(listener, AIBotPlugin.getInstance());
        }

        bukkitTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (isDone || bot.getNPCEntity() == null) {
                    stop(); cancel(); return;
                }

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

                } else {
                    // 🧱 Работа с блоком
                    Block block = BotWorldHelper.getBlockAt(target);
                    if (block == null || block.getType() == Material.AIR) {
                        BotLogger.info(isLogged, bot.getId() + " ✅ Block destroyed.");
                        stop(); cancel(); return;
                    }

                    animateHand();
                    BotUtils.playBlockBreakEffect(block.getLocation());
                    block.breakNaturally();
                    bot.getRuntimeStatus().brokenBlocksIncrease();

                    BotLogger.info(isLogged, bot.getId() + " 🧱 Block destroyed: " + block.getType());
                }
            }
        }.runTaskTimer(AIBotPlugin.getInstance(), 0L, 10L);
    }

    @Override
    public void stop() {
        isDone = true;
        bot.getRuntimeStatus().setTargetLocation(null);

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
