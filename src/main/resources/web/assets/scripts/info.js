function updateInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");

    if (!panel.classList.contains("visible")) return;
    if (currentBotId !== bot.id) return;

    document.getElementById("info-nav-targets").textContent     = bot.reachableTargets     ?? "N/A";
    document.getElementById("info-nav-reachable").textContent   = bot.reachableBlocks      ?? "N/A";
    document.getElementById("info-nav-navigable").textContent   = bot.walkableBlocks       ?? "N/A";
    document.getElementById("info-nav-walkable").textContent    = bot.walkableBlocks       ?? "N/A";
    document.getElementById("info-nav-type").textContent        = bot.navigationSuggestion ?? "N/A";
    document.getElementById("info-nav-suggestion").textContent  = bot.suggestedBlock       ?? "N/A";
    //
    
}

function showInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");
    const isVisible = panel.classList.contains("visible");

    // 🔁 Если уже открыто и клик по тому же боту — скрыть
    if (isVisible && currentBotId === bot.id) {
        hideInfoPanel();
        return;
    }

    // 🆕 Обновление данных
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
}

function hideInfoPanel() {
    const panel = document.getElementById("bot-info-panel");
    panel.classList.remove("visible");
    panel.classList.add("hidden");
    panel.removeAttribute("data-bot-id");
}

// Закрытие по крестику
document.getElementById('close-info-btn')?.addEventListener('click', () => {
    hideInfoPanel();
});
