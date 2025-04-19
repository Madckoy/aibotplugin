package com.devone.bot.core.utils;

public class BotConstants {
    public static final String  PLUGIN_NAME                  = "AIBotPlugin";
    public static final String  PLUGIN_PATH                  = "plugins/" + PLUGIN_NAME;
    public static final String  PLUGIN_PATH_CONFIGS          = PLUGIN_PATH + "/config";
    public static final String  PLUGIN_PATH_CONFIGS_TASKS    = PLUGIN_PATH_CONFIGS + "/tasks/";
    public static final String  PLUGIN_PATH_WEB_ASSETS       = PLUGIN_PATH + "/web/assets"; 
    public static final String  PLUGIN_PATH_LOGS             = PLUGIN_PATH+"/logs/";
    public static final String  PLUGIN_TMP                   = PLUGIN_PATH+"/tmp/";
    public static final boolean FLIP_COORDS                  = false;
    public static final String  PLUGIN_PATH_PATTERNS_BREAK   = PLUGIN_PATH + "/patterns/excavation/";
    public static final String  RESOURCE_PATH_PATTERNS_BREAK = "patterns/break/";


    public static final int     DEFAULT_SCAN_RANGE = 8;
    public static final int     DEFAULT_SCAN_DATA_SLICE_HEIGHT = 4;
    
    public static final long    DEFAULT_TASK_TIMEOUT = 30_000; // 30 sec
    public static final long    DEFAULT_IDLE_TIMEOUT = 10_000; // 10 sec
    
    public static final double  DEFAULT_HAND_DAMAGE = 5.0;

    public static final int     DEFAULT_OUTER_RADIUS = 4;
    public static final int     DEFAULT_INNER_RADIUS = 4;
    public static final String  DEFAULT_PATTERN_BREAK = "cone.yml";

    public static final double  DEFAULT_DETECTION_RADIUS = 10.0;


    
}
