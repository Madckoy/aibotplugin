package com.devone.aibot.utils;

public class BotTunnelMode {

    public enum TunnelMode {
        FLOOR,      // Бот у нижней грани куба (копает "вверх")
        CENTER,     // Бот копает как червь
        EDGE        // Бот на одной из граней куба (тоннель сбоку)
    }
}
