package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.core.logic.tasks.configs.BotTaskTalkConfig;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class BotTaskTalk extends BotTask {

    private final Player player;
    private final TalkType type;
    private final Random random = new Random();
    private static final BotTaskTalkConfig config = new BotTaskTalkConfig();

    public enum TalkType {
        COMPLIMENT, INSULT_MOB, ENVIRONMENT_COMMENT,
        INVENTORY_REPORT, HELP_REQUEST, SELF_TALK
    }

    public BotTaskTalk(Bot bot, Player player, TalkType type) {
        super(bot, "💬");
        this.player = player;
        this.type = type;
    }

    @Override
    public void executeTask() {
        String message = generateMessage();

        if (message.isEmpty()) {
            isDone = true;
            return;
        }

        setObjective("Размышляет: " + message);

        // 🎯 Есть игрок — говорим персонально
        if (player != null) {
            player.sendMessage("🤖 " + bot.getId() + ": " + message);
        }
        // 📣 Нет игрока — говорим в общий чат (если нужно)
        else if (shouldBroadcastToAll(type)) {
            Bukkit.broadcastMessage("🤖 " + bot.getId() + ": " + message);
        }
        // 🤫 Или просто бурчим себе под нос (в лог)
        else {
            BotLogger.debug(bot.getId() + " бурчит себе под нос: " + message);
        }

        isDone = true;
    }

    private boolean shouldBroadcastToAll(TalkType type) {
        return switch (type) {
            case ENVIRONMENT_COMMENT, INVENTORY_REPORT, HELP_REQUEST -> true;
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
            case SELF_TALK -> getRandomMessage(config.getSelfTalks());
        };
    }
    

    private String getRandomMessage(List<String> messages) {
        if (messages == null || messages.isEmpty()) return "🤖 ...";
        return messages.get(random.nextInt(messages.size()));
    }
}
