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
        super(bot, "👊");
        setObjective("Hitting the target");
    }

    @Override
    public BotTask configure(Object... params) {
        super.configure(params);

        boolean hasParams = false;

        if (params.length > 0 && params[0] instanceof Location loc) {
            this.targetLocation = loc;
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
        if (targetLocation == null && target == null) {
            BotLogger.error(bot.getId() + " ❌ Нет цели или координат для удара");
            isDone = true;
            return;
        }
    
        // ✅ Проверяем смерть только если это атака (target != null)
        if (target != null && target.isDead()) {
            BotLogger.debug(bot.getId() + " ☠️ Цель уже мертва. Завершаем атаку.");
            isDone = true;
            return;
        }
    
        Location faceTarget = (target != null) ? target.getLocation() : targetLocation;
        setObjective("Target hit by hand: " + BotStringUtils.formatLocation(faceTarget));
    
        turnToBlock(faceTarget);
    
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            animateHand();
    
            // 🔥 Если это атака, проверяем ещё раз (цель могла умереть за задержку)
            if (target != null) {
                if (!target.isDead()) {
                    target.damage(damage);
                    BotLogger.debug(bot.getId() + " 👊 Нанесён урон существу: " + target.getName());
                } else {
                    BotLogger.debug(bot.getId() + " ☠️ Цель умерла в процессе атаки. Завершаем.");
                }
            } 
            // 🛠️ Если это добыча, просто ломаем блок
            else if (targetLocation != null && targetLocation.getBlock().getType() != Material.AIR) {
                targetLocation.getBlock().breakNaturally();
                BotLogger.debug(bot.getId() + " ✅ Блок разрушен на " + BotStringUtils.formatLocation(targetLocation));
            } else {
                BotLogger.warn(bot.getId() + " ⚠️ Нечего разрушать");
            }
    
            isDone = true;
        });
    }
    

    private void turnToBlock(Location target) {
        Vector direction = target.toVector().subtract(bot.getNPCCurrentLocation().toVector()).normalize();
        bot.getNPCEntity().setRotation((float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ())), 0);
        BotLogger.trace("🔄 Бот повернулся к цели: " + BotStringUtils.formatLocation(target));
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
