package com.devone.aibot.core.math;

import com.googlecode.aviator.runtime.function.AbstractFunction;
import com.googlecode.aviator.runtime.function.FunctionUtils;
import com.googlecode.aviator.runtime.type.AviatorDouble;
import com.googlecode.aviator.runtime.type.AviatorObject;

import java.util.Map;

public class BotMathMaxFunction extends AbstractFunction {
    @Override
    public String getName() {
        return "math.max";
    }

    @Override
    public AviatorObject call(Map<String, Object> env, AviatorObject arg1, AviatorObject arg2) {
        Number a = FunctionUtils.getNumberValue(arg1, env);
        Number b = FunctionUtils.getNumberValue(arg2, env);
        return new AviatorDouble(Math.max(a.doubleValue(), b.doubleValue()));
    }
}
