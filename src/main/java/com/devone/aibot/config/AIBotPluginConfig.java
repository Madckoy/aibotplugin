package com.devone.aibot.config;

public class AIBotPluginConfig {
    public LoggingConfig logging = new LoggingConfig();
    public ServerConfig server = new ServerConfig();

    public static class LoggingConfig {
        public boolean enable = true;
        public String level = "INFO";
    }

    public static class ServerConfig {
        public String web_host = "127.0.0.1";
        public int web_port = 3000;
        public String map_host = "127.0.0.1";
        public int map_port = 8100;
    }
}
