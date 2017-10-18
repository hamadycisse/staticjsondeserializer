package ca.appostrophe.abstractions.logging;

/**
 * Created by Hamady Cissé on 2015-12-10.
 */
public class LogEvent {

    private final static String TAG = "APP";

    private LogEventType mLogEventType;
    private String mMessage;
    private String mTag;
    private Exception mException;

    public LogEvent(LogEventType logEventType, String message) {
        this(logEventType, message, null, null);
    }

    public LogEvent(LogEventType logEventType, String message, String tag) {
        this(logEventType, message, tag, null);
    }

    public LogEvent(LogEventType logEventType, String message, Exception exception) {
        this(logEventType, message, TAG, exception);
    }

    public LogEvent(LogEventType logEventType, String message, String tag, Exception exception) {
        setLogEventType(logEventType);
        setMessage(message);
        setTag(tag);
        setException(exception);
    }

    public LogEventType getLogEventType() {
        return mLogEventType;
    }

    public void setLogEventType(LogEventType logEventType) {
        this.mLogEventType = logEventType;
    }

    public String getMessage() {
        return mMessage;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    public String getTag() {
        return mTag;
    }

    private void setTag(String tag) {
        mTag = tag;
    }

    public Exception getException() {
        return mException;
    }

    private void setException(Exception exception) {
        mException = exception;
    }

    public static LogEvent createUnsupportedTypeLogEvent(final String tag, final Object unsupportedObject) {
        return new LogEvent(LogEventType.INFO, String.format("UNSUPPORTED TYPE: %s",
                unsupportedObject.getClass().getSimpleName()), tag);

    }
}
