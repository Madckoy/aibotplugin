function loadBlueMap() {
    const iframe = document.createElement("iframe");
    iframe.src = `${BLUE_MAP_URL}`;
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
        });

    } catch (error) {
        console.error("Error fetching bot data:", error);
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
