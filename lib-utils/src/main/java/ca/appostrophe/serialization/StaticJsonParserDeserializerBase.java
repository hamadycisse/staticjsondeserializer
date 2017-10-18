package ca.appostrophe.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;

import java.io.IOException;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.appostrophe.abstractions.logging.LogEvent;
import ca.appostrophe.abstractions.logging.LogEventType;
import ca.appostrophe.abstractions.logging.contracts.LoggingServiceInterface;
import ca.appostrophe.abstractions.servicelocator.ServiceLocator;

/**
 * Created by Hamady Cissé on 2015-12-10.
 */
public abstract class StaticJsonParserDeserializerBase<T> extends StaticDeserializerBase<JsonParser,T> {

    private final Class<?> mDtoClass;
    private LoggingServiceInterface mLoggingService;

    protected StaticJsonParserDeserializerBase(Class<?> klass) {
        mDtoClass = klass;
    }

    protected void logDeserializationCompletion() {
        getLoggingService().log(new LogEvent(LogEventType.DEBUG, String.format("Completed " +
                "deserialization of a %s", mDtoClass.getSimpleName())));
    }

    protected void logDeserializationOfEmptyObject() {
        getLoggingService().log(new LogEvent(LogEventType.DEBUG, String.format("Empty Object " +
                "deserialization of a %s", mDtoClass.getSimpleName())));
    }

    protected String readTextMaybe(JsonParser parser, JsonToken actualToken, JsonToken
            expectedToken) throws IOException {
        if(actualToken.id() == JsonTokenId.ID_STRING) {
            return parser.getText();
        } else {
            logFieldTypeMismatch(expectedToken.name(), actualToken.name());
            return null;
        }
    }

    protected void logInterfaceConcreteTypeNotFound(String klass) {
        getLoggingService().log(new LogEvent(LogEventType.WARNING, String.format
                ("InterfaceConcreteTypeNotFound, Interface Type: %s, Type being deserialized: " +
                        "%s", klass, mDtoClass.getSimpleName()), mDtoClass.getSimpleName() +
                "StaticDeserializer"));
    }

    protected void logInterfaceConcreteTypeNotDefined(Class<?> klass) {
        getLoggingService().log(new LogEvent(LogEventType.WARNING, String.format
                ("InterfaceConcreteTypeNotDefined, Interface Type: %s, Type being deserialized: " +
                                "%s", klass.getSimpleName(), mDtoClass.getSimpleName()), mDtoClass.getSimpleName() +
                "StaticDeserializer"));
    }

    protected void logUnrecognizedJsonToken(JsonToken token, String currentName, String json) {
        getLoggingService().log(new LogEvent(LogEventType.WARNING, String.format
                ("UnrecognizedJsonToken, Token: %s, Name: %s, Value: %s", null != token ? token
                        .name() : "null", currentName, json), mDtoClass.getSimpleName() +
                "StaticDeserializer"));
    }

    protected void skipUnexpectedChild(String currentName, JsonParser parser) throws IOException {
        logUnexpectedField(currentName, parser.getValueAsString());
        JsonToken nextToken = parser.getCurrentToken();
        switch(nextToken.id()) {
            case JsonTokenId.ID_START_OBJECT:
            case JsonTokenId.ID_START_ARRAY:
                parser.skipChildren();
                break;
            default:

                break;
        }
    }

    protected void logUnexpectedField(String currentName, String json) {
        getLoggingService().log(new LogEvent(LogEventType.WARNING, String.format
                ("UnexpectedField, Name: %s, Value: %s", currentName, json)));
    }

    protected void logFieldTypeMismatch(String expected, String actual) {
        getLoggingService().log(new LogEvent(LogEventType.WARNING, String.format("Field type " +
                "mismatch, Expected: %s, Actual %s", expected, actual), mDtoClass.getSimpleName() +
                "StaticDeserializer"));
    }

    protected void logMissingInterfaceConcreteType(Class<?> klass) {
        getLoggingService().log(new LogEvent(LogEventType.ERROR, String.format("The concrete type " +
                "field is missing for type %s", klass.getSimpleName()), mDtoClass.getSimpleName() +
                "StaticDeserializer"));
    }

    protected void logFailedToDeserializeDate(String dateTimeString) {
        getLoggingService().log(new LogEvent(LogEventType.ERROR, String.format("Failed to " +
                "deserialize Date %s", dateTimeString), mDtoClass.getSimpleName() +
                "StaticDeserializer"));
    }

    public LoggingServiceInterface getLoggingService() {
        if (mLoggingService == null) {
            mLoggingService = ServiceLocator.getCurrent().resolve(LoggingServiceInterface.class);
        }
        return mLoggingService;
    }

    protected DateFormat getSimpleDateFormat() {
        return new DateFormat() {
            private static final String DATE_WITHOUT_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss";
            private static final String DATE_WITH_MILLISECONDS = "yyyy-MM-dd'T'HH:mm:ss.S";

            //Do not mind these warnings because we know that we don't need to rely on locale
            private final SimpleDateFormat mWithMillisecondsFormat
                    = new SimpleDateFormat(DATE_WITH_MILLISECONDS);
            private final SimpleDateFormat mWithoutMillisecondsFormat
                    = new SimpleDateFormat(DATE_WITHOUT_MILLISECONDS);
            @Override
            public StringBuffer format(final Date date, final StringBuffer toAppendTo, final FieldPosition fieldPosition) {
                throw new UnsupportedOperationException("Not implemented");
            }

            @Override
            public Date parse(final String source, final ParsePosition pos) {
                if (source.contains(".") ) {
                    return mWithMillisecondsFormat.parse(source, pos);
                } else {
                    return mWithoutMillisecondsFormat.parse(source, pos);
                }
            }
        };
    }

    protected JsonToken advanceParser(JsonParser parser) throws IOException {
        JsonToken nextToken = parser.nextToken();
        if (nextToken != null && nextToken.id() == JsonTokenId.ID_START_OBJECT) {
            parser.setCurrentValue(parser.getCurrentName());
        }
        return nextToken;
    }
}
