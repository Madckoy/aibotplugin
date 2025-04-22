
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
    const isVisible = panel.classList.contains("visible");

    // 🔁 Если нажали повторно на того же бота — скрыть панель
    if (isVisible && currentBotId === bot.id) {
        panel.classList.remove("visible");
        panel.classList.add("hidden");
        panel.removeAttribute("data-bot-id");
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
    panel.setAttribute("data-bot-id", bot.id);

    document.getElementById('close-info-panel').addEventListener('click', () => {
        document.getElementById('bot-info-panel').classList.add('hidden');
    });
}