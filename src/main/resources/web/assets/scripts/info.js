function updateInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");
    if (!panel.classList.contains("visible") || currentBotId !== bot.id) return;

    const nav = bot.memory?.navigation ?? {};
    const summary = nav.summary ?? {};
    const stats = bot.memory?.stats ?? {};

    const formatCount = (key) => {
        const item = summary[key];
        return item ? `${item.confirmed ?? 0} / ${item.calculated ?? 0}` : "N/A";
    };

    // === 🗺️ Navigation ===
    document.getElementById("info-nav-targets").textContent     = formatCount("poi");
    document.getElementById("info-nav-reachable").textContent   = formatCount("reachable");
    document.getElementById("info-nav-navigable").textContent   = formatCount("navigable");
    document.getElementById("info-nav-walkable").textContent    = formatCount("walkable");
    document.getElementById("info-nav-type").textContent        = nav.suggestion ?? "N/A";
    document.getElementById("info-nav-suggestion").textContent  = nav.suggestedPoi ?? "N/A";
    document.getElementById("info-nav-moves").textContent       = stats.moves ?? "0";
    document.getElementById("info-nav-teleports").textContent   = stats.teleportUsed ?? "0";

    // === 📊 Stats ===
    const visitedCount = Object.keys(nav.visited ?? {}).length;
    document.getElementById("info-places-visited").textContent = visitedCount;

    const ttl = 30 * 60 * 1000; // 30 минут (мс)
    const cleanupInMin = Math.floor(ttl / 60000);
    document.getElementById("info-memory-cleanup-timer").textContent = `${cleanupInMin} min`;

    const invCount = bot.inventoryCount ?? 0;
    const invMax = bot.inventoryMax ?? 36;
    document.getElementById("info-stats-imventory-size").textContent = `${invCount} / ${invMax}`;

    document.getElementById("info-stats-attack").textContent = stats.attacks ?? "0";
    document.getElementById("info-memory-killed-mobs").textContent = Object.keys(stats.mobsKilled ?? {}).length;

    document.getElementById("info-stats-excavations").textContent = stats.excavations ?? "0";
    document.getElementById("info-stats-excavated").textContent   = stats.blocksBroken?.total ?? "0";
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
