package com.devone.bot.core.utils.server;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.jar.*;
import java.util.logging.Logger;

public class BotResourceExtractor {

    private static final Logger logger = Logger.getLogger("AIBotPlugin");

    /**
     * Копирует ресурсы из JAR по указанному пути, фильтруя по расширениям и учитывая перезапись.
     */
    public static void copyDirectoryFromJar(String resourceSubPath, String targetDirPath, boolean forceOverride, Set<String> allowedExtensions) {
        try {
            URL resourceURL = BotResourceExtractor.class.getClassLoader().getResource(resourceSubPath);
            if (resourceURL == null) {
                logger.warning("❌ Resource path not found: " + resourceSubPath);
                return;
            }

            if ("jar".equals(resourceURL.getProtocol())) {
                copyFromJar(resourceSubPath, targetDirPath, forceOverride, allowedExtensions);
            } else {
                throw new UnsupportedOperationException("Unsupported resource protocol: " + resourceURL.getProtocol());
            }

        } catch (Exception e) {
            logger.severe("❌ Error copying resource '" + resourceSubPath + "': " + e.getMessage());
        }
    }

    private static void copyFromJar(String resourceSubPath, String targetDirPath, boolean forceOverride, Set<String> allowedExtensions) throws IOException {
        String jarPath = BotResourceExtractor.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        jarPath = URLDecoder.decode(jarPath, StandardCharsets.UTF_8);
        try (JarFile jar = new JarFile(jarPath)) {
            Enumeration<JarEntry> entries = jar.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();

                if (!name.startsWith(resourceSubPath + "/") || entry.isDirectory()) continue;

                // Фильтрация по расширениям
                if (!allowedExtensions.isEmpty() && allowedExtensions.stream().noneMatch(name::endsWith)) {
                    continue;
                }

                try (InputStream in = jar.getInputStream(entry)) {
                    String relativePath = name.substring(resourceSubPath.length() + 1);
                    File targetFile = new File(targetDirPath, relativePath);
                    createParentDirs(targetFile);

                    if (!targetFile.exists() || forceOverride) {
                        Files.copy(in, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        logger.info("✅ Copied: " + name + " → " + targetFile.getPath());
                    } else {
                        logger.fine("⏭ Skipped existing file: " + targetFile.getPath());
                    }
                }
            }
        }
    }

    private static void createParentDirs(File file) {
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            if (!parent.mkdirs()) {
                logger.warning("⚠️ Could not create directories for: " + parent.getPath());
            }
        }
    }
}
