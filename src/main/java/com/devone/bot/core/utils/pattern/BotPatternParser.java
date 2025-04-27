package com.devone.bot.core.utils.pattern;

import com.devone.bot.core.utils.blocks.BotPosition;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BotPatternParser {

    public static class BotPatternParserResult {
        public List<BotPosition> allPoints = new ArrayList<>();
        public List<BotPosition> solidPoints = new ArrayList<>();
        public List<BotPosition> voidPoints = new ArrayList<>();
        public BotPosition offset = new BotPosition();
    }

    public static BotPatternParserResult parse(BotPattern pattern, BotPosition observerPosition) {
        BotPatternParserResult result = new BotPatternParserResult();

        Map<Integer, List<String>> layers = pattern.getLayers();
        BotPosition offset = pattern.getOffset() != null ? pattern.getOffset() : new BotPosition();
        result.offset = new BotPosition(offset);

        double centerX = 0;
        double centerZ = 0;
        if (!layers.isEmpty()) {
            List<String> anyLayer = layers.values().iterator().next();
            centerX = (anyLayer.get(0).length() / 2.0) - 0.5;
            centerZ = (anyLayer.size() / 2.0) - 0.5;
        }

        for (Map.Entry<Integer, List<String>> layerEntry : layers.entrySet()) {
            int layerIndex = layerEntry.getKey(); // теперь int, а не double!
            List<String> rows = layerEntry.getValue();

            for (int z = 0; z < rows.size(); z++) {
                String row = rows.get(z);
                for (int x = 0; x < row.length(); x++) {
                    char symbol = row.charAt(x);

                    double relX = x - centerX;
                    double relZ = z - centerZ;

                    double finalX = observerPosition.getX() + relX + offset.getX();
                    double finalY = observerPosition.getY() + layerIndex + offset.getY();
                    double finalZ = observerPosition.getZ() + relZ + offset.getZ();

                    BotPosition pos = new BotPosition(finalX, finalY, finalZ);

                    result.allPoints.add(pos);

                    if (symbol == '◼') {
                        result.solidPoints.add(pos);
                    } else if (symbol == '☐') {
                        result.voidPoints.add(pos);
                    }
                }
            }
        }

        return result;
    }
}
