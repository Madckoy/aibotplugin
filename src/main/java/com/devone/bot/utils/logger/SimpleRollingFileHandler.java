package com.devone.bot.utils.logger;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class SimpleRollingFileHandler extends Handler {

    private final File logFile;
    private final int maxFileSize; // в байтах
    private final int maxBackupCount;
    private OutputStream outputStream;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public SimpleRollingFileHandler(String filePath, int maxFileSize, int maxBackupCount) throws IOException {
        this.logFile = new File(filePath);
        this.maxFileSize = maxFileSize;
        this.maxBackupCount = maxBackupCount;
        openStream();
        setFormatter(new SimpleFormatter());
    }

    private void openStream() throws IOException {
        logFile.getParentFile().mkdirs();
        outputStream = new FileOutputStream(logFile, true);
    }

    private void rotateLogsIfNeeded() throws IOException {
        if (logFile.length() < maxFileSize)
            return;

        if (outputStream != null) {
            outputStream.close();
        }

        // Удаляем самый старый файл, если превышен лимит
        File oldestFile = new File(logFile.getPath() + "." + maxBackupCount);
        if (oldestFile.exists()) {
            oldestFile.delete();
        }

        // Сдвигаем старые файлы (n-1 -> n)
        for (int i = maxBackupCount - 1; i >= 1; i--) {
            File src = new File(logFile.getPath() + "." + i);
            if (src.exists()) {
                src.renameTo(new File(logFile.getPath() + "." + (i + 1)));
            }
        }

        // Текущий лог переименовываем в console.log.1
        File newLogFile = new File(logFile.getPath() + ".1");
        logFile.renameTo(newLogFile);

        // Открываем новый пустой лог-файл
        openStream();
    }

    @Override
    public synchronized void publish(LogRecord record) {
        if (!isLoggable(record)) return;

        String date = dateFormat.format(new Date(record.getMillis()));
        String message = String.format("%s [%s] %s%n",
                date, record.getLevel().getName(), getFormatter().formatMessage(record));

        try {
            outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            rotateLogsIfNeeded(); // проверка на ротацию после каждой записи
        } catch (IOException e) {
            System.err.println("❌ Log write error: " + e.getMessage());
        }
    }

    @Override
    public void flush() {
        try {
            outputStream.flush();
        } catch (IOException e) {
            System.err.println("❌ Log flush error: " + e.getMessage());
        }
    }

    @Override
    public void close() throws SecurityException {
        try {
            outputStream.close();
        } catch (IOException e) {
            System.err.println("❌ Log close error: " + e.getMessage());
        }
    }
}
