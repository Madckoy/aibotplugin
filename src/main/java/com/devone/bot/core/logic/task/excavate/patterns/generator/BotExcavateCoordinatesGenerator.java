package com.devone.bot.core.logic.task.excavate.patterns.generator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import com.devone.bot.core.logic.task.excavate.patterns.generator.params.BotExcavatePatternGenerationParams;
import com.devone.bot.utils.blocks.BotLocation;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

public class BotExcavateCoordinatesGenerator {

    private final List<Expression> filterExpressions;
    private final Expression sortExpression;
    private boolean inverted;

    public BotExcavateCoordinatesGenerator(List<String> filterExpressions, String sortExpression, boolean inverted) {
        this.filterExpressions = filterExpressions.stream()
                .map(expr -> AviatorEvaluator.compile(expr, true))
                .collect(Collectors.toList());

        this.sortExpression = AviatorEvaluator.compile(sortExpression, true);

        this.inverted = inverted;
    }

    public static BotExcavateCoordinatesGenerator loadYmlFromStream(InputStream input) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(input);
        @SuppressWarnings("unchecked")
        List<String> filters = (List<String>) data.getOrDefault("filters", new ArrayList<>());
        @SuppressWarnings("unchecked")
        List<String> sortList = (List<String>) data.getOrDefault("sort", Collections.singletonList("y"));
        String sort = sortList.get(0);

        // Загрузка свойства inverted с значением по умолчанию false
        boolean inverted = (boolean) data.getOrDefault("inverted", false);

        return new BotExcavateCoordinatesGenerator(filters, sort, inverted);
    }

    public List<BotLocation> generateInnerPoints(BotExcavatePatternGenerationParams params) {
        int ox = params.observerX;
        int oy = params.observerY;
        int oz = params.observerZ;
        
        int innerRadius = params.innerRadius;
        
        int offsetX = params.offsetX;
        int offsetY = params.offsetY;
        int offsetZ = params.offsetZ;

        int[] center = computeFigureCenter(ox, oy, oz, offsetX, offsetY, offsetZ);

        return generateInnerPoints(center[0], center[1], center[2], innerRadius);
    }

    private int[] computeFigureCenter(int ox, int oy, int oz, int offsetX, int offsetY, int offsetZ) {

        int cx = ox + offsetX;
        int cy = oy + offsetY;
        int cz = oz + offsetZ;

        return new int[]{cx, cy, cz};
    }

    
    private List<BotLocation> generateInnerPoints(int cx, int cy, int cz, int inner_radius) {
        List<BotLocation> result = new ArrayList<>();
        Map<String, Object> env = new HashMap<>();

        for (int y = cy - inner_radius; y <= cy + inner_radius; y++) {
            for (int x = cx - inner_radius; x <= cx + inner_radius; x++) {
                for (int z = cz - inner_radius; z <= cz + inner_radius; z++) {
                    env.put("x", x);
                    env.put("y", y);
                    env.put("z", z);
                    env.put("cx", cx);
                    env.put("cy", cy);
                    env.put("cz", cz);
                    env.put("r", inner_radius);

                    if (applyFilters(env)) {
                        result.add(new BotLocation(x, y, z));
                    }
                }
            }
        }

        result.sort(Comparator.comparingDouble(loc -> {
            env.put("x", loc.getX());
            env.put("y", loc.getY());
            env.put("z", loc.getZ());
            return ((Number) sortExpression.execute(env)).doubleValue();
        }));

        return result;
    }

    public List<BotLocation> generateOuterPoints(BotExcavatePatternGenerationParams params) {
        int ox = params.observerX;
        int oy = params.observerY;
        int oz = params.observerZ;
        

        int outerRadius = params.outerRadius;
        
        int offsetX = params.offsetX;
        int offsetY = params.offsetY;
        int offsetZ = params.offsetZ;

        int[] center = computeFigureCenter(ox, oy, oz, offsetX, offsetY, offsetZ);

        return generateOuterPoints(center[0], center[1], center[2], outerRadius);
    }

    private List<BotLocation> generateOuterPoints(int cx, int cy, int cz, int radius) {
        List<BotLocation> result = new ArrayList<>();
        for (int y = cy - radius; y <= cy + radius; y++) {
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    result.add(new BotLocation(x, y, z));
                }
            }
        }
        return result;
    }



    private boolean applyFilters(Map<String, Object> env) {

        for (Expression expr : filterExpressions) {
            Object result = expr.execute(env);
            if (!(result instanceof Boolean) || !(Boolean) result) {
                return false;
            }
        }
        return true;
    }

    public boolean getInverted() {
        return this.inverted;
    }
}
