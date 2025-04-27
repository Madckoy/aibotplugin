package com.devone.bot.core.utils.pattern;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.devone.bot.core.utils.blocks.BotPosition;
import com.devone.bot.core.utils.pattern.BotPatternParser.BotPatternParserResult;
import com.devone.bot.core.utils.pattern.params.BotPatternRunnerParams;

public class BotPatternRunner {

    BotPosition observer = null;
    BotPosition offset = null;
    String ptrnName = null;

    private BotPatternParserResult parsedResult = null;
    private BotPattern pattern = null; 
    private boolean loaded = false;

    private List<BotPosition> allPoints = new ArrayList<>();
    private List<BotPosition> allVoidPoints = new ArrayList<>();
    private List<BotPosition> allSolidPoints = new ArrayList<>();

    private Queue<BotPosition> voidPointsQueue = new LinkedList<>();
    private Queue<BotPosition> solidPointsQueue = new LinkedList<>();


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

        pattern = BotPatternLoader.load(new File(params.getFilename()));
        loaded = true;
        parsedResult = BotPatternParser.parse(pattern, obs);

        allPoints = new ArrayList<BotPosition>(parsedResult.allPoints);
        offset =  parsedResult.offset;

        allVoidPoints = new ArrayList<BotPosition>(parsedResult.voidPoints);
        allSolidPoints = new ArrayList<BotPosition>(parsedResult.solidPoints);

        voidPointsQueue.addAll(allSolidPoints);
        solidPointsQueue.addAll(allSolidPoints);

        return this;
    }

    public boolean checkIfLoaded() {
        boolean res = false;
        if (this.pattern == null) {
            //BotLogger.debug("📐", true, " 🚨 Паттерн не загружен! JSON: " + params.getFilename());
        }

        if (!loaded) {

            BotPosition obs = new BotPosition(); 
            try {
                load(obs);
            } catch(Exception ex) {
                 //BotLogger.debug("📐", true, " 🚨 Паттерн не загружен! JSON: " + params.getFilename());   
                 loaded = false;
            }    
            res = true;
        }
        return res;
    }

    public BotPosition getNextVoid() {
        if (checkIfLoaded()) {
            return voidPointsQueue.poll();
        } else {
            return null;
        } 
    }

    public BotPosition getNextSolid() {
        if (checkIfLoaded()) {
            return solidPointsQueue.poll();
        } else {
            return null;
        } 
    }


    public boolean isNoVoid() {
        return loaded && voidPointsQueue.isEmpty();
    }

    public boolean isNoSolid() {
        return loaded && solidPointsQueue.isEmpty();
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

    public BotPosition getOffset() {
        return offset;
    }

    public String getName() {
        return "BotPatternRunner(" + params.getFilename() + ")";
    }
    
    public boolean isLoaded() {
        return loaded;
    }

}
