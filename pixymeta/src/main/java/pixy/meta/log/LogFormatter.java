package pixy.meta.log;

import java.util.Arrays;

// TLAD: quick solution to get rid of Log4J
public class LogFormatter {
    static final String DELIMITER = "{}";

    public static String format(String format, Object... arguments) {
        StringBuilder sb = new StringBuilder(format.length() + 50);

        int textIndex = 0;
        int delimiterIndex;
        for (Object arg : arguments) {
            delimiterIndex = format.indexOf(DELIMITER, textIndex);

            if (delimiterIndex == -1) {
                if (textIndex == 0) {
                    return format;
                } else {
                    sb.append(format, textIndex, format.length());
                    return sb.toString();
                }
            } else {
                sb.append(format, textIndex, delimiterIndex);
                textIndex = delimiterIndex + 2;

                try {
                    if (arg == null) {
                        sb.append("null");
                    } else if (arg.getClass().isArray()) {
                        if (arg instanceof boolean[]) {
                            sb.append(Arrays.toString((boolean[]) arg));
                        } else if (arg instanceof byte[]) {
                            sb.append(Arrays.toString((byte[]) arg));
                        } else if (arg instanceof char[]) {
                            sb.append(Arrays.toString((char[]) arg));
                        } else if (arg instanceof short[]) {
                            sb.append(Arrays.toString((short[]) arg));
                        } else if (arg instanceof int[]) {
                            sb.append(Arrays.toString((int[]) arg));
                        } else if (arg instanceof long[]) {
                            sb.append(Arrays.toString((long[]) arg));
                        } else if (arg instanceof float[]) {
                            sb.append(Arrays.toString((float[]) arg));
                        } else if (arg instanceof double[]) {
                            sb.append(Arrays.toString((double[]) arg));
                        } else {
                            sb.append(Arrays.toString((Object[]) arg));
                        }
                    } else {
                        sb.append(arg.toString());
                    }
                } catch (Throwable t) {
                    sb.append("[`toString()` failed]");
                }
            }
        }
        sb.append(format, textIndex, format.length());
        return sb.toString();
    }
}
