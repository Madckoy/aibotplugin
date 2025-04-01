package com.devone.aibot.core.logic.patterns;

import com.devone.aibot.utils.BotCoordinate3D;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class BotCoordinatesGenerator {
    private final List<Expression> filterExpressions;
    private final Expression sortExpression;

    public BotCoordinatesGenerator(List<String> filterExpressions, String sortExpression) {
        this.filterExpressions = filterExpressions.stream()
                .map(expr -> AviatorEvaluator.compile(expr, true))
                .collect(Collectors.toList());
        this.sortExpression = AviatorEvaluator.compile(sortExpression, true);
    }

    public static BotCoordinatesGenerator loadYmlFromStream(InputStream input) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(input);
        List<String> filters = (List<String>) data.getOrDefault("filters", new ArrayList<>());
        List<String> sortList = (List<String>) data.getOrDefault("sort", Collections.singletonList("y"));
        String sort = sortList.get(0);

        return new BotCoordinatesGenerator(filters, sort);
    }

    public List<BotCoordinate3D> generateInnerPoints(BotPatternGenerationParams params) {

        return generateInnerPoints(params.getCenterX(), params.getCenterY(), params.getCenterZ(),
                                   params.outerRadius, params.innerRadius);
    }
    
    private List<BotCoordinate3D> generateInnerPoints(int cx, int cy, int cz, int outerRadius, int innerRadius) {
        List<BotCoordinate3D> result = new ArrayList<>();
        Map<String, Object> env = new HashMap<>();

        for (int y = cy - outerRadius; y <= cy + outerRadius; y++) {
            for (int x = cx - outerRadius; x <= cx + outerRadius; x++) {
                for (int z = cz - outerRadius; z <= cz + outerRadius; z++) {
                    env.put("x", x);
                    env.put("y", y);
                    env.put("z", z);
                    env.put("cx", cx);
                    env.put("cy", cy);
                    env.put("cz", cz);
                    env.put("r", innerRadius);

                    if (applyFilters(env)) {
                        result.add(new BotCoordinate3D(x, y, z));
                    }
                }
            }
        }

        result.sort(Comparator.comparingDouble(loc -> {
            env.put("x", loc.x);
            env.put("y", loc.y);
            env.put("z", loc.z);
            return ((Number) sortExpression.execute(env)).doubleValue();
        }));

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
    
    public List<BotCoordinate3D> generateOuterPoints(BotPatternGenerationParams params) {
        return generateFullCube(params.getCenterX(), params.getCenterY(), params.getCenterZ(),
                                   params.outerRadius);
    }

    private List<BotCoordinate3D> generateFullCube(int cx, int cy, int cz, int radius) {
        List<BotCoordinate3D> result = new ArrayList<>();
        for (int y = cy - radius; y <= cy + radius; y++) {
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    result.add(new BotCoordinate3D(x, y, z));
                }
            }
        }
        return result;
    }
}
