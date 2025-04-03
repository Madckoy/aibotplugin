package com.devone.aibot.web.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

import com.devone.aibot.core.BotManager;
import com.devone.aibot.utils.BotLogger;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CommandServlet extends HttpServlet {

    private final BotManager botManager;

    public CommandServlet(BotManager botManager) {
        this.botManager = botManager;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try (BufferedReader reader = req.getReader()) {
            JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

            String botId = json.get("botId").getAsString();
            String command = json.get("command").getAsString();
            List<String> params = new ArrayList<>();
            json.getAsJsonArray("params").forEach(e -> params.add(e.getAsString()));

            String fullCommand = command + " " + botId + " " + String.join(" ", params);
            BotLogger.info(true, "🌐 От сервера получена команда: " + fullCommand);

            // Выполнить команду на основном потоке сервера
            Bukkit.getScheduler().runTask(
                Bukkit.getPluginManager().getPlugin("AIBotPlugin"),
                () -> {
                    boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), fullCommand);
                    BotLogger.info(true, "📬 Команда выполнена: " + fullCommand + " -> " + (success ? "✅ OK" : "❌ FAIL"));
                }
            );

            out.write("{\"status\":\"accepted\"}");
            out.flush();

        } catch (Exception e) {
            e.printStackTrace(); // Временный вывод в консоль
            BotLogger.error(true, "❌ Ошибка выполнения команды: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.write("{\"error\":\"Internal error\"}");
            out.flush();
        }
    }

}