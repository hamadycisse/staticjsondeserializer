package ca.appostrophe.abstractions.logging;

/**
 * Created by Hamady Cissé on 2015-12-10.
 */
public enum LogEventType {
    VERBOSE(0),
    DEBUG(1),
    INFO(2),
    WARNING(3),
    ERROR(4),
    FATAL(5),

    ;

    private final int mIntValue;

    LogEventType(int intValue) {
        mIntValue = intValue;
    }

    public int getIntValue() {
        return mIntValue;
    }
}
