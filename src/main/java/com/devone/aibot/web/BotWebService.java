package com.devone.aibot.web;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotTask;
import com.devone.aibot.utils.Constants;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class BotWebService {
    private final Server server;
    private static final String SKIN_PATH = Constants.PLUGIN_PATH + "/web/skins/";
    private static final String CONFIG_PATH = Constants.PLUGIN_PATH + "/config.yml";
    private static final String SERVER_HOST;

    static {
        File configFile = new File(CONFIG_PATH);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        SERVER_HOST = config.getString("server.host", "localhost");
    }

    public BotWebService(int port, BotManager botManager) {
        this.server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new MainPageServlet()), "/");
        context.addServlet(new ServletHolder(new BotStatusServlet(botManager)), "/status");
        context.addServlet(new ServletHolder(new SkinServlet()), "/skins/*");
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    /**
     * ✅ Отдаёт страницу мониторинга
     */
    private static class MainPageServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("text/html");
            resp.setCharacterEncoding("UTF-8");

            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("web/template.html");
            if (inputStream == null) {
                resp.getWriter().println("Error: template.html not found.");
                return;
            }

            String html = new BufferedReader(new InputStreamReader(inputStream))
                    .lines()
                    .collect(Collectors.joining("\n"))
                    .replace("{{SERVER_HOST}}", SERVER_HOST);

            resp.getWriter().println(html);
        }
    }

    /**
     * ✅ API /status — отдаёт JSON с информацией о ботах
     */
    private static class BotStatusServlet extends HttpServlet {
        private final BotManager botManager;

        public BotStatusServlet(BotManager botManager) {
            this.botManager = botManager;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");

            JsonObject result = new JsonObject();
            JsonArray botsArray = new JsonArray();

            Collection<Bot> bots = botManager.getAllBots();
            for (Bot bot : bots) {
                JsonObject botJson = new JsonObject();
                Location loc = bot.getNPCCurrentLocation();

                if (loc != null) {
                    botJson.addProperty("id", bot.getId());
                    botJson.addProperty("position", loc.getBlockX() + " , " + loc.getBlockY() + " , " + loc.getBlockZ());
                    botJson.addProperty("task", bot.getCurrentTask().getName());
                    
                    Location tg_loc = bot.getCurrentTask().getTargetLocation();
                    String targetLoc = tg_loc.getBlockX() +" , "+tg_loc.getBlockY()+" , "+ tg_loc.getBlockZ();
                    botJson.addProperty("target", targetLoc);
                    // ✅ Добавлено время выполнения таска
                    botJson.addProperty("elapsedTime", bot.getCurrentTask().getElapsedTime());

                    // Получаем TaskStack (очередь задач) и конвертируем в List
                    List<BotTask> taskStack = (bot.getLifeCycle() != null
                        && bot.getLifeCycle().getTaskStackManager() != null)
                                ? new ArrayList<>(bot.getLifeCycle().getTaskStackManager().getTaskStack()) // Преобразуем
                                                                                                           // Stack в
                                                                                                           // List
                                : new ArrayList<>();

                    // Формируем отображение стека задач
                    String taskStackText = taskStack.isEmpty() ? "N/A"
                        : taskStack.stream().map(BotTask::getName).collect(Collectors.joining(" → "));

                    botJson.addProperty("queue", taskStackText);

                    botsArray.add(botJson);

                }
            }

            result.add("bots", botsArray);
            resp.getWriter().write(result.toString());
        }
    }

    /**
     * ✅ Отдаёт изображения скинов (/skins/{UUID}.png)
     */
    private static class SkinServlet extends HttpServlet {
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String path = req.getPathInfo();
            if (path == null || path.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid skin request");
                return;
            }

            File skinFile = new File(SKIN_PATH + path.substring(1));
            if (!skinFile.exists()) {
                skinFile = new File(SKIN_PATH + "default-bot.png");
            }

            resp.setContentType("image/png");
            try (OutputStream os = resp.getOutputStream()) {
                Files.copy(skinFile.toPath(), os);
            } catch (IOException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving skin file");
            }
        }
    }
}
