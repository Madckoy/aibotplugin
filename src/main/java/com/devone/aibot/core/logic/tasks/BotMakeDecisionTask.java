package com.devone.aibot.core.logic.tasks;

import com.devone.aibot.core.logic.tasks.configs.BotHuntTaskConfig;
import com.devone.aibot.core.logic.tasks.configs.BotIdleTaskConfig;
import com.devone.aibot.core.logic.tasks.configs.BotMakeDecisionTaskConfig;
import com.devone.aibot.core.logic.tasks.destruction.BotBreakAnyDownwardTask;

import java.util.Set;

import com.devone.aibot.utils.BotStringUtils;
import com.devone.aibot.utils.BotMaterialUtils;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;

import com.devone.aibot.core.Bot;
import com.devone.aibot.core.BotInventory;
import com.devone.aibot.utils.BotConstants;
import com.devone.aibot.utils.BotLogger;

public class BotMakeDecisionTask extends BotTask {

    public BotMakeDecisionTask(Bot bot) {
        super(bot, "🎲");
        this.bot = bot;
        config = new BotMakeDecisionTaskConfig();
        logging = config.isLogging();
        setObjective("Roll a dice");
    }

    @Override
    public void executeTask() {
        
        setObjective("Rolling a dice" );
  
        double rand = Math.random();

        int maxToCollect = 128;

        // Проверяем, нужно ли очистить инвентарь
        if (!BotInventory.hasFreeInventorySpace(bot, BotMaterialUtils.dirtTypes) || BotInventory.hasEnoughBlocks(bot, BotMaterialUtils.dirtTypes, maxToCollect)) {
            bot.setAutoPickupEnabled(false);

            BotDropAllTask drop_task = new BotDropAllTask(bot, null);
            drop_task.setPaused(true);
            bot.addTaskToQueue(drop_task);
            
            Location drop_off_loc = bot.getRuntimeStatus().getTargetLocation();
            
            // Перемещение к точке сброса
            BotMoveTask moveTask = new BotMoveTask(bot);
            moveTask.configure(drop_off_loc);
            bot.addTaskToQueue(moveTask);

            BotLogger.info(isLogging(),"📦 " + bot.getId() + " Идёт к точке сброса: " + BotStringUtils.formatLocation(drop_off_loc));
            return;
        }

        // Определяем, день или ночь
        World world = bot.getNPCEntity().getWorld();
        long time = world.getTime();
        boolean isNight = (time >= 13000 && time <= 23000); // Примерно 13000 - закат, 23000 - рассвет

        double huntChance = isNight ? 0.9 : 0.2; // 90% ночью, 20% днем

        if (rand < 0.1) { // 10% шанс сказать что-то про окружающий мир
            BotLogger.info(isLogging(),"🤖 " + bot.getId() + " Комментирует обстановку.");
            bot.addTaskToQueue(new BotTalkTask(bot, null, BotTalkTask.TalkType.ENVIRONMENT_COMMENT));
            return;
        }


        if (rand < huntChance) {
            // ⚔️ Охота
            BotLogger.info(isLogging(),"⚔️ " + bot.getId() + " Собирается на охоту! (Вероятность: " + huntChance * 100 + "%)");
           
            BotHuntMobsTask hunt_task = new BotHuntMobsTask(bot);
            //BotTaskHuntMobs config  = hunt_task.getConfig();
            Set<EntityType> a_targets = ((BotHuntTaskConfig) hunt_task.getConfig()).getTargetAggressiveMobs();
            Set<EntityType> p_targets = ((BotHuntTaskConfig) hunt_task.getConfig()).getTargetPassiveMobs();
            Set<EntityType> targets = isNight ? a_targets : p_targets;
            
            hunt_task.configure(targets, 20, true);
            bot.addTaskToQueue(hunt_task);
            BotLogger.info(isLogging(), "⚔️ " + bot.getId() + " Начинает охоту на " + (isNight ? "агрессивных мобов" : "животных") + "!");

            return;
        }

        if (rand >= 0.8) {
            // 📌 Начать патрулирование (20% вероятность)
            BotLogger.info(isLogging(), "🌐 " + bot.getId() + " начинает патрулирование.");
            BotExploreTask patrolTask = new BotExploreTask(bot);
            bot.addTaskToQueue(patrolTask);
            return;
        }

        /* 
        if (rand < 0.8 && rand >= 0.5) {
            // ⛏ 30% шанс начать добычу земли
            BotTaskBreakBlock breakTask = new BotTaskBreakBlock(bot);
        
            if (breakTask.isEnabled) {
                breakTask.configure(dirtTypes, maxToCollect, BotConstants.DEFAULT_SCAN_RANGE, true);
                bot.addTaskToQueue(breakTask);
            }
        
            return;
        }
            */

        if (rand < 0.8 && rand >= 0.2) {  
            // ⛏ 30% шанс начать добычу всего подряд вниз
            BotBreakAnyDownwardTask breakTask = new BotBreakAnyDownwardTask(bot);
        
            if (breakTask.isEnabled) {

                /**
                     * Конфигурирует задачу разрушения.
                     * 
                     * Параметры (позиционные):
                     * 
                     * 0 - Set<Material> targetMaterials (nullable) — блоки, которые нужно разрушать.
                     * 1 - Integer maxBlocks (nullable) — максимальное количество блоков, которые нужно собрать.
                     * 2 - Integer breakRadius (nullable) — радиус разрушения.
                     * 3 - Boolean shouldPickup (nullable) — собирать ли предметы после разрушения.
                     * 4 - Boolean destroyAllIfNoTarget (nullable) — если нет подходящих блоков, разрушать всё подряд.
                     * 5 - IBotDestructionPattern или String (nullable) — шаблон разрушения:
                     *     - IBotDestructionPattern — готовый объект.
                     *     - String — путь к YAML-файлу шаблона (относительно каталога паттернов).
                     *
                     * Если параметры не заданы, используются значения по умолчанию.
                     */
                breakTask.configure(null, maxToCollect, BotConstants.DEFAULT_SCAN_RANGE, true, true, breakTask.patternName);

                bot.addTaskToQueue(breakTask);
            }    
        
            return;
        }

        if (rand < 0.2) {
            // 🍹 20% шанс остаться в IDLE
            
            BotIdleTask idle = new BotIdleTask(bot, null);
            bot.addTaskToQueue(idle);

            BotLogger.info(isLogging(),"🍹" + bot.getId() + " остаётся в IDLE.");
            return;
        }
    }
}
