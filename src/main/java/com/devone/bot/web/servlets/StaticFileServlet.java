package com.devone.bot.web.servlets;

import jakarta.servlet.http.*;
import java.io.*;
import java.nio.file.Files;

import com.devone.bot.core.utils.BotConstants;

public class StaticFileServlet extends HttpServlet {
    private static final String ASSETS_PATH = BotConstants.PLUGIN_PATH + "/web/assets/";

    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid asset request");
            return;
        }
        File assetFile = new File(ASSETS_PATH + path.substring(1));
        if (!assetFile.exists()) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
            return;
        }
        if (path.endsWith(".css")) resp.setContentType("text/css");
        else if (path.endsWith(".js")) resp.setContentType("application/javascript");

        try (OutputStream os = resp.getOutputStream()) {
            Files.copy(assetFile.toPath(), os);
        }
    }
}
