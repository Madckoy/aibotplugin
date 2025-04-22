// commands.js

function setupButtonHandlers(botList = []) {
    const buttons = document.querySelectorAll(".cmd-btn");

    const handlers = {
        "bot-tp": handleCoordCommand,
        "bot-move": handleCoordCommand,
        "bot-info": (botId) => {
            const bot = botList.find(b => b.id === botId);
            if (bot) showInfoPanel(bot);
        },
        "bot-dump": (botId) => sendBotCommand(botId, "bot-dump"),
        // сюда легко добавлять новые команды
    };

    buttons.forEach(btn => {
        btn.onclick = () => {
            const botId = btn.dataset.bot;
            const command = btn.dataset.cmd;

            const handler = handlers[command];
            if (handler) {
                handler(botId);
            } else {
                alert("🪙 Insert coin to continue.");
            }
        };
    });

    // Координатные команды вынесены отдельно
    function handleCoordCommand(botId) {
        const coords = getCoordinatesFromBlueMapPopup();
        if (!coords) {
            alert("❌ Укажите координаты на карте.");
            return;
        }
        sendBotCommand(botId, event.target.dataset.cmd, [coords.x, coords.y, coords.z]);
    }
}

async function sendBotCommand(botId, command, params = []) {
    try {
        const response = await fetch("/api/command", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ botId, command, params })
        });

        const result = await response.json();

        if (!response.ok) {
            console.error("🚫 Ошибка сервера:", result.error || response.statusText);
        } else {
            console.log("✅ Команда отправлена:", result);
        }

    } catch (error) {
        console.error("🚫 Ошибка:", error.message);
    }
}
