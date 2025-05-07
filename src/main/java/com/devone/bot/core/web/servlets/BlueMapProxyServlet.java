package com.devone.bot.core.web.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;

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
        try {
            String path = req.getRequestURI().replaceFirst("/bluemap", "");
            String query = req.getQueryString();

            URI uri = new URI(
                "http",                // схема (http, не https)
                null,                  // user info
                bluemapBaseUrl,        // хост
                -1,                    // порт (по умолчанию)
                path,                  // путь
                query,                 // query string
                null                   // fragment
            );

            HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
            conn.setRequestMethod(req.getMethod());
            conn.setDoInput(true);
            conn.setDoOutput(false);

            Enumeration<String> headerNames = req.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String header = headerNames.nextElement();
                if ("host".equalsIgnoreCase(header)) continue;
                conn.setRequestProperty(header, req.getHeader(header));
            }

            int status = conn.getResponseCode();
            resp.setStatus(status);

            conn.getHeaderFields().forEach((key, values) -> {
                if (key != null) {
                    for (String v : values) {
                        resp.addHeader(key, v);
                    }
                }
            });

            try (InputStream in = conn.getInputStream(); OutputStream out = resp.getOutputStream()) {
                in.transferTo(out);
            } catch (IOException e) {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("❌ Invalid BlueMap URL request: " + e.getMessage());
        }
    }

}