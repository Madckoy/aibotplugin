package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import java.util.List;

public class BotProtectTask extends PlayerLinkedTask {

    private static final double ATTACK_RANGE = 3.5;
    private static final double PROTECT_RADIUS = 10.0;
    private long startTime = System.currentTimeMillis();

    private Location targetLocation;

    public BotProtectTask(Bot bot, Player player) {
        super(bot, player, "PROTECT");
    }

    @Override
    protected void executeTask() {
        Location botLocation = bot.getNPCCurrentLocation();
        targetLocation = player.getLocation();

        if (botLocation.distance(targetLocation) > PROTECT_RADIUS) {
            if (!bot.getLifeCycle().getTaskStackManager().isTaskActive(BotMoveTask.class)) {
                BotMoveTask moveTask = new BotMoveTask(bot);
                moveTask.configure(targetLocation);
                bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);
                BotLogger.debug("🚶 Бот " + bot.getId() + " приближается к игроку для защиты.");
            }
            return;
        }

        List<Entity> nearbyEntities = player.getNearbyEntities(PROTECT_RADIUS, PROTECT_RADIUS, PROTECT_RADIUS);
        for (Entity entity : nearbyEntities) {
            if (entity instanceof Monster) {
                Monster mob = (Monster) entity;
                if (mob.getTarget() == player || mob.getLocation().distance(targetLocation) < ATTACK_RANGE) {
                    attackMob(mob);
                    return;
                }
            }
        }
    }

    private void attackMob(LivingEntity mob) {
        bot.getNPCNavigator().setTarget(mob, true);
        mob.damage(4.0, bot.getNPCEntity());
    }

    @Override
    public void configure(Object... params) {
        return;
    }

    @Override
    public String getName() {
        return name;
    }

    public Location getTargetLocation() {
        return targetLocation;
    }

    @Override
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }
}
