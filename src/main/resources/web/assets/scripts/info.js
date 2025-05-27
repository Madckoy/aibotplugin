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
    const suggested_target = bot.memory?.navigation?.suggested_target ?? "N/A";
    const scan_radius = bot.memory?.navigation?.scan_radius ?? "N/A";
    const candidates = bot.memory?.navigation?.candidates;
    const count = Array.isArray(candidates) ? candidates.length : "N/A";

    const watchdog_pos  = bot.watchdog_position ?? "N/A";
    const watchdog_time = bot.watchdog_time? new Date(bot.watchdog_time).toLocaleString(): "N/A";

    const format = (entry) => {
        if (!entry) return "N/A";
        return `${entry.calculated ?? 0} / ${entry.confirmed ?? 0}`;
    };

    const simpleFormat = (entry) => {
        if (!entry) return "N/A";
        return `${entry.calculated ?? 0}`;
    };


    document.getElementById("info-nav-targets").textContent = simpleFormat(summary.targets);
    document.getElementById("info-nav-reachable").textContent = format(summary.reachable);
    document.getElementById("info-nav-walkable").textContent = format(summary.walkable);
    document.getElementById("info-nav-navigation-suggestion").textContent = suggestion;
    document.getElementById("info-nav-suggested-position").textContent = suggested_target;
    document.getElementById("info-nav-facing-direction").textContent = getCompassArrow(yaw);
    document.getElementById("info-nav-scan-range").textContent = scan_radius;
    document.getElementById("info-nav-сandidates").textContent = candidates;
    document.getElementById("info-nav-watchdog-position").textContent = `${watchdog_pos}`;
    document.getElementById("info-nav-watchdog-time").textContent = `${watchdog_time}`;
}

function updateStats(bot) {

    document.getElementById("info-stats-teleports").textContent = bot.teleports ?? "N/A";
    document.getElementById("info-stats-visited").textContent = bot.visited_count ?? "N/A";
    document.getElementById("info-stats-inventory-count").textContent = bot.inventory_count ?? "N/A";
    document.getElementById("info-stats-killed-mobs").textContent = bot.kills ?? "N/A";
    document.getElementById("info-stats-excavated").textContent = bot.breaks ?? "N/A";
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

  const adjusted = (yaw + 360) % 360;

  //const directions = ["N ⬇️", "NE ↙️", "E ⬅️", "SE ↖️", "S ⬆️", "SW ↗️", "W ➡️", "NW ↘️"];
  
  //const directions = ["S ⬆️", "SW ↗️", "W ➡️", "NW ↘️", "N ⬇️", "NE ↙️", "E ⬅️", "SE ↖️"];  
  
  //const directions = ["N ⬆️", "NE ↗️", "E ➡️", "SE ↘️", "S ⬇️", "SW ↙️", "W ⬅️", "NW ↖️"];

  const directions = ["S ⬇️", "SW ↙️", "W ⬅️", "NW ↖️", "N ⬆️", "NE ↗️", "E ➡️", "SE ↘️"];

  const index = Math.round(adjusted / 45) % 8;
  
  const degrees = Math.round(yaw);
  return `${directions[index]} (${degrees}°)`;
}
