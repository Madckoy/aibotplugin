package com.devone.bot.core.brain.perseption.scene;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;

public class BotSceneSaver {

    public static void saveToJsonFile(String filePath, BotSceneData blocks) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        //mapper.enable(SerializationFeature.INDENT_OUTPUT); // Читабельный JSON
        mapper.writeValue(new File(filePath), blocks);
    }
}