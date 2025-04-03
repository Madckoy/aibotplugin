package com.devone.aibot.core.logic.tasks.configs;

import org.bukkit.configuration.file.FileConfiguration;
import java.util.List;

public class BotTalkTaskConfig extends BotTaskConfig {

    public BotTalkTaskConfig() {
        super(BotTalkTaskConfig.class.getSimpleName());
    }

    @Override
    public void generateDefaultConfig() {
        FileConfiguration config = getConfig();

        config.set("talk.compliments", List.of(
            "You look great today! 😎",
            "Boss, you’re a legend! 🔥",
            "Following you is the best decision I’ve made!",
            "You make this world a better place! ✨",
            "I always got your back! 😉"
        ));

        config.set("talk.insults", List.of(
            "Hey, you move like a turtle! 🐢",
            "I can take all of you down without even trying! 😆",
            "Stand still! Where do you think you're going?",
            "Ha! Is that all you've got?!"
        ));

        config.set("talk.environment", List.of(
            "Nice view, but I have work to do! 🌄",
            "This forest looks suspicious... 🌲👀",
            "I smell adventure… or is that a zombie? 🤢",
            "It’s too quiet… too quiet… 🧐",
            "The stars are beautiful tonight! 🌌"
        ));

        config.set("talk.inventory", List.of(
            "I have {count} items in my inventory.",
            "Checking inventory: {count} items stored.",
            "I've gathered {count} items, but I want more!"
        ));

        config.set("talk.help", List.of(
            "I'm stuck! Somebody, help!",
            "SOS! I’m trapped! 🆘",
            "I need backup! ASAP!",
            "Okay… this is bad. HELP!"
        ));


        config.set("talk.need_tool", List.of(
            "I need a poroper tool to break this block!"
        ));

        config.set("talk.self_talk", List.of(
            "I wonder… who am I, really? 🤖",
            "If I had emotions, I’d… dig deeper.",
            "This block is looking at me… or am I imagining things?",
            "Nobody understands me… not even myself.",
            "I want to be a Tesla Robot. ⚡🤖"
        ));

        super.generateDefaultConfig();
    }

    public List<String> getCompliments() { return getConfig().getStringList("talk.compliments"); }
    public List<String> getInsults() { return getConfig().getStringList("talk.insults"); }
    public List<String> getEnvironmentComments() { return getConfig().getStringList("talk.environment"); }
    public List<String> getInventoryReports() { return getConfig().getStringList("talk.inventory"); }
    public List<String> getHelpRequests() { return getConfig().getStringList("talk.help"); }
    public List<String> getSelfTalks() { return getConfig().getStringList("talk.self_talk"); }
    public List<String> getToolRequests() { return getConfig().getStringList("talk.need_tool"); }
}
