function loadBlueMap() {
    const iframe = document.createElement("iframe");
    iframe.src = `/bluemap/`;
    iframe.id = 'bluemap-iframe';	
    iframe.width = "100%";
    iframe.height = "600px";
    iframe.style.border = "none";
    document.getElementById("botMap").appendChild(iframe);
}

async function fetchBotData() {
    try {
        const response = await fetch(BOT_STATUS_URL);
        const data = await response.json();

        let table = document.getElementById("botTable");
        let tbody = table.querySelector("tbody");
        if (tbody) tbody.remove();
        tbody = document.createElement("tbody");
        table.appendChild(tbody);

        data.bots.forEach(bot => {
            let row = tbody.insertRow();
            row.style.height = "30px";

            // 📛 Bot ID + skin
            let botCell = row.insertCell(0);
            botCell.innerHTML = `<img src="${bot.skin}" width="20" height="20" style="border-radius: 4px; margin-right: 5px;"> ${bot.id}`;
            botCell.style.padding = "6px";

            row.insertCell(1).innerText = bot.position;
            row.insertCell(2).innerText = bot.task;
            row.insertCell(3).innerText = bot.target;
            row.insertCell(4).innerText = bot.object;
            row.insertCell(5).innerText = bot.elapsedTime;

            // 📦 Inventory Grid
            let invCell = row.insertCell(6);
            invCell.className = "inventory-cell";
            invCell.title = `Items: ${bot.inventoryCount} / ${bot.inventoryMax}`;
            invCell.innerHTML = generateInventoryBar(bot.inventorySlotsFilled);

            // 📋 Task Queue
            let queueCell = row.insertCell(7);
            queueCell.innerText = bot.queue;
            queueCell.style.whiteSpace = "nowrap";
            queueCell.style.textAlign = "left";
            queueCell.style.padding = "6px";

            // 🧠 Command Buttons
            let cmdCell = row.insertCell(8);
            cmdCell.innerHTML = `
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="tp">TP</button>
                <button class="cmd-btn" data-bot="${bot.id}" data-cmd="move">Move</button>
            `;
            
            const buttons = cmdCell.querySelectorAll(".cmd-btn");
            buttons.forEach(btn => {
                btn.onclick = () => {
                    const botId = btn.dataset.bot;
                    const command = btn.dataset.cmd;

                    const needsCoords = ["tp", "move"]; // без префикса "bot-"
                    if (needsCoords.includes(command)) {
                        const coords = getCoordinatesFromBlueMapPopup();
                        if (!coords) {
                            alert("❌ Укажите координаты на карте.");
                            return;
                        }
                        sendBotCommand(botId, "bot-" + command, [coords.x, coords.y, coords.z]);
                    } else {
                        sendBotCommand(botId, "bot-" + command, []);
                    }
                };
            });
        });

    } catch (error) {
        console.error("❌ Ошибка получения данных о ботах:", error);
    }
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

window.onload = function () {
    loadBlueMap();
    fetchBotData();
    setInterval(fetchBotData, 5000);
};

async function sendBotCommand(botId, command, params = []) {
    try {
        const response = await fetch("/api/command", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({ botId, command, params })
        });

        const result = await response.json();

        if (!response.ok) {
            console.error("🚫 Ошибка сервера:", result.error || response.statusText);
        } else {
            console.log("✅ Команда отправлена:", result);
        }

    } catch (error) {
        console.error("🚫 Ошибка:", error.message);
    }
}

function getCoordinatesFromBlueMapPopup() {
    const popup = document.querySelector('#bm-marker-bm-popup');
    if (!popup || popup.style.opacity !== "1") {
        alert("❌ Ничего не выбрано на карте");
        return null;
    }

    const lines = popup.innerText.trim().split("\n");

    for (let line of lines) {
        const match = line.match(/X:\s*(-?\d+),\s*Y:\s*(-?\d+),\s*Z:\s*(-?\d+)/);
        if (match) {
            return {
                x: parseInt(match[1], 10),
                y: parseInt(match[2], 10),
                z: parseInt(match[3], 10)
            };
        }
    }

    alert("❌ Не удалось извлечь координаты");
    return null;
}

