package pixy.meta.log;

// TLAD: quick solution to get rid of Log4J
public class SystemPrintLogger implements Logger {
    protected String tag;

    SystemPrintLogger(Class<?> clazz) {
        this.tag = clazz.getName();
    }

    void print(String level, String format, Object... arguments) {
        System.out.println("[" + level + "] [" + tag + "] " + LogFormatter.format(format, arguments));
    }

    @Override
    public void debug(String format, Object... arguments) {
        print("DEBUG", format, arguments);
    }

    @Override
    public void info(String msg) {
        print("INFO ", msg);
    }

    @Override
    public void info(String format, Object... arguments) {
        print("INFO ", format, arguments);
    }

    @Override
    public void warn(String msg) {
        print("WARN ", msg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        print("WARN ", format, arguments);
    }

    @Override
    public void error(String msg) {
        print("ERROR", msg);
    }

    @Override
    public void error(String format, Object... arguments) {
        print("ERROR", format, arguments);
    }

    @Override
    public void error(String msg, Throwable t) {
        print("ERROR", msg + ", error=" + t);
    }
}
