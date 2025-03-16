package com.devone.aibot.core.logic.tasks;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;

public class BotTaskDropAll extends BotTaskPlayerLinked {

    private long startTime = System.currentTimeMillis();

    public BotTaskDropAll(Bot bot, Player player) {
        super(bot, player, "DROP-ALL");
    }

    @Override
    public void configure(Object... params) {
        startTime = System.currentTimeMillis();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getTargetLocation() {
        return null;
    }

    @Override
    public long getElapsedTime() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    protected void executeTask() {
        BotInventory.dropAllItems(bot);
    }

}