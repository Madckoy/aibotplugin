package com.devone.aibot.web;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotTask;
import com.devone.aibot.utils.BotUtils;
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
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class BotWebService {
    private final Server server;
    private static final String SKIN_PATH = Constants.PLUGIN_PATH + "/web/skins/";
    private static final String CONFIG_PATH = Constants.PLUGIN_PATH + "/config.yml";
    public static final String SERVER_HOST;
    private static final String MAP_HOST;


    static {
        File configFile = new File(CONFIG_PATH);
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
        SERVER_HOST = config.getString("server.web_host", "localhost");
        MAP_HOST = config.getString("server.map_host", "localhost");
    }

    public BotWebService(int port, BotManager botManager) {
        this.server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new MainPageServlet()), "/");
        context.addServlet(new ServletHolder(new BotStatusServlet(botManager)), "/status");
        context.addServlet(new ServletHolder(new SkinServlet()), "/skins/*");
        context.addServlet(new ServletHolder(new StaticFileServlet()),"/assets/*");

        copyResourceFiles();
    }

    public static String getServerHost() {
        return SERVER_HOST;
    }

    public static String getMapHost() {
        return MAP_HOST;
    }
/**
     * ✅ Метод копирования ресурсов (CSS и JS) в plugins/AIBotPlugin/web/assets/
     */
    private void copyResourceFiles() {
        String[] resourceFiles = {"web/assets/styles.css", "web/assets/script.js"};
        for (String resource : resourceFiles) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(resource)) {
                if (in == null) {
                    System.err.println("⚠ Resource not found: " + resource);
                    continue;
                }
                File targetFile = new File(Constants.PLUGIN_PATH_WEB_ASSETS + new File(resource).getName());
                targetFile.getParentFile().mkdirs();
                Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                System.out.println("✅ Copied: " + resource + " → " + targetFile.getPath());
            } catch (IOException e) {
                System.err.println("❌ Failed to copy " + resource + ": " + e.getMessage());
            }
        }
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }


    private static class StaticFileServlet extends HttpServlet {

        private static final String ASSETS_PATH = Constants.PLUGIN_PATH + "/web/assets/";
    
        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String path = req.getPathInfo();
            if (path == null || path.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid asset request");
                return;
            }
    
            File assetFile = new File(ASSETS_PATH + path.substring(1));
            if (!assetFile.exists()) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                return;
            }
    
            if (path.endsWith(".css")) {
                resp.setContentType("text/css");
            } else if (path.endsWith(".js")) {
                resp.setContentType("application/javascript");
            }
    
            try (OutputStream os = resp.getOutputStream()) {
                Files.copy(assetFile.toPath(), os);
            } catch (IOException e) {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving asset file");
            }
        }
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
                    .replace("{{MAP_HOST}}", MAP_HOST);

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
                    botJson.addProperty("skin", "http://" + getServerHost() + ":3000/skins/" + bot.getUuid() + ".png");
                    botJson.addProperty("id", bot.getId());
                    botJson.addProperty("position", loc.getBlockX() + " , " + loc.getBlockY() + " , " + loc.getBlockZ());
                    botJson.addProperty("task", bot.getCurrentTask().getName());
                    
                    Location tg_loc = bot.getCurrentTask().getTargetLocation();
                    String targetLoc = tg_loc.getBlockX() +" , "+tg_loc.getBlockY()+" , "+ tg_loc.getBlockZ();
                    botJson.addProperty("target", targetLoc);
                    // ✅ Добавлено время выполнения таска
                    botJson.addProperty("elapsedTime", BotUtils.formatTime(bot.getCurrentTask().getElapsedTime()));

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
