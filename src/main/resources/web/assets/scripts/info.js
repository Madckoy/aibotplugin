
function updateInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");

    // Обновляем содержимое ТОЛЬКО если панель видима и ID совпадает
    if (panel.classList.contains("visible") && currentBotId === bot.id) {
        document.getElementById("info-nav-targets").textContent     = bot.reachableTargets     ?? "N/A";
        document.getElementById("info-nav-reachable").textContent   = bot.reachableBlocks      ?? "N/A";
        document.getElementById("info-nav-navigable").textContent   = bot.walkableBlocks       ?? "N/A";
        document.getElementById("info-nav-walkable").textContent    = bot.walkableBlocks       ?? "N/A";
        document.getElementById("info-nav-type").textContent        = bot.navigationSuggestion ?? "N/A";
        document.getElementById("info-nav-suggestion").textContent  = bot.suggestedBlock       ?? "N/A";
    }
}  


function showInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");
    const wasVisible = panel.getAttribute("data-visible") === "true";

    // 🔁 Если уже открыт на того же бота — закрыть
    if (wasVisible && currentBotId === bot.id) {
        panel.classList.remove("visible");
        panel.classList.add("hidden");
        panel.setAttribute("data-visible", "false");
        return;
    }

    // 🆕 Обновляем содержимое
    document.getElementById("info-nav-targets").textContent     = bot.reachableTargets     ?? "N/A";
    document.getElementById("info-nav-reachable").textContent   = bot.reachableBlocks      ?? "N/A";
    document.getElementById("info-nav-navigable").textContent   = bot.walkableBlocks       ?? "N/A";
    document.getElementById("info-nav-walkable").textContent    = bot.walkableBlocks       ?? "N/A";
    document.getElementById("info-nav-type").textContent        = bot.navigationSuggestion ?? "N/A";
    document.getElementById("info-nav-suggestion").textContent  = bot.suggestedBlock       ?? "N/A";

    // 📌 Показываем панель
    panel.classList.remove("hidden");
    panel.classList.add("visible");
    panel.setAttribute("data-visible", "true");
    panel.setAttribute("data-bot-id", bot.id);
}

// Закрытие через крестик
document.getElementById('close-info-btn').addEventListener('click', () => {
    const panel = document.getElementById("bot-info-panel");
    panel.classList.remove("visible");
    panel.classList.add("hidden");
    panel.setAttribute("data-visible", "false");
    panel.removeAttribute("data-bot-id");
});


function hideInfoPanel() {
    const panel = document.getElementById("bot-info-panel");
    panel.classList.remove("visible");
    panel.classList.add("hidden");
    panel.setAttribute("data-visible", "false");
    panel.removeAttribute("data-bot-id");
}