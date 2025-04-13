package com.devone.bot.core.logic.task;

import java.util.Stack;

public class BotTaskStack {
    private final Stack<BotTask> stack = new Stack<>();

    public void push(BotTask activity) {
        stack.push(activity);
    }

    public void pop() {
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    public BotTask getCurrentActivity() {
        return stack.isEmpty() ? null : stack.peek();
    }

    public boolean isEmpty() {
        return stack.isEmpty();
    }
}

