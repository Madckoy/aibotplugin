package com.devone.aibot.core.logic.patterns;

import com.devone.aibot.utils.Bot3DCoordinate;
import com.devone.aibot.utils.BotAxisDirection.AxisDirection;
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

    public List<Bot3DCoordinate> generateInnerPointsFromObserver(

            int ox, int oy, int oz,
            int radius, AxisDirection direction,  int inner_radius, Integer offset) {

        int[] center;
        if (direction != null && (offset == null || offset != 0)) {
            int actualOffset = (offset != null) ? offset : radius;
            center = computeFigureCenterFromObserver(ox, oy, oz, actualOffset, direction);
        } else {
            center = new int[]{ox, oy, oz};
        }
        return generateInnerPoints(center[0], center[1], center[2], radius, inner_radius);
    }

    private int[] computeFigureCenterFromObserver(int ox, int oy, int oz, int offset, AxisDirection direction) {
        int dx = 0, dy = 0, dz = 0;

        switch (direction) {
            case UP: dy = 1; break;
            case DOWN: dy = -1; break;
            case NORTH: dz = -1; break;
            case SOUTH: dz = 1; break;
            case WEST: dx = -1; break;
            case EAST: dx = 1; break;
            default: throw new IllegalArgumentException("Unknown facing: " + direction);
        }

        int cx = ox + dx * offset;
        int cy = oy + dy * offset;
        int cz = oz + dz * offset;

        return new int[]{cx, cy, cz};
    }

    private List<Bot3DCoordinate> generateInnerPoints(int cx, int cy, int cz, int radius, int r) {
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

                    if (applyFilters(env)) {
                        result.add(new Bot3DCoordinate(x, y, z));
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

    public List<Bot3DCoordinate> generateOuterPointsFromObserver(
        int ox, int oy, int oz,
        int radius, AxisDirection direction, Integer offset
        ) {
            int[] center;
            if (direction != null && (offset == null || offset != 0)) {
                int actualOffset = (offset != null) ? offset : radius;
                center = computeFigureCenterFromObserver(ox, oy, oz, actualOffset, direction);
            } else {
                center = new int[]{ox, oy, oz};
            }
            return generateFullCube(center[0], center[1], center[2], radius);
        }


    private List<Bot3DCoordinate> generateFullCube(int cx, int cy, int cz, int radius) {
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