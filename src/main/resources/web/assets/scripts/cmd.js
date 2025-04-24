// commands.js

function setupButtonHandlers(botList = []) {
    const buttons = document.querySelectorAll(".cmd-btn");

    const handlers = {
        "bot-tp":       (botId) => handleCoordCommand(botId, "bot-tp"),
        "bot-move":     (botId) => handleCoordCommand(botId, "bot-move"),
        "bot-dump":     (botId) => sendBotCommand(botId, "bot-dump"),
        "bot-drop-all": (botId) => sendBotCommand(botId, "bot-drop-all"),
        "bot-signal":   (botId) => sendBotCommand(botId, "bot-excavate"),
        "bot-home":     (botId) => sendBotCommand(botId, "bot-home"),
        "bot-info":     (botId) => {
            const bot = botList.find(b => b.id === botId);
            if (bot) showInfoPanel(bot);
        },
        "bot-close-info": () => hideInfoPanel(), // если будет кнопка с этим cmd
    };

    buttons.forEach(btn => {
        btn.onclick = (event) => {
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

    function handleCoordCommand(botId, command) {
        const coords = getCoordinatesFromBlueMapPopup();
        if (!coords) {
            alert("❌ Укажите координаты на карте.");
            return;
        }
        sendBotCommand(botId, command, [coords.x, coords.y, coords.z]);
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
