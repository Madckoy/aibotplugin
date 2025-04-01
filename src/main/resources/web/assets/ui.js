// ui.js

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
        let botCell = row.insertCell(0);
        botCell.innerHTML = `<img src="${bot.skin}" width="20" height="20" style="border-radius: 4px; margin-right: 5px;"> ${bot.id}`;
        botCell.style.padding = "6px";

        row.insertCell(1).innerText = bot.position;
        row.insertCell(2).innerText = bot.task;
        row.insertCell(3).innerText = bot.target;
        row.insertCell(4).innerText = bot.object;
        row.insertCell(5).innerText = bot.elapsedTime;

        // 📦 Inventory
        let invCell = row.insertCell(6);
        invCell.className = "inventory-cell";
        invCell.title = `Items: ${bot.inventoryCount} / ${bot.inventoryMax}`;
        invCell.innerHTML = generateInventoryGrid(bot.inventorySlotsFilled);

        // 📋 Task Queue
        let queueCell = row.insertCell(7);
        queueCell.innerText = bot.queue;
        queueCell.style.whiteSpace = "nowrap";
        queueCell.style.textAlign = "left";
        queueCell.style.padding = "6px";

        // 🎮 Control Buttons
        let cmdCell = row.insertCell(8);
        cmdCell.innerHTML = `
            <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-tp">TP</button>
            <button class="cmd-btn" data-bot="${bot.id}" data-cmd="bot-move">Move</button>
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
