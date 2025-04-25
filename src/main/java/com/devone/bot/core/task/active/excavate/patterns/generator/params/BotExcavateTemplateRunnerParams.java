package com.devone.bot.core.task.active.excavate.patterns.generator.params;

import com.devone.bot.core.utils.blocks.BotPosition;

public class BotExcavateTemplateRunnerParams {

    public BotPosition observer    = new BotPosition();
    public BotPosition offsetOuter = new BotPosition(); 
    public BotPosition offsetInner = new BotPosition();

    public double outerRadius;
    public double innerRadius;

    public boolean inverted;

    public BotExcavateTemplateRunnerParams() {
    

        this.outerRadius = 1;
        this.innerRadius = 1;

        this.inverted    = false;
    }

    public BotExcavateTemplateRunnerParams(BotPosition observer, 
                                           BotPosition offsetOuter, 
                                           double outerRadius, 
                                           BotPosition offsetInner, 
                                           double innerRadius, 
                                           boolean inverted) {
        this.observer  = new BotPosition(observer);
        this.offsetOuter = new BotPosition(offsetOuter);
        this.offsetInner = new BotPosition(offsetInner);
        
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;

        this.inverted    = inverted;

    }

    @Override
    public String toString() {
        String res = "Observer: "+ observer + 
                    ", offsetOuter: " + offsetOuter + 
                    ", outerRadius: " + outerRadius + 
                    ", offsetInner: " + offsetInner + 
                    ", innerRadius: " + innerRadius + 
                    ", inverted: " + inverted;




    return res;
}
}
