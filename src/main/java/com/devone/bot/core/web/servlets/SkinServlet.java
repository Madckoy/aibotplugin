package com.devone.bot.core.web.servlets;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

import com.devone.bot.core.web.BotWebService;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SkinServlet extends HttpServlet {
    @SuppressWarnings("static-access")
    @Override protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        if (path == null || path.length() <= 1) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid skin request");
            return;
        }
        File skinFile = new File(BotWebService.getInstance().getSkinPath() + path.substring(1));
        if (!skinFile.exists()) skinFile = new File(BotWebService.getInstance().getSkinPath() + "default-bot.png");
        resp.setContentType("image/png");
        try (OutputStream os = resp.getOutputStream()) {
            Files.copy(skinFile.toPath(), os);
        }
    }
}