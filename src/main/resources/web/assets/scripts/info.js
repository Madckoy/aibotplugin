function updateInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");

    if (!panel.classList.contains("visible")) return;
    if (currentBotId !== bot.id) return;

    updateNavSummary(bot);
    updateStats(bot);
}

function updateNavSummary(bot) {
    const summary = bot.memory?.navigation?.summary ?? {};
    const yaw = bot.memory?.navigation?.yaw;
    const suggestion = bot.memory?.navigation?.suggestion ?? "N/A";
    const suggestedTarget = bot.memory?.navigation?.suggestedTarget ?? "N/A";
    const scanRadius = bot.memory?.navigation?.scanRadius ?? "N/A";

    const format = (entry) => {
        if (!entry) return "N/A";
        return `${entry.calculated ?? 0} / ${entry.confirmed ?? 0}`;
    };

    document.getElementById("info-nav-pois").textContent = format(summary.poi);
    document.getElementById("info-nav-reachable").textContent = format(summary.reachable);
    document.getElementById("info-nav-navigable").textContent = format(summary.navigable);
    document.getElementById("info-nav-walkable").textContent = format(summary.walkable);
    document.getElementById("info-nav-navigation-suggestion").textContent = suggestion;
    document.getElementById("info-nav-suggested-position").textContent = suggestedTarget;
    document.getElementById("info-nav-facing-direction").textContent = getCompassArrow(yaw);
    document.getElementById("info-nav-scan-range").textContent = scanRadius;
}

function updateStats(bot) {
    document.getElementById("info-stats-teleports").textContent = bot.teleportUsed ?? "N/A";
    document.getElementById("info-stats-visited").textContent = bot.visitedCount ?? "N/A";
    document.getElementById("info-stats-inventory-count").textContent = bot.inventoryCount ?? "N/A";
    document.getElementById("info-stats-killed-mobs").textContent = bot.mobsKilled ?? "N/A";
    document.getElementById("info-stats-excavated").textContent = bot.blocksBroken ?? "N/A";
}

function showInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const isVisible = panel.classList.contains("visible");

    if (isVisible && panel.getAttribute("data-bot-id") === bot.id) {
        hideInfoPanel();
        return;
    }

    panel.setAttribute("data-bot-id", bot.id);
    panel.classList.remove("hidden");
    panel.classList.add("visible");
    updateInfoPanel(bot);
}

function hideInfoPanel() {
    const panel = document.getElementById("bot-info-panel");
    panel.classList.remove("visible");
    panel.classList.add("hidden");
    panel.removeAttribute("data-bot-id");
}

document.getElementById('close-info-btn')?.addEventListener('click', hideInfoPanel);

function getCompassArrow(yaw) {
    if (typeof yaw !== "number") return "❓";
    yaw = (yaw + 360) % 360;
    const arrows = ["⬆️", "↗️", "➡️", "↘️", "⬇️", "↙️", "⬅️", "↖️"];
    const index = Math.round(yaw / 45) % 8;
    const degrees = Math.round(yaw);
    const arrow = arrows[index];
    return `${arrow} (${degrees}°)`;
}
