package com.devone.bot.core.utils.pattern;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BotPatternLoader {
    public static BotPattern load(File file) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules(); // Чтобы маппить BotPosition нормально
        return mapper.readValue(file, BotPattern.class);
    }
}
