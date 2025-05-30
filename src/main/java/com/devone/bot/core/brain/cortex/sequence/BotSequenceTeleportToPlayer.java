package com.devone.bot.core.brain.cortex.sequence;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.devone.bot.core.Bot;
import com.devone.bot.core.brain.cortex.sequence.params.BotSequenceTeleportToPlayerParams;
import com.devone.bot.core.task.passive.BotSequenceContainer;
import com.devone.bot.core.task.passive.BotTask;
import com.devone.bot.core.task.active.teleport.BotTeleportTask;
import com.devone.bot.core.task.active.teleport.params.BotTeleportTaskParams;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotSequenceTeleportToPlayer
        extends BotSequenceContainer<BotSequenceTeleportToPlayerParams> {

    private final Player player;

    public BotSequenceTeleportToPlayer(Bot bot, Player player) {
        super(bot, BotSequenceTeleportToPlayerParams.class);
        this.player = player;
        //setIcon("🔀");
        setObjective("Sequence: Телепорт к игроку (не вплотную)");
        //setDeffered(true);
    }

    @Override
    protected List<BotTask<?>> enqueue(Bot bot) {
        Location playerLoc = player.getLocation();

        // 📏 Смещаемся на 2 блока назад по направлению взгляда
        // Vector offset = playerLoc.getDirection().normalize().multiply(-2);
        // Location behind = playerLoc.clone().add(offset);

        BotTeleportTask tp = new BotTeleportTask(bot, player);
        BotTeleportTaskParams params = tp.getParams();
        params.setPosition(new BotPosition(
                playerLoc.getBlockX(),
                playerLoc.getBlockY(),
                playerLoc.getBlockZ()));

        tp.setParams(params);
        tp.setIcon("⚡");
        tp.setObjective("Teleport behind a player");

        List<BotTask<?>> subtasks = new ArrayList<>();
        subtasks.add(tp);
        return subtasks;
    }
}
