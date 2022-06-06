package com.stinger.server;

import com.stinger.framework.commands.GenericCommandType;
import com.stinger.framework.data.KnownTypes;
import com.stinger.framework.logging.Logger;
import com.stinger.framework.storage.GenericProductType;
import com.stinger.server.commands.CommandQueue;
import com.stinger.server.storage.ToolStorage;

public class Environment {

    private final KnownTypes<GenericCommandType, Integer> mKnownCommandTypes;
    private final KnownTypes<GenericProductType, Integer> mKnownProductTypes;
    private final CommandQueue mCommandQueue;
    private final ToolStorage mStorage;
    private final Logger mLogger;

    public Environment(KnownTypes<GenericCommandType, Integer> knownCommandTypes,
                       KnownTypes<GenericProductType, Integer> knownProductTypes,
                       CommandQueue commandQueue,
                       ToolStorage storage,
                       Logger logger) {
        mKnownCommandTypes = knownCommandTypes;
        mKnownProductTypes = knownProductTypes;
        mCommandQueue = commandQueue;
        mStorage = storage;
        mLogger = logger;
    }

    public KnownTypes<GenericCommandType, Integer> getCommandTypes() {
        return mKnownCommandTypes;
    }

    public KnownTypes<GenericProductType, Integer> getProductTypes() {
        return mKnownProductTypes;
    }

    public CommandQueue getCommandQueue() {
        return mCommandQueue;
    }

    public ToolStorage getStorage() {
        return mStorage;
    }

    public Logger getLogger() {
        return mLogger;
    }
}
