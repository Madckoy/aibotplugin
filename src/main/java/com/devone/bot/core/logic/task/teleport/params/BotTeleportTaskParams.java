package com.devone.bot.core.logic.task.teleport.params;

import com.devone.bot.core.logic.task.params.BotLocationParams;
import com.devone.bot.utils.blocks.BotLocation;

public class BotTeleportTaskParams extends BotLocationParams {

    /**
     * Загружает параметры из JSON-файла.
     * Если файл отсутствует — создаётся дефолтный конфиг.
     * Если файл повреждён — выбрасывается исключение.
     */
    public BotTeleportTaskParams() {
        super();
        // Устанавливаем значения по умолчанию (на случай, если файл не загрузится)
        setIcon("🗲");
        setObjective("Teleport");

    }

    /**
     * Позволяет создать параметры и вручную переопределить точку телепортации.
     */
    public BotTeleportTaskParams(BotLocation loc) {
        this(); // загружаем всё остальное из JSON
        setLocation(loc);
    }

    @Override
    public String toString() {
        return "BotTeleportTaskParams{" +
                "location=" + getLocation() +
                ", icon=" + getIcon() +
                ", objective=" + getObjective() +
                '}';
    }
}
