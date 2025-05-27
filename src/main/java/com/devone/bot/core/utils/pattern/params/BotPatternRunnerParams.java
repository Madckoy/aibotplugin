package com.devone.bot.core.utils.pattern.params;

import com.devone.bot.core.utils.blocks.BotOffset;
import com.devone.bot.core.utils.blocks.BotPosition;

public class BotPatternRunnerParams {

    BotPosition observer = null;
    BotOffset   offset  = null;
    String      filename = "default.json";

    public BotPosition getObserver() {
        return observer;
    }
    public void setObserver(BotPosition observer) {
        this.observer = observer;
    }
    public BotOffset getOffset() {
        return offset;
    }
    public void setOffset(BotOffset offset) {
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

    public BotPatternRunnerParams(BotPosition obs, BotOffset ofst,  String filename ) {
        this();
        if(obs!=null) {
            observer = new BotPosition(obs);
        }
        if(ofst!=null) {
            offset = new BotOffset(ofst);
        }
        filename = new String(filename);
    }

}
