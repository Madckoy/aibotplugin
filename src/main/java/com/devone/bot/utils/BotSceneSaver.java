package com.devone.bot.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class BotSceneSaver {

    public static void saveToJsonFile(String filePath, BotSceneData blocks) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.enable(SerializationFeature.INDENT_OUTPUT); // Читабельный JSON
        mapper.writeValue(new File(filePath), blocks);
    }
}