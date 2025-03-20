package com.devone.aibot.core.logic.tasks;

import java.util.Set;

import org.bukkit.Material;

import com.devone.aibot.core.Bot;
import com.devone.aibot.utils.BotLogger;

public class BotTaskBreakBlockAny extends BotTaskBreakBlock {
    
    public BotTaskBreakBlockAny(Bot bot) {
        super(bot);
        setName("🪓");

        setTargetMaterials(null);

    }
 

}
