package ca.appostrophe.types.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.JsonTokenId;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

import ca.appostrophe.helpers.ArrayUtils;
import ca.appostrophe.helpers.StringHelpers;
import ca.appostrophe.serialization.SerializationException;
import ca.appostrophe.serialization.SerializerInterface;
import ca.appostrophe.serialization.StaticJsonParserDeserializerBase;
import ca.appostrophe.types.DynamicType;


/**
 * Created by Hamady Cissé on 2015-12-17.
 *
 * This class can be used to store a JSON structure into a key/value map {@link DynamicType}
 */
public class DynamicTypeStaticDeserializer extends StaticJsonParserDeserializerBase<HashMap<String, Object>> {

    public DynamicTypeStaticDeserializer() {
        super(HashMap.class);
    }

    public DynamicType deserialize(final JsonParser parser, final
    SerializerInterface<JsonParser> genericSerializer) throws SerializationException {
        return deserialize(parser);
    }

    public DynamicType deserialize(final JsonParser parser) throws SerializationException {
        final DynamicType values = new DynamicType();
        final String[] keyPrefixRef = new String[]{null};
        final ArrayList<Integer[]> indexRef = new ArrayList<>();
        final String startingContext = (String)parser.getCurrentValue();
        boolean deserializationCompleted = isDeserializationComplete(parser, startingContext);

        while (!parser.isClosed() && !deserializationCompleted) {
            try {
                deserializationCompleted = addContentValues(startingContext, values, parser,
                        keyPrefixRef, indexRef);
            } catch (IOException e) {
                throw new SerializationException(e, HashMap.class);
            }
        }
        return values;
    }

    /**
     * 
     * @param startingContext the object used to keep track of the first Json value
     * @param values the map used to store the value
     * @param jsonParser the parser
     * @param keyPrefixRef the prefix used to keep track of the current structured data (object, 
     *                     array)
     *                     NOTE: this parameter is an array because it is modified by the method 
     *                     (passed by reference like in C# ref)
     * @param indexesRefs the list of indexes of the the arrays found during the parsing.
     *                     NOTE: this parameter is an array because it is modified by the method 
     *                     (passed by reference like in C# ref)
     * @return  true if the deserialization of the dynamic type was completed
     * @throws IOException if the deserialization fails
     */
    private static boolean addContentValues(String startingContext, DynamicType
            values, JsonParser jsonParser, String[] keyPrefixRef, ArrayList<Integer[]> indexesRefs)
            throws IOException {
        JsonToken token = jsonParser.nextToken();
        boolean deserializationCompleted = false;
        jsonParser.setCurrentValue(jsonParser.getCurrentName());
        if (token != null) {
            JsonToken lastClearedToken = jsonParser.getLastClearedToken();
            String currentName = jsonParser.getCurrentName();
            //Build the property syntax for the prefix (ie: object(property) )
            String fieldKey = StringHelpers.isNullOrEmpty(keyPrefixRef[0])? currentName :
                    StringHelpers.formatWithDefaultLocale("%s(%s)", keyPrefixRef[0], currentName);
            String newKeyPrefix;
            switch (token.id()) {
                case JsonTokenId.ID_START_OBJECT:
                    if (StringHelpers.isNullOrEmpty(keyPrefixRef[0])) {
                        //No prefix so far, simply use the name of the current token
                        newKeyPrefix = currentName;
                    } else if (isTokenAnArrayItem(currentName)) {
                        newKeyPrefix = updateKeyPrefixBecauseItsArrayItem(keyPrefixRef[0], indexesRefs);
                    } else {
                        //Append the name of the current token to the prefix using the object
                        // suffix (ie: parent.child_object)
                        newKeyPrefix = StringHelpers.formatWithDefaultLocale("%s.%s", keyPrefixRef[0], currentName);
                    }
                    keyPrefixRef[0] = newKeyPrefix;
                    addContentValues(startingContext, values, jsonParser, keyPrefixRef, indexesRefs);
                    break;
                case JsonTokenId.ID_START_ARRAY:
                    //We are starting an array, set the index to 0
                    indexesRefs.add(new Integer[] { 0 });
                    keyPrefixRef[0] = fieldKey;
                    updateLastClearedToken(jsonParser);
                    addContentValues(startingContext, values, jsonParser, keyPrefixRef, indexesRefs);
                    break;
                case JsonTokenId.ID_END_ARRAY:
                    //The array has ended without any elements
                    if (lastClearedToken != null && lastClearedToken.id() == JsonTokenId.ID_START_ARRAY) {
                        values.put(keyPrefixRef[0], "[]");
                    }
                    if (!StringHelpers.isNullOrEmpty(keyPrefixRef[0]) && !StringHelpers.isNullOrEmpty(currentName)) {
                        final Integer[] lastItemIndexRef = ArrayUtils.last(indexesRefs);
                        //Trim the key of an array from the key prefix since the array ended
                        //a prefix can be: object(array)[N], object.array[N], object.array if the
                        // array is empty
                        keyPrefixRef[0] = Pattern.compile(StringHelpers
                                .formatWithDefaultLocale("(\\.)?(\\()?%s(\\))?(\\[%d\\])?$", currentName, lastItemIndexRef[0] - 1))
                                .matcher(keyPrefixRef[0])
                                .replaceFirst(StringHelpers.EMPTY);
                    }
                    //Remove the reference of the integer used to keep track of the indexes of the
                    // current array
                    indexesRefs.remove(indexesRefs.size() - 1);
                    break;
                case JsonTokenId.ID_END_OBJECT:
                    if (!StringHelpers.isNullOrEmpty(keyPrefixRef[0])) {
                        //The object ended, remove its name from the key prefix.
                        if (StringHelpers.isNullOrEmpty(currentName)) {
                            //The object was array item, remove the array key prefix (ie: [i])
                            final String charSequence = keyPrefixRef[0];
                            keyPrefixRef[0] = Pattern.compile("\\[(\\d)+\\]$")
                                    .matcher(charSequence).replaceFirst(StringHelpers.EMPTY);
                        } else {
                            //A suffix of an object can be: root_object.object(object_property) or
                            // root_object.object or root_object
                            keyPrefixRef[0] = Pattern.compile(StringHelpers
                                    .formatWithDefaultLocale("(\\.[.^\\.]*)?(\\()?%s(\\))?$", currentName))
                                    .matcher(keyPrefixRef[0])
                                    .replaceFirst(StringHelpers.EMPTY);
                        }
                    }
                    deserializationCompleted = isDeserializationComplete(jsonParser, startingContext);
                    updateLastClearedToken(jsonParser);
                    break;
                case JsonTokenId.ID_FIELD_NAME:
                    addContentValues(startingContext, values, jsonParser, keyPrefixRef, indexesRefs);
                    updateLastClearedToken(jsonParser);
                    break;
                case JsonTokenId.ID_NUMBER_FLOAT:
                    //Scalar value, just add it to the map
                    values.put(getValueKey(keyPrefixRef, indexesRefs, currentName, fieldKey), jsonParser.getFloatValue());
                    break;
                case JsonTokenId.ID_NUMBER_INT:
                    //Scalar value, just add it to the map
                    values.put(getValueKey(keyPrefixRef, indexesRefs, currentName, fieldKey), jsonParser.getLongValue());
                    break;
                case JsonTokenId.ID_STRING:
                      //Scalar value, just add it to the map
                    values.put(getValueKey(keyPrefixRef, indexesRefs, currentName, fieldKey), jsonParser.getText());
                    break;
                case JsonTokenId.ID_TRUE:
                case JsonTokenId.ID_FALSE:
                    //Scalar values, just add them to the map
                    values.put(getValueKey(keyPrefixRef, indexesRefs, currentName, fieldKey), jsonParser.getBooleanValue());
                    break;
                case JsonTokenId.ID_NULL:
                    //Handle null values
                    values.put(fieldKey, "null");
                    break;
                default:
                    break;
            }
        }
        return deserializationCompleted;
    }

    private static String getValueKey(final String[] keyPrefixRef, final ArrayList<Integer[]> indexesRefs,
                                      final String currentName, final String fieldKey) {
        String newKeyPrefix = fieldKey;
        if (isTokenAnArrayItem(currentName)) {
            keyPrefixRef[0] = updateKeyPrefixBecauseItsArrayItem(keyPrefixRef[0], indexesRefs);
            newKeyPrefix = keyPrefixRef[0];
        }
        return newKeyPrefix;
    }

    //In order to keep track of the beginning of a new array, reset the stored
    // state of the parser so that the next call to getLastClearToken() returns
    // a START_ARRAY token if the array is empty.
    private static void updateLastClearedToken(final JsonParser jsonParser) {
        jsonParser.clearCurrentToken();
    }

    /**
     *
     * @param currentName the name of the current token
     * @return true if the token represent an object or value which belongs to an array
     */
    private static boolean isTokenAnArrayItem(final String currentName) {
        return StringHelpers.isNullOrEmpty(currentName);
    }

    private static String updateKeyPrefixBecauseItsArrayItem(final String keyPrefix, final ArrayList<Integer[]> indexesRefs) {
        final String newKeyPrefix;
        if (keyPrefix != null && keyPrefix.endsWith("]")) {
            //For subsequent appends to the prefix
            newKeyPrefix = keyPrefix.replaceFirst(StringHelpers.formatWithDefaultLocale("\\[%d\\]$",
                    ArrayUtils.last(indexesRefs)[0] - 1), StringHelpers.formatWithDefaultLocale("[%d]", ArrayUtils.last(indexesRefs)[0]++));
        } else {
            //For the first append to the prefix
            newKeyPrefix = StringHelpers.formatWithDefaultLocale("%s[%d]", keyPrefix, ArrayUtils.last(indexesRefs)[0]++);
        }
        return newKeyPrefix;
    }

    private static boolean isDeserializationComplete(JsonParser parser, String startingContext) {
        boolean isEndOfObject = null != parser.getCurrentToken() && parser.getCurrentToken().id()
                == JsonTokenId.ID_END_OBJECT;
        return isEndOfObject && startingContext != null && startingContext.equals(parser
                .getCurrentValue());
    }
}
