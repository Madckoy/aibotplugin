// commands.js

function setupButtonHandlers() {
    const buttons = document.querySelectorAll(".cmd-btn");

    buttons.forEach(btn => {
        btn.onclick = () => {
            const botId = btn.dataset.bot;
            const command = btn.dataset.cmd;

            // 👉 Обработка локальной команды
            if (command === "bot-info") {
                const bot = latestBotData.bots.find(b => b.id === botId);
                if (bot) showInfoPanel(bot);
                return; // ⛔ Не отправлять на сервер
            }

            if (command === "bot-home" || command === "bot-signal") {
                alert("🕹️ Insert coin to continue...\n🚧 Not implemented yet.");
                return;
            }

            // 👉 Обработка координатных команд
            const needsCoords = ["bot-tp", "bot-move"];
            if (needsCoords.includes(command)) {
                const coords = getCoordinatesFromBlueMapPopup();
                if (!coords) {
                    alert("❌ Укажите координаты на карте.");
                    return;
                }
                sendBotCommand(botId, command, [coords.x, coords.y, coords.z]);
            } else {
                sendBotCommand(botId, command, []);
            }
        };
    });
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
