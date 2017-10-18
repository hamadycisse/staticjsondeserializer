package ca.appostrophe.helpers;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Hamady Cissé on 2015-12-14.
 */

public final class StringHelpers {


    public static final char UNBREAKABLE_SPACE = (char)0x00A0;


    public static final String EMPTY = "";


    public static String trimEnd(String value, char end) {
        return trimEnd(value, new String(new char[] { end }));
    }


    public static String trimEnd(String value, String end) {
        if (value == null || value.length() == 0) {
            return value;
        }
        int index = value.lastIndexOf(end);
        final int unwantedStringLength = end.length();
        while (index == value.length() - unwantedStringLength) {
            if (index < 0) {
                // Occurence not found, abort
                break;
            }

            value = value.substring(0, index);
            index = value.lastIndexOf(end);
        }
        return value;
    }


    public static String trimStart(String value, char start) {
        return trimStart(value, new String(new char[] { start }));
    }


    public static String trimStart(String value, String start) {
        if (value == null || value.length() == 0 || isNullOrEmpty(start)) {
            return value;
        }
        int index;
        do {
            index = value.indexOf(start);
            if (index == 0) {
                if (value.length() > 1) {
                    value = value.substring(index + start.length(), value.length());
                } else {
                    value = "";
                }
            }
        } while (index == 0);
        return value;
    }


    public static String toCamelCase(String name) {
        if (name == null) {
            return null;
        }
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }


    public static String concat(final List<String> strings, final String separator) {
        final StringBuilder builder = ArrayUtils.concat(strings, new StringBuilder(), new ArrayUtils
                .ListConcatenationInterface<String, StringBuilder>() {
            @Override
            public StringBuilder concat(final StringBuilder concatenation, final String value) {
                concatenation.append(String.format("%s%s",value,separator));
                return concatenation;
            }
        });
        return trimEnd(builder.toString(), separator);
    }


    public static boolean isNullOrEmpty(final String value) {
        return (value == null) || (value.isEmpty());
    }

    private static final SimpleDateFormat DURATION_FORMATTER = new SimpleDateFormat("mm:ss");
     @Deprecated //Use DateTimeServiceInterface.makeMediaDuration
    public static String createDurationText(final int durationInMs) {
        return DURATION_FORMATTER.format(new Date(durationInMs));
    }

    public static String formatWithDefaultLocale(String format, Object... params) {
        return isNullOrEmpty(format) ? format : String.format(Locale.getDefault(), format, params);
    }
}
