package com.devone.bot.core.task.active.swim.params;

import com.devone.bot.core.task.passive.params.BotLocationParams;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotSwimTaskParams extends BotLocationParams {

    public BotSwimTaskParams() {
        super();
        setIcon("🌊");
        setObjective("Swim to target");
    }

    public BotSwimTaskParams(BotPosition position) {
        this();
        setPosition(position);
    }

    @Override
    public String toString() {
        return "BotSwimToTaskParams{" +
                "position=" + getPosition() +
                ", icon=" + getIcon() +
                ", objective=" + getObjective() +
                '}';
    }
}
