package pixy.meta.log;

// TLAD: quick solution to get rid of Log4J
public interface Logger {
    public void debug(String format, Object... arguments);

    public void info(String msg);

    public void info(String format, Object... arguments);

    public void warn(String msg);

    public void warn(String format, Object... arguments);

    public void error(String msg);

    public void error(String format, Object... arguments);

    public void error(String msg, Throwable t);
}
