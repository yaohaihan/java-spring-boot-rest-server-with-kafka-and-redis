package oxus.lib.common.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import ch.qos.logback.core.LayoutBase;

import java.nio.charset.Charset;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Map;

import static java.lang.Math.min;
import static java.lang.String.format;
import static java.time.LocalDateTime.ofInstant;

public class OxusLayout extends LayoutBase<ILoggingEvent> {

    private boolean encode = true;

    public void setEncode(final boolean encode) {

        this.encode = encode;
    }

    @Override
    public String doLayout(final ILoggingEvent event) {

        final String timestamp = getTimestamp(event.getTimeStamp());
        final String threadName = event.getThreadName();
        final String traceId = getTraceId(event.getMDCPropertyMap());
        final String spanId = getSpanId(event.getMDCPropertyMap());
        final String level = getLevel(event.getLevel());
        final String loggerName = event.getLoggerName();
        final String message = getMessage(event);
        final boolean multiLine = isMultiLine(message);

        return timestamp
                + " " +
                "[" + threadName + "] " +
                "[" + traceId + "] " +
                "[" + spanId + "] " +
                level +
                " [" + getMultiLine(multiLine) + "] " +
                loggerName + " - " +
                getMessage(message, multiLine) +
                "\n";
    }

    private String getMessage(final String message,
                              final boolean multiLine) {

        return multiLine && encode ? encode(message) : message;
    }

    private String getMultiLine(final boolean multiLine) {

        return multiLine && encode ? "Y" : "N";
    }

    private static boolean isMultiLine(final String message) {

        return message.contains(System.getProperty("line.separator"));
    }

    private static String getMessage(final ILoggingEvent event) {

        final StringBuilder message = new StringBuilder(event.getFormattedMessage());

        final IThrowableProxy throwableProxy = event.getThrowableProxy();
        if (throwableProxy != null) {
            message.append("\n").append(throwableProxy.getMessage()).append("\n");

            for (final StackTraceElementProxy element : throwableProxy.getStackTraceElementProxyArray()) {
                message.append("\t").append(element).append("\n");
            }
        }
        return message.toString();
    }

    private static String getLevel(final Level level) {

        return format("%-5s", level);
    }

    private static String getTraceId(final Map<String, String> mdcProperties) {

        final String traceId = mdcProperties.getOrDefault("traceId", "");
        return format("%-32s", traceId == null ? "" : traceId);
    }

    private static String getSpanId(final Map<String, String> mdcProperties) {

        final String spanId = mdcProperties.getOrDefault("spanId", "");
        return format("%-16s", spanId == null ? "" : spanId);
    }

    private static String getTimestamp(final long timeStamp) {

        return ofInstant(Instant.ofEpochMilli(timeStamp), ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
    }

    private static String encode(final String message) {

        return Base64.getEncoder().encodeToString(trim(message).getBytes(Charset.forName("UTF-8")));
    }

    private static String trim(final String message) {

        final int MAX_MESSAGE_LENGTH = 8000;
        return message.substring(0, min(message.length(), MAX_MESSAGE_LENGTH));
    }
}
