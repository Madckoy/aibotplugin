// Optimized ui.js with selective DOM updates

let previousBotData = {};

function updateMonitoringHeader(data) {
    const mcTimeElem = document.getElementById("mc-time");
    const serverTimeElem = document.getElementById("server-time");

    const mcTime = data["mc-time"];
    mcTimeElem.textContent = `⛅ ${typeof mcTime === "string" && mcTime.trim() !== "" ? mcTime : "--:--"}`;

    const serverTime = data["server-time"];
    serverTimeElem.textContent = `🕒 ${typeof serverTime === "string" && serverTime.trim() !== "" ? serverTime : "--:--"}`;
}

function renderBotTable(data) {
    const table = document.getElementById("botTable");
    const tbody = table.querySelector("tbody") || table.appendChild(document.createElement("tbody"));

    data.bots.forEach(bot => updateOrCreateBotRow(bot, tbody));

    setupButtonHandlers(data.bots);
}

function updateOrCreateBotRow(bot) {
    const table = document.getElementById("bot-table");
    if (!table) return;

    let row = document.getElementById(`bot-row-${bot.id}`);
    if (!row) {
        row = document.createElement("tr");
        row.id = `bot-row-${bot.id}`;
        row.innerHTML = `
            <td>${bot.id}</td>
            <td>${bot.status}</td>
            <td><button class="info-btn" data-bot-id="${bot.id}">ℹ️</button></td>
        `;
        table.appendChild(row);

        row.querySelector(".info-btn").addEventListener("click", () => {
            showInfoPanel(bot);
        });
    } else {
        row.children[1].textContent = bot.status;
    }
}

function renderBotTable(bots) {
    bots.forEach(bot => {
        updateOrCreateBotRow(bot);
    });
}
function getTaskStatusEmoji(isReactive) {
    if (isReactive === true || isReactive === "true") return "🔸";
    if (isReactive === false || isReactive === "false") return "▪️";
    return "❔";
}

function generateInventoryGrid(slots, autoPickupEnabled) {
    const maxSlots = 36;
    slots = Array.isArray(slots) ? slots : [];
    return '<div class="inv-bar">' + Array.from({length: maxSlots}, (_, i) => {
        const slot = slots[i];
        let className = "inv-slot";
        if (slot?.amount >= 64) className += " full";
        else if (slot?.amount > 0) className += " partial";
        if (autoPickupEnabled) className += " pickup-enabled";
        const tooltip = slot ? `${slot.amount}× ${slot.type}` : 'Empty';
        return `<div class="${className}" title="${tooltip}"></div>`;
    }).join('') + '</div>';
}

function getCompassArrow(yaw) {
    if (typeof yaw !== "number") return "❓";
    yaw = (yaw + 360) % 360;

    const arrows = ["⬆️", "↗️", "➡️", "↘️", "⬇️", "↙️", "⬅️", "↖️"];
    const index = Math.round(yaw / 45) % 8;
    return arrows[index];
}

