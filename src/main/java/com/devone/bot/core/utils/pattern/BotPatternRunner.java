package com.devone.bot.core.utils.pattern;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.devone.bot.AIBotPlugin;
import com.devone.bot.core.utils.BotConstants;
import com.devone.bot.core.utils.blocks.BotOffset;
import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.logger.BotLogger;
import com.devone.bot.core.utils.pattern.BotPatternParser.BotPatternParserResult;
import com.devone.bot.core.utils.pattern.params.BotPatternRunnerParams;

public class BotPatternRunner {

    BotPosition observer = null;
    BotOffset offset = null;
    String ptrnName = null;

    private BotPatternParserResult parsedResult = null;
    private BotPattern pattern = null; 
    private boolean loaded = false;

    private List<BotPosition> allPoints = new ArrayList<>();
    private List<BotPosition> allVoidPoints = new ArrayList<>();
    private List<BotPosition> allSolidPoints = new ArrayList<>();

    private Queue<BotPosition> voidPointsQueue = new LinkedList<>();
    private Queue<BotPosition> solidPointsQueue = new LinkedList<>();

    private boolean isLogged = AIBotPlugin.getInstance().isLogged();

    public boolean isLogged() {
        return isLogged;
    }

    public void setLogged(boolean isLogged) {
        this.isLogged = isLogged;
    }

    private BotPatternRunnerParams params;
    
    public BotPatternRunnerParams getParams() {
        return params;
    }

    public BotPatternRunner() {
        params = new BotPatternRunnerParams();
    }

    public BotPatternRunner setParams(BotPatternRunnerParams params) {
        this.params.setObserver(params.getObserver());
        this.params.setOffset(params.getOffset());
        this.params.setFilename(params.getFilename());
        return this;
    }

    public BotPatternRunner load(BotPosition obs) throws IOException {

        pattern = BotPatternLoader.load(new File(BotConstants.PLUGIN_PATH_PATTERNS_BREAK+params.getFilename()));
        loaded = true;
        parsedResult = BotPatternParser.parse(pattern, obs);

        allPoints = new ArrayList<BotPosition>(parsedResult.allPoints);
        offset =  parsedResult.offset;

        allVoidPoints = new ArrayList<BotPosition>(parsedResult.voidPoints);
        allSolidPoints = new ArrayList<BotPosition>(parsedResult.solidPoints);

        voidPointsQueue.addAll(allVoidPoints);
        solidPointsQueue.addAll(allSolidPoints);

        //BotLogger.debug("📐", isLogged(), " 📝 Pattern Summary: " + params.getFilename());
        //BotLogger.debug("📐", isLogged(), "          All Points: " + allPoints);
        //BotLogger.debug("📐", isLogged(), "     All Void Points: " + allVoidPoints);
        //BotLogger.debug("📐", isLogged(), "    All Solid Points: " + allSolidPoints);
        //BotLogger.debug("📐", isLogged(), " -----------------------------------------");
        //BotLogger.debug("📐", isLogged(), " Void Points Queue: " + voidPointsQueue);
        //BotLogger.debug("📐", isLogged(), " Solid Points Queue: " + solidPointsQueue);

        return this;
    }

    public boolean checkIfLoaded(BotPosition obs) {
        boolean res = true;

        if (!loaded) {
            res = false;
            try {
                load(obs);
                res = true;
            } catch(Exception ex) {
                 BotLogger.debug("📐", isLogged(), " 🚨 Паттерн не загружен! JSON: " + params.getFilename());   
                 res = false;
            }    
        }
        return res;
    }

    public BotPosition getNextVoid(BotPosition obs) {
        if (checkIfLoaded(obs)) {
            return voidPointsQueue.poll();
        } else {
            BotLogger.debug("📐", isLogged(), " 🚨 getNextVoid: checkIfLoaded вернул FALSE: " + params.getFilename());   
            return null;
        } 
    }

    public BotPosition getNextSolid(BotPosition obs) {
        if (checkIfLoaded(obs)) {
            return solidPointsQueue.poll();
        } else {
            BotLogger.debug("📐", isLogged(), " 🚨 getNextSolid: checkIfLoaded вернул FALSE: " + params.getFilename());
            return null;
        } 
    }

    public List<BotPosition> getAll() {
        return allPoints;
    }

    public List<BotPosition> getAllSolid() {
        return allSolidPoints;
    }

    public List<BotPosition> getAllVoid() {
        return allVoidPoints;
    }

    public BotOffset getOffset() {
        return offset;
    }

    public String getName() {
        return "BotPatternRunner(" + params.getFilename() + ")";
    }
    
    public boolean isLoaded() {
        return loaded;
    }

}
