package com.devone.aibot.core.logic.tasks;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;

import com.devone.aibot.core.Bot;

import com.devone.aibot.utils.BotEnv3DScan;


public class BotTaskSonar3D extends BotTask {

    private BotTask parent;
    private int radius;
    private int height;

    public BotTaskSonar3D(Bot bot, BotTask caller, int radius, int height) {
        super(bot, "ᯤ");
        parent = caller;
        this.radius = radius;
        this.height = height;
    }

    @Override
    public void executeTask() {
        
        Map<Location, Material> env_map = BotEnv3DScan.scan3D(bot, radius, height);

        parent.setEnvMap(env_map);

        isDone = true;
    }

}