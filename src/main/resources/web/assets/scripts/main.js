window.onload = async function () {
    loadBlueMap();

    const data = await fetchBotData();
    renderBotTable(data);

    setInterval(async () => {
        const newData = await fetchBotData();
        renderBotTable(newData);
    }, 5000);
};