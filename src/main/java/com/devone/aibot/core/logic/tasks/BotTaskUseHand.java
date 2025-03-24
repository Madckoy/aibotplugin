package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.AIBotPlugin;
import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;

public class BotTaskUseHand extends BotTask {

    private LivingEntity target;
    private int damage = 1;

    public BotTaskUseHand(Bot bot) {
        super(bot, "✋🏻");
        setObjective("Hit the target");
    }

    @Override
    public BotTask configure(Object... params) {
        super.configure(params);

        boolean hasParams = false;

        if (params.length > 0 && params[0] instanceof Location loc) {
            bot.getRuntimeStatus().setTargetLocation(loc);
            hasParams = true;
        }

        if (params.length > 1 && params[1] instanceof LivingEntity entity) {
            this.target = entity;
            hasParams = true;
        }

        if (params.length > 2 && params[2] instanceof Integer dmg) {
            this.damage = dmg;
            hasParams = true;
        }

        if (!hasParams) {
            BotLogger.error(bot.getId() + " ❌ Некорректные параметры для `BotTaskUseHand`: " + Arrays.toString(params));
            isDone = true;
        }

        return this;
    }

    @Override
    public void executeTask() {
        if (bot.getRuntimeStatus().getTargetLocation() == null && target == null) {
            BotLogger.error(bot.getId() + " ❌ Нет цели или координат для удара");
            isDone = true;
            return;
        }
    
        // ✅ Проверяем, если цель уже мертва — выходим (для атаки)
        if (target != null && target.isDead()) {
            BotLogger.debug(bot.getId() + " ☠️ Цель уже мертва. Завершаем атаку.");
            isDone = true;
            return;
        }
    
        Location faceTarget = (target != null) ? target.getLocation() : bot.getRuntimeStatus().getTargetLocation();

        setObjective("Hitting: " + BotUtils.getBlockName(faceTarget.getBlock())+" at "+BotStringUtils.formatLocation(faceTarget));
    
        turnToBlock(faceTarget);
    
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            animateHand();
    
            if (target != null && !target.isDead()) {
                target.damage(damage);
                BotLogger.debug(bot.getId() + " ✋🏻 Нанесён урон существу: " + target.getName());
            } else if (bot.getRuntimeStatus().getTargetLocation() != null && bot.getRuntimeStatus().getTargetLocation().getBlock().getType() != Material.AIR) {
                // ✅ Добавляем эффект разрушения перед ломанием блока
                BotUtils.playBlockBreakEffect(bot.getRuntimeStatus().getTargetLocation());
    
                bot.getRuntimeStatus().getTargetLocation().getBlock().breakNaturally();

                BotLogger.debug(bot.getId() + " ✅ Блок разрушен на " + BotStringUtils.formatLocation(bot.getRuntimeStatus().getTargetLocation()));
            } else {
                BotLogger.warn(bot.getId() + " ⚠️ Нечего разрушать");
            }
    
            isDone = true;
        });
    }

    private void turnToBlock(Location target) {
        Vector direction = target.toVector().subtract(bot.getRuntimeStatus().getCurrentLocation().toVector()).normalize();
        float yaw = (float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ()));

        bot.getNPCEntity().setRotation(yaw, 0);
        
        // ✅ Принудительно обновляем положение, если поворот сбрасывается
        Bukkit.getScheduler().runTaskLater(AIBotPlugin.getInstance(), () -> {
            bot.getNPCEntity().teleport(bot.getRuntimeStatus().getCurrentLocation());
        }, 1L); // ✅ Через тик, чтобы дать время на обновление

        BotLogger.debug("🔄 TURNING: " + bot.getId() + " | Yaw: " + yaw + " | Target: " + BotStringUtils.formatLocation(target));
    }

    private void animateHand() {
        if (bot.getNPCEntity() instanceof Player playerBot) {
            playerBot.swingMainHand();
            BotLogger.trace("🤚 Анимация руки выполнена");
        } else {
            BotLogger.trace("🤚 Анимация не выполнена: бот — не игрок");
        }
    }
}
