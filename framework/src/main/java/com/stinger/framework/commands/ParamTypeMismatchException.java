package com.stinger.framework.commands;

public class ParamTypeMismatchException extends CommandException {

    public ParamTypeMismatchException(String key, Class<?> actual, Class<?> wanted) {
        super(String.format("%s: actual=%s, wanted=%s",
                key, actual.getName(), wanted.getName()));
    }
}
