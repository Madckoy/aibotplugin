package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class BotTaskFollow extends BotTaskPlayerLinked {

    private static final double FOLLOW_DISTANCE = 2.5;
    private static final double MAX_FOLLOW_RADIUS = 15.0;

    private Location targetLocation;

    private long startTime = System.currentTimeMillis();

    public BotTaskFollow(Bot bot, Player player) {
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
            if (!bot.getLifeCycle().getTaskStackManager().isTaskActive(BotTaskMove.class)) {
                BotTaskMove moveTask = new BotTaskMove(bot);
                moveTask.configure(targetLocation);
                bot.getLifeCycle().getTaskStackManager().pushTask(moveTask);
                BotLogger.info("🚶‍ " +  bot.getId() + " Идет за игроком " + player.getName());
            }
        }

        if (distanceToPlayer >= MAX_FOLLOW_RADIUS) {
            bot.getNPCEntity().teleport(targetLocation);
            BotLogger.info("⚡ " + bot.getId() + " Телепортирован к игроку " + player.getName());
        }
    }

    @Override
    public void configure(Object... params) {
        startTime = System.currentTimeMillis();
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
