package com.devone.bot.core.utils.pattern.params;

import com.devone.bot.core.utils.blocks.BotPosition;

public class BotPatternRunnerParams {

    BotPosition observer = null;
    BotPosition  offset  = null;
    String      filename = "default.json";

    public BotPosition getObserver() {
        return observer;
    }
    public void setObserver(BotPosition observer) {
        this.observer = observer;
    }
    public BotPosition getOffset() {
        return offset;
    }
    public void setOffset(BotPosition offset) {
        this.offset = offset;
    }
    public String getFilename() {
        return filename;
    }
    public void setFilename(String filename) {
        this.filename = filename;
    }

    public BotPatternRunnerParams() {
        super();
    }

    public BotPatternRunnerParams(BotPosition obs, BotPosition ofst,  String filename ) {
        this();
        if(obs!=null) {
            observer = new BotPosition(obs);
        }
        if(ofst!=null) {
            offset = new BotPosition(obs);
        }
        filename = new String(filename);
    }

}
