package com.devone.bot.core.web.servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import com.devone.bot.core.bot.brain.logic.utils.BotConstants;
import com.devone.bot.core.bot.brain.logic.utils.logger.BotLogger;

public class MainPageServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=UTF-8");

        File file = new File(BotConstants.PLUGIN_PATH + "/web/template.html");
        if (!file.exists()) {
            BotLogger.debug("⚠️", true, "template.html not found: " + file.getAbsolutePath());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "template.html not found");
            return;
        }

        String html = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        resp.getWriter().println(html);
    }
}
