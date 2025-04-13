package com.devone.bot.utils;

public class BotConstants {
    public static final String  PLUGIN_NAME                  = "AIBotPlugin";
    public static final String  PLUGIN_PATH                  = "plugins/" + PLUGIN_NAME;
    public static final String  PLUGIN_PATH_CONFIGS          = PLUGIN_PATH + "/cfg";
    public static final String  PLUGIN_PATH_CONFIGS_TASKS    = PLUGIN_PATH_CONFIGS + "/tasks/";
    public static final String  PLUGIN_PATH_WEB_ASSETS       = PLUGIN_PATH + "/web/assets"; 
    public static final String  PLUGIN_PATH_LOGS             = PLUGIN_PATH+"/logs/";
    public static final String  PLUGIN_TMP                   = PLUGIN_PATH+"/tmp/";
    public static final boolean FLIP_COORDS                  = false;
    public static final String  PLUGIN_PATH_PATTERNS_BREAK   = PLUGIN_PATH + "/patterns/break/";
    public static final String  RESOURCE_PATH_PATTERNS_BREAK = "patterns/break/";


    public static int DEFAULT_SCAN_RANGE = 5;
    public static final String  DEFAULT_PATTERN_BREAK = "default.yml";
    public static final long DEFAULT_TASK_TIMEOUT = 30_000; // 30 sec
}
