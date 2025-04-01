package com.devone.aibot.web;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotTask;

import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.BotLogger;
import com.devone.aibot.utils.BotStringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class BotWebService {
    private final Server server;
    private final String SKIN_PATH = BotConstants.PLUGIN_PATH + "/web/skins/";
    private final String CONFIG_PATH = BotConstants.PLUGIN_PATH + "/config.yml";

    public String SERVER_HOST = "localhost";
    public String MAP_HOST = "localhost";
    public String MAP_PORT = "8100";

    private String bluemapBaseUrl = "http://localhost:8100"; // по умолчанию

    private static BotWebService instance = null;

    public BotWebService(int port, BotManager botManager) {
        // Переносим инициализацию конфигурации сюда
        File configFile = new File(CONFIG_PATH);

        if (configFile.exists()) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);
            SERVER_HOST = config.getString("server.web_host", "localhost");
            MAP_HOST = config.getString("server.map_host", "localhost");
            MAP_PORT = config.getString("server.map_port", "8100"); 
            
            bluemapBaseUrl = "http://" + MAP_HOST + ":" + MAP_PORT;
            BotLogger.info(true, "🧭 BlueMap Proxy Target: " + bluemapBaseUrl);

        } else {
            BotLogger.warn(true, "⚠️ Конфиг не найден, MAP_HOST = localhost");
        }

        BotLogger.info(true, "🌐 SERVER_HOST: " + SERVER_HOST);
        BotLogger.info(true, "🌐 MAP_HOST: " + MAP_HOST);
        BotLogger.info(true, "🌐 MAP_PORT: " + MAP_PORT);
        

        this.server = new Server(port);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new MainPageServlet()), "/");
        context.addServlet(new ServletHolder(new BotStatusServlet(botManager)), "/status");
        context.addServlet(new ServletHolder(new SkinServlet()), "/skins/*");
        context.addServlet(new ServletHolder(new StaticFileServlet()), "/assets/*");
        context.addServlet(new ServletHolder(new BlueMapProxyServlet(bluemapBaseUrl)), "/bluemap/*");
        //commands
        context.addServlet(new ServletHolder(new CommandServlet(botManager)), "/api/command");

        instance = this;
    }

    public static BotWebService getInstance() {
        return instance;
    }


    public static String getServerHost() { return BotWebService.getInstance().SERVER_HOST; }
    public static String getMapHost() { return BotWebService.getInstance().MAP_HOST; }

    public void start() throws Exception { server.start(); }
    public void stop() throws Exception { server.stop(); }

    private static class StaticFileServlet extends HttpServlet {
        private static final String ASSETS_PATH = BotConstants.PLUGIN_PATH + "/web/assets/";
        @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
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
            if (path.endsWith(".css")) resp.setContentType("text/css");
            else if (path.endsWith(".js")) resp.setContentType("application/javascript");

            try (OutputStream os = resp.getOutputStream()) {
                Files.copy(assetFile.toPath(), os);
            }
        }
    }

    private static class MainPageServlet extends HttpServlet {
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("text/html");
            resp.setCharacterEncoding("UTF-8");
    
            File file = new File(BotConstants.PLUGIN_PATH + "/web/template.html");

            if (!file.exists()) {
                BotLogger.info(true, "⚠ template.html not found on disk: " + file.getAbsolutePath());
                resp.getWriter().println("Error: template.html not found on disk.");
                return;
            } else {
                BotLogger.info(true, "⚠ template.html was found on disk: " + file.getAbsolutePath());
            }
    
            try {
                String html = Files.readString(file.toPath(), StandardCharsets.UTF_8);
                                   //.replace("{{MAP_HOST}}", BotWebService.getInstance().MAP_HOST);

                //BotLogger.info(true, html);                   

                resp.getWriter().println(html);


            } catch (IOException e) {
                BotLogger.info(true, "❌ Error reading template.html: " + e.getMessage());
                resp.getWriter().println("Error reading template.html: " + e.getMessage());
            }
        }
    }


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

    private static class BotStatusServlet extends HttpServlet {
        private final BotManager botManager;
        public BotStatusServlet(BotManager botManager) { this.botManager = botManager; }
        @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setHeader("Access-Control-Allow-Origin", "*");

            JsonObject result = new JsonObject();
            JsonArray botsArray = new JsonArray();
            Collection<Bot> bots = botManager.getAllBots();

            for (Bot bot : bots) {
                JsonObject botJson = new JsonObject();
                Location loc = bot.getRuntimeStatus().getCurrentLocation();
                if (loc != null) {
                    botJson.addProperty("skin", "http://" + getServerHost() + ":3000/skins/" + bot.getUuid() + ".png");
                    botJson.addProperty("id", bot.getId());
                    botJson.addProperty("position", BotStringUtils.formatLocation(loc));
                    botJson.addProperty("task", bot.getCurrentTask().getName());
                    botJson.addProperty("object", getCurrentObjective(bot));
                    Location tg_loc = bot.getRuntimeStatus().getTargetLocation();
                    botJson.addProperty("target", BotStringUtils.formatLocation(tg_loc));
                    botJson.addProperty("elapsedTime", BotStringUtils.formatTime(bot.getCurrentTask().getElapsedTime()));

                    List<BotTask> taskStack = (bot.getLifeCycle() != null && bot.getLifeCycle().getTaskStackManager() != null)
                        ? new ArrayList<>(bot.getLifeCycle().getTaskStackManager().getTaskStack())
                        : new ArrayList<>();
                    String taskStackText = taskStack.isEmpty() ? "N/A" :
                        taskStack.stream().map(BotTask::getName).collect(Collectors.joining("➜"));
                    botJson.addProperty("queue", taskStackText);
                    botsArray.add(botJson);


                    // 📦 Serialize inventory
                    ItemStack[] contents = bot.getInventory().getNPCInventory().getContents();
                    JsonArray inventoryArray = new JsonArray();

                    for (ItemStack item : contents) {
                        if (item != null && item.getAmount() > 0) {
                            JsonObject slotObj = new JsonObject();
                            slotObj.addProperty("type", item.getType().toString().toLowerCase());
                            slotObj.addProperty("amount", item.getAmount());
                            inventoryArray.add(slotObj);
                        }
                    }

                    botJson.add("inventorySlotsFilled", inventoryArray);

                    int count = Arrays.stream(contents)
                                    .filter(Objects::nonNull)
                                    .mapToInt(ItemStack::getAmount)
                                    .sum();

                    botJson.addProperty("inventoryCount", count);
                    botJson.addProperty("inventoryMax", 36); // или сколько слотов у тебя по факту

                }
            }

            result.add("bots", botsArray);
            resp.getWriter().write(result.toString());
        }
    }

    private static String getCurrentObjective(Bot bot) {
        BotTask currentTask = bot.getCurrentTask();
        return (currentTask != null) ? currentTask.getObjective() : "";
    }

    private static class SkinServlet extends HttpServlet {
        @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
            String path = req.getPathInfo();
            if (path == null || path.length() <= 1) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid skin request");
                return;
            }
            File skinFile = new File(BotWebService.getInstance().SKIN_PATH + path.substring(1));
            if (!skinFile.exists()) skinFile = new File(BotWebService.getInstance().SKIN_PATH + "default-bot.png");
            resp.setContentType("image/png");
            try (OutputStream os = resp.getOutputStream()) {
                Files.copy(skinFile.toPath(), os);
            }
        }
    }

    private class BlueMapProxyServlet extends HttpServlet {
        private String bluemapBaseUrl;

        public BlueMapProxyServlet(String bluemapBaseUrl) {
            this.bluemapBaseUrl = bluemapBaseUrl;
        }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI().replaceFirst("/bluemap", "");
        String query = req.getQueryString();
        String url = bluemapBaseUrl + path + (query != null ? "?" + query : "");

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(req.getMethod());

        conn.setDoInput(true);
        conn.setDoOutput(false);

        // Копируем заголовки запроса
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            if ("host".equalsIgnoreCase(header)) continue;
            conn.setRequestProperty(header, req.getHeader(header));
        }

        int status = conn.getResponseCode();
        resp.setStatus(status);

        // Копируем заголовки ответа
        conn.getHeaderFields().forEach((key, values) -> {
            if (key != null) {
                for (String v : values) {
                    resp.addHeader(key, v);
                }
            }
        });

        // Пересылаем тело ответа
        try (InputStream in = conn.getInputStream();
             OutputStream out = resp.getOutputStream()) {
            in.transferTo(out);
        } catch (IOException e) {
            // Может быть тело отсутствует (например, 304 Not Modified)
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}
}