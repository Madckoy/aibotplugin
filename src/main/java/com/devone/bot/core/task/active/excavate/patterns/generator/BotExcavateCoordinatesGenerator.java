package com.devone.bot.core.task.active.excavate.patterns.generator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import com.devone.bot.core.task.active.excavate.patterns.generator.params.BotExcavateTemplateRunnerParams;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.googlecode.aviator.AviatorEvaluator;
import com.googlecode.aviator.Expression;

public class BotExcavateCoordinatesGenerator {

    private final List<Expression> filterExpressions;
    private final Expression sortExpression;
    private boolean inverted;
    private final BotExcavatePatternAttributes attributes;

    public BotExcavatePatternAttributes getAttributes() {
        return attributes;
    }

    public BotExcavateCoordinatesGenerator(List<String> filterExpressions, String sortExpression, BotExcavatePatternAttributes attr) {
        attributes = attr;
        
        this.filterExpressions = filterExpressions.stream()
                .map(expr -> AviatorEvaluator.compile(expr, true))
                .collect(Collectors.toList());

        this.sortExpression = AviatorEvaluator.compile(sortExpression, true);
    }

    public static BotExcavateCoordinatesGenerator loadYmlFromStream(InputStream input) {
        
        Yaml yaml = new Yaml();
        Map<String, Object> data = yaml.load(input);
        @SuppressWarnings("unchecked")
        List<String> filters = (List<String>) data.getOrDefault("filters", new ArrayList<>());
        @SuppressWarnings("unchecked")
        List<String> sortList = (List<String>) data.getOrDefault("sort", Collections.singletonList("y"));
        String sort = sortList.get(0);


        BotExcavatePatternAttributes attributes = readPatternAttributes(data);

        return new BotExcavateCoordinatesGenerator(filters, sort, attributes);
    }

    @SuppressWarnings("unchecked")
    private static BotExcavatePatternAttributes readPatternAttributes(Map<String, Object> data) {

        double innerRadius = ((Number) data.getOrDefault("innerRadius", 0)).doubleValue();
        double outerRadius = ((Number) data.getOrDefault("outerRadius", 0)).doubleValue();
        boolean inverted = (boolean) data.getOrDefault("inverted", false);

        Map<String, Object> outerOffsetMap = (Map<String, Object>) data.getOrDefault("offsetOuter", Collections.emptyMap());
        Map<String, Object> innerOffsetMap = (Map<String, Object>) data.getOrDefault("offsetInner", Collections.emptyMap());

        
        double x1 = ((Number) outerOffsetMap.getOrDefault("x", 0)).doubleValue();
        double y1 = ((Number) outerOffsetMap.getOrDefault("y", 0)).doubleValue();
        double z1 = ((Number) outerOffsetMap.getOrDefault("z", 0)).doubleValue();

        BotPosition offsetOuter = new BotPosition(x1, y1, z1);

        double x2 = ((Number) innerOffsetMap.getOrDefault("x", 0)).doubleValue();
        double y2 = ((Number) innerOffsetMap.getOrDefault("y", 0)).doubleValue();
        double z2 = ((Number) innerOffsetMap.getOrDefault("z", 0)).doubleValue();

        BotPosition offsetInner = new BotPosition(x2, y2, z2);

        return new BotExcavatePatternAttributes(offsetOuter, outerRadius, offsetInner, innerRadius, inverted);

        //BotExcavateInterpretedYamlPattern(x1, y1, z1, outerRadius, x2, y2, z2, innerRadius);
    }

    public List<BotPosition> generateInnerPoints(BotExcavateTemplateRunnerParams params) {
        double ox = params.observerX;
        double oy = params.observerY;
        double oz = params.observerZ;
        
        double innerRadius = params.innerRadius;
        
        double offsetX = params.offsetInnerX;
        double offsetY = params.offsetInnerY;
        double offsetZ = params.offsetInnerZ;

        double[] center = computeFigureCenter(ox, oy, oz, offsetX, offsetY, offsetZ);

        return generateInnerPoints(center[0], center[1], center[2], innerRadius);
    }

    private double[] computeFigureCenter(double ox, double oy, double oz, double offsetX, double offsetY, double offsetZ) {

        double cx = ox + offsetX;
        double cy = oy + offsetY;
        double cz = oz + offsetZ;

        return new double[]{cx, cy, cz};
    }

    
    private List<BotPosition> generateInnerPoints(double cx, double cy, double cz, double inner_radius) {
        List<BotPosition> result = new ArrayList<>();
        Map<String, Object> env = new HashMap<>();

        for (double y = cy - inner_radius; y <= cy + inner_radius; y++) {
            for (double x = cx - inner_radius; x <= cx + inner_radius; x++) {
                for (double z = cz - inner_radius; z <= cz + inner_radius; z++) {
                    env.put("x", x);
                    env.put("y", y);
                    env.put("z", z);
                    env.put("cx", cx);
                    env.put("cy", cy);
                    env.put("cz", cz);
                    env.put("r", inner_radius);

                    if (applyFilters(env)) {
                        result.add(new BotPosition(x, y, z));
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

    public List<BotPosition> generateOuterPoints(BotExcavateTemplateRunnerParams params) {
        double ox = params.observerX;
        double oy = params.observerY;
        double oz = params.observerZ;
        

        double outerRadius = params.outerRadius;
        
        double offsetX = params.offsetOuterX;
        double offsetY = params.offsetOuterY;
        double offsetZ = params.offsetOuterZ;

        double[] center = computeFigureCenter(ox, oy, oz, offsetX, offsetY, offsetZ);

        return generateOuterPoints(center[0], center[1], center[2], outerRadius);
    }

    private List<BotPosition> generateOuterPoints(double cx, double cy, double cz, double radius) {
        List<BotPosition> result = new ArrayList<>();
        for (double y = cy - radius; y <= cy + radius; y++) {
            for (double x = cx - radius; x <= cx + radius; x++) {
                for (double z = cz - radius; z <= cz + radius; z++) {
                    result.add(new BotPosition(x, y, z));
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
