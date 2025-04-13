// ui.js

function updateMonitoringHeader(data) {
    document.getElementById("mc-time").textContent = `⛅ ${data["mc-time"]}`;
    document.getElementById("server-time").textContent = `🕒 ${data["server-time"]}`;
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
        botCell_1.innerHTML = `<span class="bot-stats-horizontal"><div>🧱 ${bot.blocks_broken}</div><div>💀 ${bot.mobs_killed}</div><div>🗲 ${bot.teleport_used}</div></span>`;

        row.insertCell(1).innerText = bot.position;

        row.insertCell(2).innerText = bot.position; 
        row.insertCell(3).innerText = bot.stuck;
        row.insertCell(4).innerText = bot.task;
        row.insertCell(5).innerText = bot.target;
        row.insertCell(6).innerText = bot.object;
        row.insertCell(7).innerText = bot.elapsedTime;

        // 📦 Inventory
        let invCell = row.insertCell(8);
        invCell.className = "inventory-cell";
        invCell.title = `Items: ${bot.inventoryCount} / ${bot.inventoryMax}`;
        invCell.innerHTML = generateInventoryGrid(bot.inventorySlotsFilled);

        // 📋 Task Queue
        let queueCell = row.insertCell(9);
        queueCell.innerText = bot.queue;
        queueCell.style.whiteSpace = "nowrap";
        queueCell.style.textAlign = "left";
        queueCell.style.padding = "6px";

        // 🎮 Control Buttons
        let cmdCell = row.insertCell(10);
        cmdCell.innerHTML = `
            <div class="command-cell">
               <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-tp">TP</button>
               <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-move">Move</button>
               <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-drop-all">Drop All</button
            </div>   
        `;
    });

    setupButtonHandlers();
}

function generateInventoryGrid(slots) {
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

        const tooltip = slot ? `${slot.amount}× ${slot.type}` : 'Empty';
        html += `<div class="${className}" title="${tooltip}"></div>`;
    }
    html += '</div>';
    return html;
}
