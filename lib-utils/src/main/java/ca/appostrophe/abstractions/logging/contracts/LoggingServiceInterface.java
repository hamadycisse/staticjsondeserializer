package ca.appostrophe.abstractions.logging.contracts;


import ca.appostrophe.abstractions.logging.LogEvent;
import ca.appostrophe.abstractions.logging.LogEventType;

/**
 * Created by Hamady Cissé on 2015-12-10.
 */
public interface LoggingServiceInterface {

    String LOGGING_CONFIG_NAME = "loggingconfiguration";

    String MINIMUM_LOGGING_LEVEL_CONFIG = "level";
    int DEFAULT_MINIMUM_LOGGING_LEVEL = LogEventType.DEBUG.getIntValue();

    void log(LogEvent event);
}
