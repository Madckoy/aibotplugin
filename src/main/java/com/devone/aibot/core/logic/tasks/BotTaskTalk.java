package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.logic.tasks.configs.BotTaskTalkConfig;
import com.devone.aibot.utils.BotLogger;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Random;

public class BotTaskTalk extends BotTask {

    private final Player player;
    private final TalkType type;
    private final Random random = new Random();
    private static final BotTaskTalkConfig config = new BotTaskTalkConfig();

    public enum TalkType {
        COMPLIMENT, INSULT_MOB, ENVIRONMENT_COMMENT
    }

    public BotTaskTalk(Bot bot, Player player, TalkType type) {
        super(bot, "💬");
        this.player = player;
        this.type = type;
    }

    @Override
    public void executeTask() {
        
        String message = generateMessage();
        
        if (player == null) {
            message = "Игрок отсутствует!";
            setObjective("Говорит: "+message);
            isDone = true;
            return;
        }

        if (!message.isEmpty()) {
            setObjective("Собираюсь сказать глупость: "+message);
            player.sendMessage("🤖 " + bot.getId() + ": " + message);
            BotLogger.debug("💬 Бот сказал в чат: " + message);
        }
        
        isDone = true;
    }

    private String generateMessage() {
        switch (type) {
            case COMPLIMENT:
                return getRandomMessage(config.getCompliments());
            case INSULT_MOB:
                return getRandomMessage(config.getInsults());
            case ENVIRONMENT_COMMENT:
                return getRandomMessage(config.getEnvironmentComments());
            default:
                return "";
        }
    }

    private String getRandomMessage(List<String> messages) {
        if (messages.isEmpty()) return "";
        return messages.get(random.nextInt(messages.size()));
    }
}
