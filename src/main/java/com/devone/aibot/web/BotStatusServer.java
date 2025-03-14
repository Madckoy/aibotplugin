package com.devone.aibot.web;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotManager;
import com.devone.aibot.core.logic.tasks.BotTask;
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
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

public class BotStatusServer {
    private final Server server;
    private final String serverIp; // ✅ Stores the internal server IP

    public BotStatusServer(int port, BotManager botManager) {

        this.server = new Server(port);
        this.serverIp = getLocalIpAddress(); // ✅ Gets the internal server IP

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        // ✅ Main HTML Page (Injects IP dynamically)
        context.addServlet(new ServletHolder(new MainPageServlet(serverIp)), "/");

        // ✅ API endpoint for bot status
        context.addServlet(new ServletHolder(new BotStatusServlet(botManager)), "/status");
    }

    // ✅ Gets the local server IP address dynamically
    private String getLocalIpAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()) {
                        return addr.getHostAddress(); // ✅ Uses the internal IP
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "127.0.0.1"; // Fallback to localhost if no valid IP is found
    }

    public void start() throws Exception {
        server.start();
    }

    public void stop() throws Exception {
        server.stop();
    }

    // ✅ Serves the dynamically generated main page with correct server IP
    private class MainPageServlet extends HttpServlet {
        private final String serverIp;

        public MainPageServlet(String serverIp) {
            this.serverIp = serverIp;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("text/html");
            resp.setCharacterEncoding("UTF-8");

            // ✅ Load the template from resources
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("web/template.html");
            if (inputStream == null) {
                resp.getWriter().println("Error: template.html not found.");
                return;
            }

            // ✅ Read file content and replace {{SERVER_IP}}
            String html = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"))
                    .replace("{{SERVER_IP}}", serverIp);

            resp.getWriter().println(html);
        }
    }

    // ✅ API /status — Returns JSON with bot data
    private static class BotStatusServlet extends HttpServlet {
        private final BotManager botManager;

        public BotStatusServlet(BotManager botManager) {
            this.botManager = botManager;
        }

        @Override
        protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");

            // ✅ Fix CORS issue for external access
            resp.setHeader("Access-Control-Allow-Origin", "*");

            JsonObject result = new JsonObject();
            JsonArray botsArray = new JsonArray();

            Collection<Bot> bots = botManager.getAllBots();
            for (Bot bot : bots) {

                JsonObject botJson = new JsonObject();

                Location  loc = bot.getNPCCurrentLocation();

                if (loc != null) {

                    botJson.addProperty("id", bot.getId());
                    String currentLoc = loc.getBlockX() +" , "+loc.getBlockY()+" , "+ loc.getBlockZ();
                    botJson.addProperty("position", currentLoc);
                    botJson.addProperty("task", bot.getCurrentTask().getName());
                    Location tg_loc = bot.getCurrentTask().getTargetLocation();
                    String targetLoc = tg_loc.getBlockX() +" , "+tg_loc.getBlockY()+" , "+ tg_loc.getBlockZ();
                    botJson.addProperty("target", targetLoc);

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

                    // ✅ Добавлено время выполнения таска
                    botJson.addProperty("elapsedTime", bot.getCurrentTask().getElapsedTime());

                    botsArray.add(botJson);
                }
            }

            result.add("bots", botsArray);
            resp.getWriter().write(result.toString());
        }
    }
}
