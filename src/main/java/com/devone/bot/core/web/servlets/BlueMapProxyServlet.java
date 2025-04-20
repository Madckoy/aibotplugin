package com.devone.bot.core.web.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Enumeration;

public class BlueMapProxyServlet extends HttpServlet {
    private String bluemapBaseUrl;

    public BlueMapProxyServlet(String bluemapBaseUrl) {
        this.bluemapBaseUrl = bluemapBaseUrl;
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getRequestURI().replaceFirst("/bluemap", "");
        String query = req.getQueryString();
        String url = bluemapBaseUrl + path + (query != null ? "?" + query : "");

        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod(req.getMethod());

        conn.setDoInput(true);
        conn.setDoOutput(false);

        // Копируем заголовки запроса
        Enumeration<String> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            if ("host".equalsIgnoreCase(header)) continue;
            conn.setRequestProperty(header, req.getHeader(header));
        }

        int status = conn.getResponseCode();
        resp.setStatus(status);

        // Копируем заголовки ответа
        conn.getHeaderFields().forEach((key, values) -> {
            if (key != null) {
                for (String v : values) {
                    resp.addHeader(key, v);
                }
            }
        });

        // Пересылаем тело ответа
        try (InputStream in = conn.getInputStream();
            OutputStream out = resp.getOutputStream()) {
            in.transferTo(out);
        } catch (IOException e) {
            // Может быть тело отсутствует (например, 304 Not Modified)
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }
}