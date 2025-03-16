package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BotFollowTask extends PlayerLinkedTask {

    private static final double FOLLOW_DISTANCE = 2.5;
    private static final double MAX_FOLLOW_RADIUS = 15.0;

    private Location targetLocation;

    private long startTime = System.currentTimeMillis();

    public BotFollowTask(Bot bot, Player player) {
        super(bot, player, "FOLLOW");
    }

    @Override
    protected void executeTask() {
        Location botLocation = bot.getNPCCurrentLocation();
        targetLocation = player.getLocation();

        double distanceToPlayer = botLocation.distance(targetLocation);

        if (distanceToPlayer <= FOLLOW_DISTANCE) {
            return;
        }

        if (distanceToPlayer < MAX_FOLLOW_RADIUS) {
            if (!bot.getLifeCycle().getTaskStackManager().isTaskActive(BotMoveTask.class)) {
                BotMoveTask moveTask = new BotMoveTask(bot);
                moveTask.configure(targetLocation);
                bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);
                BotLogger.debug("🚶‍♂️ Бот " + bot.getId() + " идет за игроком " + player.getName());
            }
        }

        if (distanceToPlayer >= MAX_FOLLOW_RADIUS) {
            bot.getNPCEntity().teleport(targetLocation);
            BotLogger.debug("⚡ Бот " + bot.getId() + " телепортирован к игроку " + player.getName());
        }
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
