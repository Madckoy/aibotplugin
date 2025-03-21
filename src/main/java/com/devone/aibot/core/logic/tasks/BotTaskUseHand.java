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


public class BotTaskUseHand extends BotTask {

    private LivingEntity target;
    private int damage;

    public BotTaskUseHand(Bot bot) {
        super(bot, "👊");
        setObjective("Hitting the target");
    }

@Override
    public BotTask configure(Object... params) {
        super.configure(params);

        if (params.length == 1 && params[0] instanceof Location) {

            this.targetLocation = (Location) params[0];
        }
        if (params.length == 2 && params[1] instanceof LivingEntity) {

            this.target = (LivingEntity) params[1];
        }
        if (params.length == 3 && params[2] instanceof Integer) {

            this.damage = (int) params[2];

        } else {

            BotLogger.error(bot.getId() + " ❌ Некорректные параметры для `BotTaskUseHand`!");
            isDone = true;
        }

        super.configure(params);
        return this;
    }

    @Override
    public void executeTask() {
        
        setObjective("Target hit by hand: " + BotUtils.getBlockName(targetLocation.getBlock()));
        
        turnToBlock(targetLocation);

        destroyBlock(targetLocation);
        
        isDone = true;

    }

    private void turnToBlock(Location target) {
        Vector direction = target.toVector().subtract(bot.getNPCCurrentLocation().toVector()).normalize();
        bot.getNPCEntity().setRotation((float) Math.toDegrees(Math.atan2(-direction.getX(), direction.getZ())), 0);
        BotLogger.trace("🔄 Бот повернулся к блоку: " + BotStringUtils.formatLocation(target));
    }

    
    private void animateHand() {
        if (bot.getNPCEntity() instanceof Player) {
            Player playerBot = (Player) bot.getNPCEntity();
            playerBot.swingMainHand();
            BotLogger.trace("🤚 Анимация руки выполнена");
        }
    }

    private void destroyBlock(Location target) {
        Bukkit.getScheduler().runTask(AIBotPlugin.getInstance(), () -> {
            if (target.getBlock().getType() != Material.AIR &&
                target.getBlock().getType() != Material.WATER && 
                target.getBlock().getType() != Material.LAVA &&
                target.getBlock().getType() != Material.VOID_AIR ) {

                animateHand();

                if(target instanceof LivingEntity) {
                    ((LivingEntity)target).damage(damage);
                } else {
                    target.getBlock().breakNaturally();
                }

                BotLogger.debug("✅ Блок разрушен на " + BotStringUtils.formatLocation(target));
                isDone = false;
            } else {
                isDone = true;
            }
        });
    }

}
