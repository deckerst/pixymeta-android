package pixy.meta.log;

import android.util.Log;

// TLAD: quick solution to get rid of Log4J
public class AndroidLogger implements Logger {
    protected String tag;

    AndroidLogger(Class<?> clazz) {
        this.tag = clazz.getName();
    }

    @Override
    public void debug(String format, Object... arguments) {
        Log.d(tag, LogFormatter.format(format, arguments));
    }

    @Override
    public void info(String msg) {
        Log.i(tag, msg);
    }

    @Override
    public void info(String format, Object... arguments) {
        Log.i(tag, LogFormatter.format(format, arguments));
    }

    @Override
    public void warn(String msg) {
        Log.w(tag, msg);
    }

    @Override
    public void warn(String format, Object... arguments) {
        Log.w(tag, LogFormatter.format(format, arguments));
    }

    @Override
    public void error(String msg) {
        Log.e(tag, msg);
    }

    @Override
    public void error(String format, Object... arguments) {
        Log.e(tag, LogFormatter.format(format, arguments));
    }

    @Override
    public void error(String msg, Throwable t) {
        Log.e(tag, msg, t);
    }
}
