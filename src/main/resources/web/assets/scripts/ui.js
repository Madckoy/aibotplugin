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
                                    <div><span>🪨</span><span>${bot.blocks_broken_size}</span></div>
                                    <div><span>💀</span><span>${bot.mobs_killed_size}</span></div>
                                    <div><span>⚡️</span><span>${bot.teleport_used}</span></div>
                                </div>`; 

        let objCellPos = row.insertCell(2);
        objCellPos.innerHTML = `
            <div class="bot-position-cell">
                <div><span>📍</span><span>${bot.position}</span></div>
                <div class="bot-objective-divider"></div>
                <div><span>🎯</span><span>${bot.target}</span></div>
            </div>`;
        row.insertCell(2).innerText = bot.position; 


        row.insertCell(3).innerText = bot.stuck;
        row.insertCell(4).innerText = bot.task;
        //row.insertCell(5).innerText = bot.target;
        
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
        invCell.innerHTML = generateInventoryGrid(bot.inventorySlotsFilled, bot.auto_pick_up_items);


        // 🎮 Control Buttons
        let cmdCell = row.insertCell(8);
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
