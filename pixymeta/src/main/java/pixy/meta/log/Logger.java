package pixy.meta.log;

// TLAD: quick solution to get rid of Log4J
public interface Logger {
    void debug(String format, Object... arguments);

    void info(String msg);

    void info(String format, Object... arguments);

    void warn(String msg);

    void warn(String format, Object... arguments);

    void error(String msg);

    void error(String format, Object... arguments);

    void error(String msg, Throwable t);
}
