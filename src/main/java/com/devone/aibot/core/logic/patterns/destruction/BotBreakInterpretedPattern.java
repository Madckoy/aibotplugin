// Стартуем с минимального каркаса для загрузки и исполнения паттернов на основе YAML

package com.devone.aibot.core.logic.patterns.destruction;

import org.bukkit.Location;

import java.util.*;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

/**
 * Базовый класс интерпретируемого паттерна разрушения.
 * Загружается из YAML и используется для определения порядка обхода.
 */
public class BotBreakInterpretedPattern {

    public static class PatternContext {
        public final int cx, cy, cz;
        public final int radius;
        public PatternContext(Location center, int radius) {
            this.cx = center.getBlockX();
            this.cy = center.getBlockY();
            this.cz = center.getBlockZ();
            this.radius = radius;
        }
    }

    private List<String> filterConditions = new ArrayList<>();
    private List<String> sortConditions = new ArrayList<>();

    public void setFilterConditions(List<String> filters) {
        this.filterConditions = filters;
    }

    public void setSortConditions(List<String> sorts) {
        this.sortConditions = sorts;
    }

    /**
     * Генерация точек в соответствии с условиями фильтрации и сортировки
     */
    public List<Location> generate(PatternContext context) {
        List<Location> result = new ArrayList<>();

        for (int x = context.cx - context.radius; x <= context.cx + context.radius; x++) {
            for (int y = context.cy - context.radius; y <= context.cy + context.radius; y++) {
                for (int z = context.cz - context.radius; z <= context.cz + context.radius; z++) {
                    if (passesFilters(x, y, z, context)) {
                        result.add(new Location(null, x, y, z)); // TODO: world будет подставляться отдельно
                    }
                }
            }
        }

        result.sort(Comparator.comparingDouble(loc -> combinedSortScore(loc, context)));

        return result;
    }

    private boolean passesFilters(int x, int y, int z, PatternContext ctx) {
        for (String filter : filterConditions) {
            if (!evaluateBooleanExpression(filter, x, y, z, ctx)) {
                return false;
            }
        }
        return true;
    }

    private double combinedSortScore(Location loc, PatternContext ctx) {
        double score = 0;
        for (String expr : sortConditions) {
            score += evaluateDoubleExpression(expr, loc, ctx);
        }
        return score;
    }

    private boolean evaluateBooleanExpression(String expr, int x, int y, int z, PatternContext ctx) {
        try {
            Expression e = new ExpressionBuilder(expr)
                    .variables("x", "y", "z", "cx", "cy", "cz", "r")
                    .build()
                    .setVariable("x", x)
                    .setVariable("y", y)
                    .setVariable("z", z)
                    .setVariable("cx", ctx.cx)
                    .setVariable("cy", ctx.cy)
                    .setVariable("cz", ctx.cz)
                    .setVariable("r", ctx.radius);

            return e.evaluate() > 0; // любое положительное значение считаем true
        } catch (Exception ex) {
            return false;
        }
    }

    private double evaluateDoubleExpression(String expr, Location loc, PatternContext ctx) {
        try {
            Expression e = new ExpressionBuilder(expr)
                    .variables("x", "y", "z", "cx", "cy", "cz", "r")
                    .build()
                    .setVariable("x", loc.getBlockX())
                    .setVariable("y", loc.getBlockY())
                    .setVariable("z", loc.getBlockZ())
                    .setVariable("cx", ctx.cx)
                    .setVariable("cy", ctx.cy)
                    .setVariable("cz", ctx.cz)
                    .setVariable("r", ctx.radius);

            return e.evaluate();
        } catch (Exception ex) {
            return 9999;
        }
    }

        // Загрузка из YAML
    @SuppressWarnings("unchecked")
    public static BotBreakInterpretedPattern fromYaml(Map<String, Object> yaml) {
        BotBreakInterpretedPattern pattern = new BotBreakInterpretedPattern();

        Object filter = yaml.get("filter");
        Object sort = yaml.get("sort");

        if (filter instanceof List) {
            pattern.setFilterConditions((List<String>) filter);
        } else if (filter instanceof String) {
            pattern.setFilterConditions(Collections.singletonList((String) filter));
        }

        if (sort instanceof List) {
            pattern.setSortConditions((List<String>) sort);
        } else if (sort instanceof String) {
            pattern.setSortConditions(Collections.singletonList((String) sort));
        }

        return pattern;
    }
}
