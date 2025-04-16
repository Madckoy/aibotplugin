package com.devone.bot.core.logic.task.attack.survival.params;

import com.devone.bot.core.logic.task.params.BotTaskParams;
import com.devone.bot.utils.BotConstants;
import com.devone.bot.utils.blocks.BotBlockData;

public class BotSurvivalAttackTaskParams extends BotTaskParams {
    private BotBlockData target;
    private double damage = BotConstants.DEFAULT_HAND_DAMAGE;

    // Константы для иконки и цели
    private static final String DEFAULT_ICON = "જ⁀➴";
    private static final String DEFAULT_OBJECTIVE = "Survival strike (Teleport & Strike)";
    
    public BotSurvivalAttackTaskParams() {
        super();
        setIcon(DEFAULT_ICON);
        setObjective(DEFAULT_OBJECTIVE);
        this.target = null;

        // Загрузка параметров из конфигурации
        loadDefaults();
    }

    public BotSurvivalAttackTaskParams(BotBlockData target, double damage) {
        super();
        this.target = target;
        this.damage = damage;
        setIcon(DEFAULT_ICON);
        setObjective(DEFAULT_OBJECTIVE);

        // Загрузка параметров из конфигурации
        loadDefaults();
    }

    // Метод для загрузки значений из файла
    private void loadDefaults() {
        // Загрузка параметров из родительского класса
        BotSurvivalAttackTaskParams loaded = loadOrCreate(BotSurvivalAttackTaskParams.class);

        this.damage = loaded.damage;  // Если есть параметры в файле — они перезапишут дефолтные
        this.target = loaded.target;
        setIcon(loaded.getIcon());  // Если есть загруженная иконка — она перезапишет дефолтную
        setObjective(loaded.getObjective());
    }

    public BotBlockData getTarget() {
        return target;
    }

    public void setTarget(BotBlockData target) {
        this.target = target;
    }

    public double getDamage() {
        return damage;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    @Override
    public String toString() {
        return "BotSurvivalAttackTaskParams{" +
                "damage=" + damage +
                ", target=" + target +
                '}';
    }
}
