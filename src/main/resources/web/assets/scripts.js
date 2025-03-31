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

            let botCell = row.insertCell(0);
            botCell.innerHTML = "<img src=" + bot.skin + " width='20' height='20' style='border-radius: 4px; margin-right: 5px;'> " + bot.id;
            botCell.style.padding = "6px";

            row.insertCell(1).innerText = bot.position;
            row.insertCell(2).innerText = bot.task;
            row.insertCell(3).innerText = bot.target;
            row.insertCell(4).innerText = bot.object;
            row.insertCell(5).innerText = bot.elapsedTime;

            let invCell = row.insertCell(6);
            invCell.className = "inventory-cell";
            invCell.title = `Items: ${bot.inventoryCount} / ${bot.inventoryMax}`;
            invCell.innerHTML = generateInventoryGrid(bot.inventorySlots); // 🔧 Вставка грида

            let queueCell = row.insertCell(7);
            queueCell.innerText = bot.queue;
            queueCell.style.whiteSpace = "nowrap";
            queueCell.style.textAlign = "left";
            queueCell.style.paddingLeft = "6px";
            queueCell.style.padding = "6px";

            let controlCell = row.insertCell(8);
            controlCell.className = "bot-actions";
            controlCell.innerHTML = generateControlPanel(bot.id);


        });

    } catch (error) {
        console.error("Error fetching bot data:", error);
    }
}

function generateControlPanel(botId) {
    return `
        <div class="bot-actions">
            <div class="button-grid">
		<button onclick="sendBotToSelectedFromMap();">Teleport</button>
                <button onclick="alert('Move');">Move</button>
                <button onclick="alert('Break');">Break</button>
                <button onclick="alert('Build');", 'Build')">Build</button>
                <button onclick="alert('Drop');", 'Drop All')">Drop</button>
            </div>
        </div>
    `;
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

function sendBotCommand(botId, command) {
    fetch(`/api/bot/${botId}/command/${command}`, { method: "POST" })
        .then(res => res.ok ? console.log(`✅ ${botId} -> ${command}`) : console.warn(`❌ ${botId} -> ${command}`));
}


window.bluemap?.getViewer().then(viewer => {
    const map = viewer.getMap();

    map.once("click", event => {
        const { x, y, z } = event.position;
        console.log(`📍 Выбрана точка: (${x}, ${y}, ${z})`);
    });
});



function sendBotToSelectedFromMap() {
    const iframe = document.getElementById("bluemap-iframe");
    if (!iframe) return;

    try {
        const doc = iframe.contentWindow.document;
        const popup = doc.querySelector("#bm-marker-bm-popup");

        if (!popup || popup.style.opacity !== "1") {
            alert("❌ Не выбрана точка на карте!");
            return;
        }

        const values = popup.querySelectorAll(".value");
        if (values.length < 3) {
            alert("⚠️ Не удалось извлечь координаты!");
            return;
        }

        const x = parseInt(values[0].textContent);
        const y = parseInt(values[1].textContent);
        const z = parseInt(values[2].textContent);

        if (isNaN(x) || isNaN(y) || isNaN(z)) {
            alert("❌ Некорректные координаты!");
            return;
        }

        console.log(`📍 Отправка команды: /bot-move ${x} ${y} ${z}`);

        fetch("/api/command", {
            method: "POST",
            body: JSON.stringify({
                botId: "AI_Steve_2",
                command: `/bot-move ${x} ${y} ${z}`
            }),
            headers: {
                "Content-Type": "application/json"
            }
        });

    } catch (err) {
        alert("🚫 Ошибка при чтении карты: " + err.message);
    }
}
