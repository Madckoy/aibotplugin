window.onload = async function () {
    loadBlueMap();

    try {
        const data = await fetchBotData();
        updateMonitoringHeader(data);
        renderBotTable(data);
    } catch (e) {
        console.error("Ошибка при первичной загрузке:", e);
    }

    setInterval(async () => {
        try {
            const newData = await fetchBotData();
            updateMonitoringHeader(newData);
            renderBotTable(newData);
        } catch (e) {
            console.error("Ошибка при обновлении данных:", e);
        }
    }, 250);
};
