

function updateInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");
    if (!panel.classList.contains("visible") || currentBotId !== bot.id) return;

    const nav = bot.memory?.navigation || {};
    const stats = bot.memory?.stats || {};

    // Navigation
    const summary = nav.summary || {};
    updateNavSummary("poi", summary.poi);
    updateNavSummary("reachable", summary.reachable);
    updateNavSummary("navigable", summary.navigable);
    updateNavSummary("walkable", summary.walkable);


    document.getElementById("info-nav-type").textContent = nav.suggestion ?? "N/A";
    document.getElementById("info-nav-suggestion").textContent = nav.suggestedPoi ?? "N/A";
    document.getElementById("info-nav-moves").textContent = bot.blocksBroken ?? "N/A";
    document.getElementById("info-nav-teleports").textContent = bot.teleportUsed ?? "N/A";

    document.getElementById("info-nav-direction").textContent = getCompassArrow(bot.yaw);

    // Stats
    const visited = nav.visited || {};
    document.getElementById("info-places-visited").textContent = Object.keys(visited).length;
    document.getElementById("info-memory-cleanup-timer").textContent = formatCleanupTimer(visited);

    document.getElementById("info-stats-inventory-size").textContent = `${bot.inventoryCount} / ${bot.inventoryMax}`;
    document.getElementById("info-stats-health").textContent = "❤️"; // TODO: health logic
    document.getElementById("info-memory-killed-mobs").textContent = Object.keys(stats.mobsKilled || {}).length || 0;
    document.getElementById("info-stats-excavated").textContent = stats.blocksBroken?.total ?? "N/A";

    // Future: Attacks, Excavations
}

function updateNavSummary(key, data) {
    if (!data) {
        document.getElementById(`info-nav-${key}`).textContent = "N/A";
        return;
    }
    const confirmed = data.confirmed ?? 0;
    const calculated = data.calculated ?? 0;
    document.getElementById(`info-nav-${key}`).textContent = `${confirmed} / ${calculated}`;
}

function formatCleanupTimer(visited) {
    const now = Date.now();
    const ttl = 30 * 60 * 1000; // 30 мин
    const times = Object.values(visited);
    if (times.length === 0) return "N/A";

    const oldest = Math.min(...times);
    const remaining = (ttl - (now - oldest)) / 1000;
    if (remaining <= 0) return "soon";

    const m = Math.floor(remaining / 60);
    const s = Math.floor(remaining % 60);
    return `${m}m ${s}s`;
}


function showInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");
    const isVisible = panel.classList.contains("visible");

    if (isVisible && currentBotId === bot.id) {
        hideInfoPanel();
        return;
    }

    // Обновляем данные сразу
    updateInfoPanel(bot);

    // Показываем панель
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
