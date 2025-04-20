// ui.js

function updateMonitoringHeader(data) {
    const mcTimeElem = document.getElementById("mc-time");
    const serverTimeElem = document.getElementById("server-time");

    // ⛅ Minecraft Time (уже строка)
    const mcTime = data["mc-time"];
    if (typeof mcTime === "string" && mcTime.trim() !== "") {
        mcTimeElem.textContent = `⛅ ${mcTime}`;
    } else {
        mcTimeElem.textContent = "⛅ --:--";
    }

    // 🕒 Server Time
    const serverTime = data["server-time"];
    if (typeof serverTime === "string" && serverTime.trim() !== "") {
        serverTimeElem.textContent = `🕒 ${serverTime}`;
    } else {
        serverTimeElem.textContent = "🕒 --:--";
    }
}

function renderBotTable(data) {
    let table = document.getElementById("botTable");
    let tbody = table.querySelector("tbody");
    if (tbody) tbody.remove();
    tbody = document.createElement("tbody");
    table.appendChild(tbody);

    data.bots.forEach(bot => {
        let row = tbody.insertRow();
        row.style.height = "30px";

        // 📛 Bot ID + Skin
        let botCell_0 = row.insertCell(0);
        botCell_0.innerHTML = `<img src="${bot.skin}" width="20" height="20" style="border-radius: 4px; margin-right: 5px;"> ${bot.id}`;
        botCell_0.style.padding = "6px";

        let botCell_1 = row.insertCell(1); 
        botCell_1.innerHTML = `
                                <div class="bot-stats-cell">
                                    <div><span>🪨</span><span>${bot.blocksBroken}</span></div>
                                    <div><span>💀</span><span>${bot.mobsKilled}</span></div>
                                    <div><span>⚡️</span><span>${bot.teleportUsed}</span></div>
                                </div>`; 

        let objCellPos = row.insertCell(2);
        objCellPos.innerHTML = `
            <div class="bot-stats-cell">
                <div><span>📍</span><span>${bot.position}</span></div>
                <div class="bot-objective-divider"></div>
                <div><span>🎯</span><span>${bot.target}</span></div>
            </div>`;

        let objCellStuck = row.insertCell(3);
        objCellStuck.innerHTML = `
            <div class="bot-position-cell">
                <div><span>${bot.stuck}</span></div>
                <div class="bot-objective-divider"></div>
                <div><span>${bot.stuckCount}</span></div>
            </div>`;

        let taskCell = row.insertCell(4);
        taskCell.innerHTML = `
        <div class="bot-position-cell">
            <div><span>${bot.task}</span></div>
            <div class="bot-objective-divider"></div>
            <div><span>${getTaskStatusEmoji(bot.taskIsReactive)}</span></div>
        </div>`; 
        
        let objCell = row.insertCell(5);
        objCell.innerHTML = `
            <div class="bot-objective-cell">
                <div><span>ᯓ </span><span>${bot.queue}</span></div>
                <div class="bot-objective-divider"></div>
                <div><span>✴ </span><span>${bot.object}</span></div>
            </div>`;

        row.insertCell(6).innerText = bot.elapsedTime;

        // 📦 Inventory
        let invCell = row.insertCell(7);
        invCell.className = "inventory-cell";
        invCell.title = `Items: ${bot.inventoryCount} / ${bot.inventoryMax}`;
        invCell.innerHTML = generateInventoryGrid(bot.inventorySlotsFilled, bot.autoPickUpItems);


        // 🎮 Control Buttons
        let cmdCell = row.insertCell(8);
        cmdCell.innerHTML = `
            <div class="bot-position-cell">
   	            <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-tp">⚡</button>
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-move">🚶‍♀️‍➡️</button>
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-drop-all">📦</button
                <div class="bot-objective-divider"></div>
   	            <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-signal">📡</button>
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-home">🏡</button>
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-info">ℹ️</button>
            </div>   
        `;
    });

    setupButtonHandlers();
    
    setupInfoPanel();

}


function getTaskStatusEmoji(isReactive) {
    if (isReactive === true || isReactive === "true") {
        return "🔸"; // реактивная задача
    } else if (isReactive === false || isReactive === "false") {
        return " "; // обычная
    } else {
        return "❔"; // не определено / fallback
    }
}

function generateInventoryGrid(slots, autoPickupEnabled) {
    const maxSlots = 36;
    slots = Array.isArray(slots) ? slots : [];

    let html = '<div class="inv-bar">';
    for (let i = 0; i < maxSlots; i++) {
        const slot = slots[i];
        let className = "inv-slot";

        if (slot && slot.amount >= 64) {
            className += " full";
        } else if (slot && slot.amount > 0) {
            className += " partial";
        }

        if (autoPickupEnabled) {
            className += " pickup-enabled";
        }

        const tooltip = slot ? `${slot.amount}× ${slot.type}` : 'Empty';
        html += `<div class="${className}" title="${tooltip}"></div>`;
    }
    html += '</div>';
    return html;
}

function setupInfoPanel() {
    const panel = document.getElementById("bot-info-panel");
    const closeBtn = document.getElementById("info-close-btn");
    closeBtn.addEventListener("click", () => {
        panel.classList.remove("visible");
        panel.classList.add("hidden");
    });
}

function showInfoPanel(bot) {
    document.getElementById("info-id").textContent = bot.id;
    document.getElementById("info-model").textContent = bot.model || "M-000.2";
    document.getElementById("info-navpoints").textContent = bot.navPoints ?? "n/a";
    document.getElementById("info-reactive").textContent = getTaskStatusEmoji(bot.taskIsReactive);

    const panel = document.getElementById("info-panel");
    panel.classList.remove("hidden");
    panel.classList.add("visible");
}