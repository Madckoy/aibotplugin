package com.devone.bot.core.task.active.teleport.params;

import com.devone.bot.core.task.passive.params.BotLocationParams;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotTeleportTaskParams extends BotLocationParams {

    /**
     * Загружает параметры из JSON-файла.
     * Если файл отсутствует — создаётся дефолтный конфиг.
     * Если файл повреждён — выбрасывается исключение.
     */
    public BotTeleportTaskParams() {
        super();
        // Устанавливаем значения по умолчанию (на случай, если файл не загрузится)
        setIcon("⚡");
        setObjective("Teleport");

    }

    /**
     * Позволяет создать параметры и вручную переопределить точку телепортации.
     */
    public BotTeleportTaskParams(BotPosition loc) {
        this(); // загружаем всё остальное из JSON
        setPosition(loc);
    }

    @Override
    public String toString() {
        return "BotTeleportTaskParams{" +
                "location=" + getPosition() +
                ", icon=" + getIcon() +
                ", objective=" + getObjective() +
                '}';
    }
}
