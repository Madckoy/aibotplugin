package com.devone.bot.plugin.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class AIBotBaseJsonConfig<T> {
    
    private transient final File file;
    private final Class<T> clazz;
    private final Gson gson;
    private T config;

    public AIBotBaseJsonConfig(File file, Class<T> clazz) {
        this.file = file;
        this.clazz = clazz;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public T loadOrCreate() {
        if (file.exists()) {
            try (Reader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
                this.config = gson.fromJson(reader, clazz);
            } catch (IOException e) {
                System.err.println("❌ Failed to read config: " + file.getName() + " → " + e.getMessage());
            }
        }

        if (this.config == null) {
            try {
                this.config = clazz.getDeclaredConstructor().newInstance();
                save(); // сразу сохранить дефолт
                System.out.println("📄 Created new config: " + file.getName());
            } catch (Exception e) {
                throw new RuntimeException("❌ Failed to create config for class: " + clazz.getName(), e);
            }
        }

        return config;
    }

    public void save() {
        try {
            // ✅ Проверка и создание директории, если её нет
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                boolean created = parent.mkdirs();
                if (created) {
                    System.out.println("📁 Создана директория: " + parent.getAbsolutePath());
                } else {
                    System.err.println("❌ Не удалось создать директорию: " + parent.getAbsolutePath());
                }
            }
    
            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)) {
                gson.toJson(config, writer);
            }
        } catch (IOException e) {
            System.err.println("❌ Failed to save config: " + file.getName() + " → " + e.getMessage());
        }
    }


    public void deleteAndRegenerate() {
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("🗑 Deleted config file: " + file.getName());
            } else {
                System.err.println("❌ Failed to delete config file: " + file.getName());
            }
        }
        loadOrCreate();
    }

    public T get() {
        return config;
    }

    public File getFile() {
        return file;
    }
}
