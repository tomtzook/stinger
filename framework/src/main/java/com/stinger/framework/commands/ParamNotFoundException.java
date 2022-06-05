package com.stinger.framework.commands;

public class ParamNotFoundException extends CommandException {

    public ParamNotFoundException(String key) {
        super(key);
    }
}
