package com.devone.aibot.utils;

import java.lang.reflect.Method;

import org.bukkit.Bukkit;



public class ServerUtils {

    private static volatile boolean stopping = false;


public static void onDisable() {
    stopping = true;
}

public static boolean isStopping() {
    return stopping;
}

    public static boolean isServerStopping() {
        try {
            // Проверим, есть ли метод isStopping() через reflection
            Method isStoppingMethod = Bukkit.getServer().getClass().getMethod("isStopping");
            return (boolean) isStoppingMethod.invoke(Bukkit.getServer());
        } catch (NoSuchMethodException e) {
            // Метод не существует — значит, это не Paper
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}