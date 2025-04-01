// api.js
async function fetchBotData() {
    try {
        const response = await fetch(BOT_STATUS_URL);
        const data = await response.json();
        return data;
    } catch (error) {
        console.error("❌ Ошибка получения данных о ботах:", error);
        return { bots: [] };
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
