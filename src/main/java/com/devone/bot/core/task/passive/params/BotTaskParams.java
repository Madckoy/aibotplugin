package com.devone.bot.core.task.passive.params;

import com.devone.bot.core.utils.BotConstants;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public abstract class BotTaskParams implements IBotTaskParams {

    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .create();

    private String icon = "☑️";
    private String objective = "Do something abstract";
    private boolean isEnabled = false;
    private boolean isLogging = false;
    private long timeout = BotConstants.DEFAULT_TASK_TIMEOUT;

    private transient File configFile;

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }

    public static <T extends BotTaskParams> T loadOrCreate(Class<T> clazz) {
        String fileName = clazz.getSimpleName() + ".json";
        File configFolder = new File(BotConstants.PLUGIN_PATH_CONFIGS_TASKS);
        if (!configFolder.exists()) configFolder.mkdirs();

        File file = new File(configFolder, fileName);

        try {
            if (!file.exists()) {
                // Файл не существует — создаём дефолтный экземпляр и записываем его
                T instance = clazz.getDeclaredConstructor().newInstance();
                instance.setConfigFile(file);
                instance.save();
                return instance;
            } else {
                try (Reader reader = new FileReader(file)) {
                    T loaded = GSON.fromJson(reader, clazz);

                    if (loaded == null) {
                        // Повреждённый файл — удаляем
                        file.delete();
                        throw new RuntimeException("Конфигурация повреждена. Файл удалён: " + file.getName());
                    }

                    loaded.setConfigFile(file);
                    return loaded;
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при загрузке параметров: " + clazz.getSimpleName(), e);
        }
    }

    public void save() {
        if (configFile == null) return;
        try (Writer writer = new FileWriter(configFile)) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении параметров: " + configFile.getName(), e);
        }
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnable(boolean enabled) {
        this.isEnabled = enabled;
    }

    public boolean isLogging() {
        return isLogging;
    }

    public void setIsLogging(boolean logging) {
        this.isLogging = logging;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
