package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.core.comms.BotCommunicator;
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

    private BotCommunicator communicator; // Ссылка на BotCommunicator

    public enum TalkType {
        COMPLIMENT, INSULT_MOB, ENVIRONMENT_COMMENT,
        INVENTORY_REPORT, HELP_REQUEST, TOOL_REQUEST, SELF_TALK
    }

    public BotTaskTalk(Bot bot, Player player, TalkType type) {
        super(bot, "💬");
        this.player = player;
        this.type = type;
        this.communicator = new BotCommunicator(bot); // Инициализация BotCommunicator
    }

    @Override
    public void executeTask() {
        String message = generateMessage();
    
        if (message.isEmpty()) {
            isDone = true;
            return;
        }
    
        // ✅ Показываем в мониторинге всегда, даже если бот молчит
        setObjective("Размышляет: " + message);
    
        // 🤐 Бот занят рукой? Не говорим в чат, но оставляем в Objective
        if (bot.getActiveTask() instanceof BotTaskUseHand) {
            BotLogger.debug(bot.getId() + " 🤐 Занят рукой, не говорит: " + message);
            isDone = true;
            return;
        }
    
        // 🎯 Если есть игрок — говорим персонально
        if (player != null) {
            communicator.sendMessageToPlayer(message);  // Используем BotCommunicator
        }
        // 📣 Если нужно вещать в общий чат
        else if (shouldBroadcastToAll(type)) {
            communicator.broadcastMessage(message);  // Используем BotCommunicator
        }
        // 🤫 Иначе просто бурчим себе под нос (логируем)
        else {
            BotLogger.debug(bot.getId() + " бурчит себе под нос: " + message);
        }
    
        isDone = true;
    }

    private boolean shouldBroadcastToAll(TalkType type) {
        return switch (type) {
            case ENVIRONMENT_COMMENT, INVENTORY_REPORT, HELP_REQUEST, TOOL_REQUEST -> true;
            default -> false;
        };
    }

    private String generateMessage() {
        return switch (type) {
            case COMPLIMENT -> getRandomMessage(config.getCompliments());
            case INSULT_MOB -> getRandomMessage(config.getInsults());
            case ENVIRONMENT_COMMENT -> getRandomMessage(config.getEnvironmentComments());
            case INVENTORY_REPORT -> {
                String raw = getRandomMessage(config.getInventoryReports());
                yield raw.replace("{count}", String.valueOf(BotInventory.getTotalItemCount(bot)));
            }
            case HELP_REQUEST -> getRandomMessage(config.getHelpRequests());
            case TOOL_REQUEST -> getRandomMessage(config.getToolRequests());
            case SELF_TALK -> getRandomMessage(config.getSelfTalks());
        };
    }

    private String getRandomMessage(List<String> messages) {
        if (messages == null || messages.isEmpty()) return "🤖 ...";
        return messages.get(random.nextInt(messages.size()));
    }
}
