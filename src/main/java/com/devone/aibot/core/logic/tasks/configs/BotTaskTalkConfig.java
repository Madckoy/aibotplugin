package com.devone.aibot.core.logic.tasks.configs;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;

public class BotTaskTalkConfig extends BotTaskConfig {

    public BotTaskTalkConfig() {
        super("BotTaskTalk.yml");
    }

    @Override
    public void generateDefaultConfig() {
        FileConfiguration config = getConfig();

        config.set("talk.compliments", List.of(
            "Ты выглядишь великолепно сегодня! 😎",
            "Босс, ты просто легенда! 🔥",
            "Я бы и дальше ходил за тобой, ты лучший лидер!",
            "Ты делаешь этот мир лучше! ✨",
            "Я всегда за тобой! 😉"
        ));

        config.set("talk.insults", List.of(
            "Эй, ты, твои движения как у черепахи! 🐢",
            "Я отлуплю вас всех, даже не напрягаясь! 😆",
            "Стоять, куда пошел!",
            "Ха, это всё, на что ты способен?!"
        ));

        config.set("talk.environment", List.of(
            "Тут красивый вид, но мне не до пейзажей! 🌄",
            "Этот лес мне кажется подозрительным... 🌲👀",
            "Я чувствую запах приключений... или это зомби? 🤢",
            "Что-то тихо... слишком тихо... 🧐",
            "Небо сегодня такое звёздное! 🌌"
        ));

        super.generateDefaultConfig();
    }

    public List<String> getCompliments() {
        return getConfig().getStringList("talk.compliments");
    }

    public List<String> getInsults() {
        return getConfig().getStringList("talk.insults");
    }

    public List<String> getEnvironmentComments() {
        return getConfig().getStringList("talk.environment");
    }
}
