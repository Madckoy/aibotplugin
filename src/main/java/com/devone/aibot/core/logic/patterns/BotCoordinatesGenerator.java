package com.devone.aibot.core.logic.patterns;

import com.devone.aibot.utils.Bot3DCoordinate;
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
        // Компилируем фильтры заранее
        this.filterExpressions = filterExpressions.stream()
                .map(expr -> AviatorEvaluator.compile(expr, true))
                .collect(Collectors.toList());

        // Компилируем одно выражение для сортировки
        this.sortExpression = AviatorEvaluator.compile(sortExpression, true);
    }

    public static BotCoordinatesGenerator loadYamlFromStream(InputStream input) {
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(input);
        List<String> filters = (List<String>) data.getOrDefault("filters", new ArrayList<>());

        // Берём только первое выражение для сортировки для простоты (можно расширить)
        List<String> sortList = (List<String>) data.getOrDefault("sort", Collections.singletonList("y"));
        String sort = sortList.get(0);

        return new BotCoordinatesGenerator(filters, sort);
    }

    public List<Bot3DCoordinate> generateFullCube(int cx, int cy, int cz, int radius) {
        List<Bot3DCoordinate> result = new ArrayList<>();
        for (int y = cy - radius; y <= cy + radius; y++) {
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {
                    result.add(new Bot3DCoordinate(x, y, z));
                }
            }
        }
        return result;
    }

    public List<Bot3DCoordinate> generate(int cx, int cy, int cz, int radius, int r, int y_min) {
        List<Bot3DCoordinate> result = new ArrayList<>();

        Map<String, Object> env = new HashMap<>();

        for (int y = cy - radius; y <= cy + radius; y++) {
            for (int x = cx - radius; x <= cx + radius; x++) {
                for (int z = cz - radius; z <= cz + radius; z++) {

                    env.put("x", x);
                    env.put("y", y);
                    env.put("z", z);
                    env.put("cx", cx);
                    env.put("cy", cy);
                    env.put("cz", cz);
                    env.put("r", r);
                    env.put("y_min", y_min);

                    if (applyFilters(env)) {
                        result.add(new Bot3DCoordinate(x, y, z));
                    }
                }
            }
        }

        // Сортировка только по одному выражению
        result.sort(Comparator.comparingDouble(loc -> {
            env.put("x", loc.x);
            env.put("y", loc.y);
            env.put("z", loc.z);
            return ((Number)sortExpression.execute(env)).doubleValue();
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
}
