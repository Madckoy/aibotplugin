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
    const table = document.getElementById("bot-table");
    const tbody = table.querySelector("tbody") || table.appendChild(document.createElement("tbody"));

    data.bots.forEach(bot => updateOrCreateBotRow(bot, tbody));

    setupButtonHandlers(data.bots);
}

function updateOrCreateBotRow(bot, tbody) {
    const rowId = `bot-row-${bot.id}`;
    let row = document.getElementById(rowId);

    if (!row) {
        row = document.createElement("tr");
        row.id = rowId;
        row.innerHTML = `
            <td><img src="${bot.skin}" width="20" height="20" style="border-radius: 4px; margin-right: 5px;"> ${bot.id}</td>
            <td class="stats"></td>
            <td class="pos"></td>
            <td class="stuck"></td>
            <td class="task"></td>
            <td class="objective"></td>
            <td class="elapsed"></td>
            <td class="inventory-cell"></td>
            <td class="commands"></td>
        `;
        tbody.appendChild(row);
    }

    const cells = row.children;

    // 🪨 Статистика
    cells[1].innerHTML = `
        <div class="bot-stats-cell">
            <div><span>⛏️</span><span>${bot.blocksBroken}</span></div>
            <div><span>☠️</span><span>${bot.mobsKilled}</span></div>
            <div><span>⚡️</span><span>${bot.teleportUsed}</span></div>
        </div>`;

    // 📍 Позиция и цель
    cells[2].innerHTML = `
        <div class="bot-stats-cell">
            <div><span>📍</span><span>${bot.position}</span></div>
            <div class="bot-objective-divider"></div>
            <div><span>🎯</span><span>${bot.target}</span></div>
        </div>`;

    // 🧱 Застревание
    cells[3].innerHTML = `
        <div class="bot-position-cell">
            <div><span>${bot.stuck}</span></div>
            <div class="bot-objective-divider"></div>
            <div><span>${bot.stuckCount}</span></div>
        </div>`;

    // 🧠 Задача
    cells[4].innerHTML = `
        <div class="bot-position-cell">
            <div><span>${bot.task}</span></div>
            <div class="bot-objective-divider"></div>
            <div><span>${getTaskStatusEmoji(bot.taskIsReactive)}</span></div>
        </div>`;

    // 📋 Очередь задач
    cells[5].innerHTML = `
        <div class="bot-objective-cell">
            <div><span>ᯓ </span><span>${bot.queue}</span></div>
            <div class="bot-objective-divider"></div>
            <div><span>✴ </span><span>${bot.object}</span></div>
        </div>`;

    // ⏱️ Время
    cells[6].textContent = bot.elapsedTime;

    // 🎒 Инвентарь
    cells[7].title = `Items: ${bot.inventoryCount} / ${bot.inventoryMax}`;
    cells[7].innerHTML = generateInventoryGrid(bot.inventorySlotsFilled, bot.autoPickUpItems);

    // ⚙️ Команды
    if (!cells[8].innerHTML.trim()) {
        cells[8].innerHTML = `
            <div class="bot-position-cell">
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-tp">⚡</button>
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-move">🏃🏻‍♂️‍➡️</button>
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-excavate">⛏️</button>
                <div class="bot-objective-divider"></div>      
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-drop-all">📦</button>
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-dump">#️⃣</button>
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-info">ℹ️</button>
            </div>`;
    }

    updateInfoPanel(bot);     
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
