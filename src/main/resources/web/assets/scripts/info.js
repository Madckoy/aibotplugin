function yawToDirectionArrow(yaw) {
    if (typeof yaw !== "number") return "N/A";
    const angle = (yaw + 360) % 360;
    if (angle >= 337.5 || angle < 22.5) return "↑";
    if (angle >= 22.5 && angle < 67.5) return "↗";
    if (angle >= 67.5 && angle < 112.5) return "→";
    if (angle >= 112.5 && angle < 157.5) return "↘";
    if (angle >= 157.5 && angle < 202.5) return "↓";
    if (angle >= 202.5 && angle < 247.5) return "↙";
    if (angle >= 247.5 && angle < 292.5) return "←";
    if (angle >= 292.5 && angle < 337.5) return "↖";
    return "❓";
}

function updateInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");

    if (!panel.classList.contains("visible")) return;
    if (currentBotId !== bot.id) return;

    const setText = (id, value) => {
        const elem = document.getElementById(id);
        if (elem) elem.textContent = value ?? "N/A";
    };

    setText("info-nav-targets", bot.reachableTargets);
    setText("info-nav-reachable", bot.reachableBlocks);
    setText("info-nav-navigable", bot.walkableBlocks);
    setText("info-nav-walkable", bot.walkableBlocks);
    setText("info-nav-type", bot.navigationSuggestion);
    setText("info-nav-suggestion", bot.suggestedBlock);
    setText("info-nav-direction", yawToDirectionArrow(bot.memory?.navigation?.yaw));
    setText("info-nav-moves", bot.memory?.navigation?.moves);
    setText("info-nav-teleports", bot.memory?.navigation?.teleports);
    setText("info-places-visited", bot.memory?.places?.visited);
    setText("info-memory-cleanup-timer", bot.memory?.cleanupTimer);
    setText("info-stats-inventory-size", bot.inventory?.length);
    setText("info-stats-attack", bot.memory?.combat?.attacks);
    setText("info-memory-killed-mobs", bot.memory?.combat?.kills);
    setText("info-stats-excavations", bot.memory?.excavation?.count);
    setText("info-stats-excavated", bot.memory?.excavation?.blocks);
}

function showInfoPanel(bot) {
    const panel = document.getElementById("bot-info-panel");
    const currentBotId = panel.getAttribute("data-bot-id");
    const isVisible = panel.classList.contains("visible");

    if (isVisible && currentBotId === bot.id) {
        hideInfoPanel();
        return;
    }

    updateInfoPanel(bot);

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

document.getElementById('close-info-btn')?.addEventListener('click', () => {
    hideInfoPanel();
});
