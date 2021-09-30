package pixy.meta.log;

// TLAD: quick solution to get rid of Log4J
public class LoggerFactory {
    public static boolean test = false;

    public static Logger getLogger(Class<?> clazz) {
        return test ? new SystemPrintLogger(clazz) : new AndroidLogger(clazz);
    }
}
