// map.js

function loadBlueMap() {
    const iframe = document.createElement("iframe");
    iframe.src = `/bluemap/`;
    iframe.id = 'bluemap-iframe';
    iframe.width = "100%";
    iframe.height = "600px";
    iframe.style.border = "none";
    document.getElementById("botMap").appendChild(iframe);
}

function getCoordinatesFromBlueMapPopup() {
    // Получаем iframe
    const iframe = document.getElementById('bluemap-iframe');

    // Проверяем, если iframe существует и доступен
    if (iframe) {
        // Получаем доступ к документу внутри iframe
        const iframeDoc = iframe.contentDocument || iframe.contentWindow.document;

        // Находим попап в документе iframe
        const popup = iframeDoc.querySelector('#bm-marker-bm-popup');

        if (popup && popup.style.opacity === "1") {
            //console.log("Popup найден:", popup);  // Логируем содержимое попапа для диагностики
            const lines = popup.innerText.trim().split("\n");
            //console.log("Строки попапа:", lines);  // Логируем строки попапа

            // Извлекаем координаты
            const coords = {};
            lines.forEach(line => {
                const matchX = line.match(/x:\s*(-?\d+)/);
                const matchY = line.match(/y:\s*(-?\d+)/);
                const matchZ = line.match(/z:\s*(-?\d+)/);

                if (matchX) coords.x = parseInt(matchX[1], 10);
                if (matchY) coords.y = parseInt(matchY[1], 10);
                if (matchZ) coords.z = parseInt(matchZ[1], 10);
            });

            // Проверяем, если координаты найдены
            if (coords.x !== undefined && coords.y !== undefined && coords.z !== undefined) {
                //console.log("Найдены координаты:", coords);  // Логируем координаты
                return coords;
            } else {
                alert("❌ Не удалось извлечь координаты");
            }
        } else {
            alert("❌ Ничего не выбрано на карте");
        }
    } else {
        alert("❌ Попап не найден!");
    }

    return null;
}
