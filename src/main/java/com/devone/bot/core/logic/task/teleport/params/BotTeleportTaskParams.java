package com.devone.bot.core.logic.task.teleport.params;

import com.devone.bot.core.logic.task.params.BotLocationParams;
import com.devone.bot.utils.blocks.BotLocation;

public class BotTeleportTaskParams extends BotLocationParams {

    // Всегда создаём объект по умолчанию, даже если потом перезапишем из файла
    private BotLocation location = new BotLocation(0, 0, 0);

    /**
     * Загружает параметры из JSON-файла.
     * Если файл отсутствует — создаётся дефолтный конфиг.
     * Если файл повреждён — выбрасывается исключение.
     */
    public BotTeleportTaskParams() {
        // Устанавливаем значения по умолчанию (на случай, если файл не загрузится)
        setIcon("🗲");
        setObjective("Teleport");

        // Загружаем параметры из файла
        BotTeleportTaskParams loaded = loadOrCreate(BotTeleportTaskParams.class);

        // Копируем значения без проверок — файл должен быть корректным
        this.location = loaded.location;
        setIcon(loaded.getIcon());
        setObjective(loaded.getObjective());
    }

    /**
     * Позволяет создать параметры и вручную переопределить точку телепортации.
     */
    public BotTeleportTaskParams(BotLocation loc) {
        this(); // загружаем всё остальное из JSON
        this.location = loc; // но переопределяем целевую точку вручную
    }

    public BotLocation getLocation() {
        return location;
    }

    public void setLocation(BotLocation loc) {
        this.location = loc;
    }
}
