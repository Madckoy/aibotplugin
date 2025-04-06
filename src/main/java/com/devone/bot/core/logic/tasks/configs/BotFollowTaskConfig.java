package com.devone.bot.core.logic.tasks.configs;

import org.bukkit.configuration.file.FileConfiguration;

public class BotFollowTaskConfig extends BotLocationConfig {

    public BotFollowTaskConfig() {
        super(BotFollowTaskConfig.class.getSimpleName());
    }

    @Override
    public void generateDefaultConfig() {
        FileConfiguration config = getConfig();

        config.set("follow.follow_distance", 2.5); // Расстояние следования за игроком
        config.set("follow.attack_range", 10); // Дистанция атаки на мобов
        config.set("follow.chat_cooldown", 10); // Пауза между репликами (в секундах)
        config.set("follow.insult_chance", 0.3); // Вероятность ругани при атаке

        super.generateDefaultConfig();
    }

    public double getFollowDistance() {
        return getConfig().getDouble("follow.follow_distance", 2.5);
    }

    public double getAttackRange() {
        return getConfig().getDouble("follow.attack_range", 1.5);
    }

    public long getChatCooldown() {
        return getConfig().getLong("follow.chat_cooldown", 10) * 1000; // Преобразуем в миллисекунды
    }

    public double getInsultChance() {
        return getConfig().getDouble("follow.insult_chance", 0.3);
    }
}
