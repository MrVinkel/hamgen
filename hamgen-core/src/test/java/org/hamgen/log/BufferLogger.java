package org.hamgen.log;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import static org.hamgen.log.Logger.LogLevel.*;

public class BufferLogger extends StandardOutLogger {

    private static List<Entry<LogLevel,String>> log = new ArrayList<>();

    public BufferLogger(LogLevel level) {
        super(level);
        setLogger(this);
    }

    @Override
    public void debug(String message) {
        logMessage(DEBUG, message, null);
    }

    @Override
    public void debug(String message, Throwable t) {
        logMessage(DEBUG, message, t);
    }

    @Override
    public void info(String message) {
        logMessage(INFO, message, null);
    }

    @Override
    public void info(String message, Throwable t) {
        logMessage(INFO, message, t);
    }

    @Override
    public void warn(String message) {
        logMessage(WARN, message, null);
    }

    @Override
    public void warn(String message, Throwable t) {
        logMessage(WARN, message, t);
    }

    @Override
    public void error(String message) {
        logMessage(ERROR, message, null);
    }

    @Override
    public void error(String message, Throwable t) {
        logMessage(ERROR, message, t);
    }

    protected void logMessage(LogLevel level, String message, Throwable t) {
        BufferLogger.log.add(new AbstractMap.SimpleEntry<>(level, message));
        super.logMessage(level, message, t);
    }

    public static List<Entry<LogLevel,String>> getLog() {
        return BufferLogger.log;
    }

    public static void clear() {
        BufferLogger.log.clear();
    }
}
