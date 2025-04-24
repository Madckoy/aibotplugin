package com.devone.bot.core.web;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.BotManager;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.web.servlets.BlueMapProxyServlet;
import com.devone.bot.core.web.servlets.BotStatusServlet;
import com.devone.bot.core.web.servlets.CommandServlet;
import com.devone.bot.core.web.servlets.MainPageServlet;
import com.devone.bot.core.web.servlets.SkinServlet;
import com.devone.bot.core.web.servlets.StaticFileServlet;
import com.devone.bot.plugin.config.AIBotPluginConfig;


public class BotWebService {
    private final Server server;
    private final String SKIN_PATH = BotConstants.PLUGIN_PATH + "/web/skins/";
    //private final String CONFIG_PATH = BotConstants.PLUGIN_PATH + "/config.json";

    public String SERVER_HOST = "localhost";
    public int SERVER_PORT = 3000;

    public String MAP_HOST = "localhost";
    public int MAP_PORT = 8100;

    private static BotWebService instance = null;

    public BotWebService (AIBotPlugin plugin, BotManager botManager) {

        AIBotPluginConfig pluginConfig = plugin.getConfigManager().getConfig();

        // Используем новый JSON-конфиг вместо старого YAML
        SERVER_HOST = pluginConfig.server.web_host;
        SERVER_PORT = Integer.valueOf(pluginConfig.server.web_port);
        MAP_HOST = pluginConfig.server.map_host;
        MAP_PORT = Integer.valueOf(pluginConfig.server.map_port);

        String bluemapBaseUrl = "http://" + MAP_HOST + ":" + MAP_PORT;

        BotLogger.debug("🌐",true, "SERVER_HOST: " + SERVER_HOST);
        BotLogger.debug("🌐",true, "SERVER_PORT: " + SERVER_PORT);
        BotLogger.debug("🌐",true, "MAP_HOST: " + MAP_HOST);
        BotLogger.debug("🌐",true, "MAP_PORT: " + MAP_PORT);
        BotLogger.debug("🧭",true, "BlueMap Proxy Target: " + bluemapBaseUrl);

        this.server = new Server(SERVER_PORT);

        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);

        context.addServlet(new ServletHolder(new MainPageServlet()), "/");
        context.addServlet(new ServletHolder(new BotStatusServlet(botManager)), "/status");
        context.addServlet(new ServletHolder(new SkinServlet()), "/skins/*");
        context.addServlet(new ServletHolder(new StaticFileServlet()), "/assets/*");
        context.addServlet(new ServletHolder(new BlueMapProxyServlet(bluemapBaseUrl)), "/bluemap/*");
        context.addServlet(new ServletHolder(new CommandServlet(botManager)), "/api/command");

        instance = this;
    }


    public static BotWebService getInstance() {
        return instance;
    }

    public static String getServerHost() { return BotWebService.getInstance().SERVER_HOST; }
    public static int getServerPort() { return BotWebService.getInstance().SERVER_PORT; }
    public static String getMapHost() { return BotWebService.getInstance().MAP_HOST; }
    public static int getMapPort() { return BotWebService.getInstance().MAP_PORT; }
    public static String getSkinPath() { return BotWebService.getInstance().SKIN_PATH; }


    public void start() throws Exception { server.start(); }
    public void stop() throws Exception { server.stop(); }

}