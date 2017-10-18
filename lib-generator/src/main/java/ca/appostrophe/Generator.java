package ca.appostrophe;

import com.google.common.collect.Lists;

import org.reflections.Reflections;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ca.appostrophe.annotations.IsAMap;
import ca.appostrophe.annotations.IsAnonymousArray;
import ca.appostrophe.annotations.SerializeStatically;
import ca.appostrophe.helpers.ArrayUtils;
import ca.appostrophe.helpers.StringHelpers;
import ca.appostrophe.internal.DtosPackageList;
import ca.appostrophe.internal.SerializationSupportInfo;
import ca.appostrophe.types.DynamicType;

public class Generator {

    private static boolean sGenerateUsingTestPackage = false;

    private static String sCurrent = System.getProperty("user.dir");
    private static String sSerializersModuleName = "serialization";
    private static String sSerializersModuleNameUsingTestPackage = "fx-support";

    private static String sAppNameInPackageName = "app";
    private static String sDtoInterfaceTypeJsonFieldName = "$type";

    private static String sTypedSerializersPackageNameSuffix = ".gen.typed";
    private static String sGenericSerializersPackageNameSuffix = ".gen";
    private static String sStaticJsonParserDeserializerBaseClassName = "StaticJsonParserDeserializerBase";
    private static String sGenericSerializerInterface = "SerializerInterface";
    private static String sJsonParserGenericSerializerInterface = "JsonParserSerializerInterface";
    private static String sSerializationExceptionTypeName = "SerializationException";
    private static String sSerializationExceptionTypePackageName = "ca.appostrophe.serialization";
    @SuppressWarnings("FieldCanBeLocal") private static String sStaticJsonDeserializerBasePackageName = "ca.appostrophe.serialization";
    private static String sSerializerInterfacePackageName = "ca.appostrophe.serialization";
    private static String sGenericStaticSerializerClassName = "GenericStaticDeserializer";
    @SuppressWarnings("FieldCanBeLocal") private static String sDynamicTypeStaticDeserializerType = "DynamicTypeStaticDeserializer";
    @SuppressWarnings("FieldCanBeLocal") private static String sDynamicTypeStaticDeserializerPackageName = "ca.appostrophe.types.serialization";

    private static final String[] LANGUAGE_RESERVED_WORDS = new String[]{
            "abstract", "continue", "for", "new", "switch",
            "assert", "default", "goto", "package", "synchronized",
            "boolean", "do", "if", "private", "this",
            "break", "double", "implements", "protected", "throw",
            "byte", "else", "import", "public", "throws",
            "case", "enum", "instanceof", "return", "transient",
            "catch", "extends", "int", "short", "try",
            "char", "final", "interface", "static", "void",
            "class", "finally", "long", "strictfp", "volatile",
            "const", "float", "native", "super", "while"
    };

    @SuppressWarnings("unused")
    public void generate(final SerializationSupportInfo supportInfo) {

        sAppNameInPackageName = supportInfo.getSerializationPackageName();
        sDtoInterfaceTypeJsonFieldName = supportInfo.getPolymorphicDtosTypeKeyword();
        final DtosPackageList dtosPackagesNames = supportInfo.getDtosPackageList();

        final Reflections reflection = new Reflections(dtosPackagesNames.toArray());
        final Set<Class<?>> types = reflection
                .getTypesAnnotatedWith(SerializeStatically.class);
        String dummyFileName = createSerializerFileName(Object.class);
        File existingDeserializers = new File(dummyFileName.substring(0, dummyFileName.lastIndexOf
                (File.separator)));
        boolean result = true;
        if(existingDeserializers.exists()) {
            File[] existingFiles = existingDeserializers.listFiles();

            if (existingFiles != null) {
                for(File f : existingFiles) {
                    result = result && f.delete();
                }
            }
        } else {
            result = existingDeserializers.mkdirs();
        }
        if (!result) {
            throw new RuntimeException(String.format("Could not delete or make directory %s",
                    existingDeserializers.getAbsolutePath()));
        }
        if ((null != types) && (types.size() > 0)) {
            for (final Class<?> klass : types) {
                Generator.createTypedStaticDeserializer(klass);
            }
            createGenericStaticDeserializer(types);
        }
    }

    private static void createGenericStaticDeserializer(Set<Class<?>> types) {

        final String filePath = Generator.createGenericSerializerFileName();
        final File file = createFile(filePath, sGenericStaticSerializerClassName, true);

        try {
            Generator.writeGenericDeserializer(types, file);
        } catch (final IOException exception) {
            throw new RuntimeException(
                    String.format(
                            "Fatal failure during the generation of the static serializer of type '%1$s'",
                            sGenericStaticSerializerClassName), exception);
        }
    }

    private static void writeGenericDeserializer(Set<Class<?>> types, File file) throws IOException {

        final FileWriter writer = new FileWriter(file);

        writeGenericDeserializerHeader(types, writer);
        jumpLine(writer);
        writeLine(writer, createGenericClassDeclarationStatement());
        jumpLine(writer);
        types.add(DynamicType.class);   //To Handle dynamic object types
        writeGenericDeserializerFieldDeclaration(writer, types);
        jumpLine(writer);
        writeGenericDeserializerConstructor(types, writer);
        jumpLine(writer);
        writeGenericDeserializerDeserializeMethod(writer, types);
        jumpLine(writer);
        writeLine(indentLine(writer, 1), "@Override");
        writeLine(indentLine(writer, 1), "public <TObject> InputStream serialize(TObject entity, Class<TObject> klass) {");
        writeLine(indentLine(writer, 2), "return null;");
        closeBracket(indentLine(writer, 1));
        closeBracket(writer);

        writer.flush();
        writer.close();
    }

    private static void createTypedStaticDeserializer(final Class<?> klass) {

        final String filePath = Generator.createSerializerFileName(klass);
        final File file = createFile(filePath, klass.getSimpleName());

        try {
            Generator.writeDeserializer(klass, file);
        } catch (final IOException exception) {
            throw new RuntimeException(
                    String.format(
                            "Fatal failure during the generation of the static serializer of type '%1$s'",
                            klass.getCanonicalName()), exception);
        }
    }

    private static void writeDeserializer(final Class<?> klass, final File file)
            throws IOException {
        final FileWriter writer = new FileWriter(file);
        final List<Field> fields = Generator.getFieldsUpTo(klass, Object.class);
        final ArrayList<Field> objectFieldsList = new ArrayList<>();
        final ArrayList<Field> primitivesFieldsList = new ArrayList<>();
        final HashMap<Field, Class<?>> arraysFieldsMap = new HashMap<>();
        final HashMap<Field, Class<?>> mapFieldsList = new HashMap<>();
        int size = fields.size();
        for (int i = size - 1; i >= 0; --i) {
            Field field = fields.get(i);
            Class<?> type = field.getType();
            if (isASimpleType(type)) {
                primitivesFieldsList.add(field);
            } else if (type.isArray() || ArrayList.class.equals(type) || List.class.equals(type)) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                Class<?> clazz = (Class<?>) genericType.getActualTypeArguments()[0];
                arraysFieldsMap.put(field, clazz);
            } else if (HashMap.class.equals(type) || Map.class.equals(type)) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                //noinspection StatementWithEmptyBody
                if (genericType.getActualTypeArguments().length > 0 && genericType
                        .getActualTypeArguments()[1] instanceof Class<?>) {
                    Class<?> clazz = (Class<?>) genericType.getActualTypeArguments()[1];
                    mapFieldsList.put(field, clazz);
                } else {
                    //Complex generics are not supported
                }
            } else {
                objectFieldsList.add(field);
            }
        }

        writeDeserializerHeader(klass, writer, objectFieldsList, arraysFieldsMap, mapFieldsList);

        jumpLine(writer);

        writeLine(writer, createClassDeclarationStatement(klass));

        jumpLine(writer);

        writeDeserializerConstructor(klass, writer);

        jumpLine(writer);

        writeDeserializerDeserializeMethod(klass, writer, objectFieldsList,
                primitivesFieldsList, mapFieldsList, arraysFieldsMap);

        closeBracket(writer);

        writer.flush();
        writer.close();
    }

    private static void writeGenericDeserializerDeserializeMethod(FileWriter writer,
                                                                  Set<Class<?>> types) throws
            IOException {

        writeLine(indentLine(writer, 1), "@Override");
        writeLine(indentLine(writer, 1), "public <TObject> TObject deserialize(JsonParser parser," +
                " Class<TObject> klass) throws SerializationException {");
        boolean addElse = false;
        for (Class<?> klass : types) {
            String typeSimpleName = createTypeSimpleName(klass);
            writeLine(indentLine(writer, 2), String.format("%sif (%s.class.equals(klass)) {",
                    addElse ? "} else " : "", typeSimpleName));
            writeLine(indentLine(writer, 3), String.format("return klass" +
                            ".cast(m%sStaticDeserializer.deserialize(parser, this));",
                    typeSimpleName));
            addElse = true;
        }
        if (addElse) {
            writeLine(indentLine(writer, 2), "} else");
            writeLine(indentLine(writer, 3), String.format("throw new %s(\"Unsupported DTO " +
                    "type: \" + klass.getName(), klass);", sSerializationExceptionTypeName));
        } else {
            writeLine(indentLine(writer, 2), String.format("throw new %s(\"Unsupported DTO " +
                    "type: \" + klass.getName(), klass);", sSerializationExceptionTypeName));
        }
        closeBracket(indentLine(writer, 1));
    }

    private static void writeDeserializerDeserializeMethod(Class<?> klass, FileWriter writer,
                                                           ArrayList<Field> objectFieldsList,
                                                           ArrayList<Field> primitivesFieldsList,
                                                           HashMap<Field, Class<?>>
                                                                   mapFieldsList,
                                                           HashMap<Field, Class<?>> arraysFieldsMap)
            throws IOException {
        writeLine(indentLine(writer, 1), String.format("@Override\r\n" + generateTab() + "public %s " +
                "deserialize(JsonParser parser, SerializerInterface<JsonParser> " +
                "genericSerializer) throws SerializationException {", createTypeSimpleName(klass)));
        writeLine(indentLine(writer, 2), "try {");
        writeLine(indentLine(writer, 3), String.format("final %1$s %2$s = new %1$s();",
                createTypeSimpleName(klass), createVariableName(klass)));
        writeLine(indentLine(writer, 3), "JsonToken token = advanceParser(parser);");
        writeLine(indentLine(writer, 3), "String currentName;");
        writeLine(indentLine(writer, 3), "do {");
        writeLine(indentLine(writer, 4), "if (token != null) {");
        writeLine(indentLine(writer, 5), "currentName = parser.getCurrentName();");
        writeLine(indentLine(writer, 5), "switch (token.id()) {");

        writeLine(indentLine(writer, 6), "case JsonTokenId.ID_START_OBJECT:");
        if(objectFieldsList.size() > 0 || mapFieldsList.size() > 0) {
            writeDeserializerDeserializeObject(klass, writer, 7, objectFieldsList, mapFieldsList);
        } else {
            writeLine(indentLine(writer, 7), "if (currentName != null) {");
            writeLine(indentLine(writer, 8), "//Skip any OBJECT because the type does not have any");
            writeLine(indentLine(writer, 8), "skipUnexpectedChild(currentName, parser);");
            writeLine(indentLine(writer, 7), "}");
        }
        writeLine(indentLine(writer, 7), "break;");

        writeLine(indentLine(writer, 6), "case JsonTokenId.ID_START_ARRAY:");
        if (arraysFieldsMap.size() > 0) {
            final boolean isArray = true;
            final boolean ignoreRootElement = klass.getAnnotation(IsAnonymousArray.class) != null;
            writeDeserializerDeserializeArrayOrMap(klass, writer, 7, arraysFieldsMap,
                    false, isArray, ignoreRootElement, true /*addSkipUnexpectedChildCondition*/);
        } else {
            writeLine(indentLine(writer, 7), "if (currentName != null) {");
            writeLine(indentLine(writer, 8), "//Skip any ARRAY because the type does not have any");
            writeLine(indentLine(writer, 8), "skipUnexpectedChild(currentName, parser);");
            writeLine(indentLine(writer, 7), "}");
        }
        writeLine(indentLine(writer, 7), "break;");

        //Handle any descendant of MappedDto as if they have fields because the content of the
        // json fields will be stored in the map of MappedDto
        if(primitivesFieldsList.size() > 0) {
            writeLine(indentLine(writer, 6), "case JsonTokenId.ID_FIELD_NAME:");
            writeDeserializerDeserializeField(klass, writer, 7, objectFieldsList,
                    primitivesFieldsList, mapFieldsList, arraysFieldsMap);
            writeLine(indentLine(writer, 7), "break;");
        }
        writeLine(indentLine(writer, 6), "case JsonTokenId.ID_END_OBJECT:");
        writeLine(indentLine(writer, 7), "logDeserializationOfEmptyObject();");
        writeLine(indentLine(writer, 7), String.format("return %1$s;", createVariableName(klass)));
        writeLine(indentLine(writer, 5), "}");
        writeLine(indentLine(writer, 4), "}");
        writeLine(indentLine(writer, 4), "token = advanceParser(parser);");
        writeLine(indentLine(writer, 3), "} while (!parser.isClosed() && (token == null || " +
                "(token.id() != JsonTokenId.ID_END_OBJECT)));");
        writeLine(indentLine(writer, 3), String.format("return %s;", createVariableName
                (klass)));
        writeLine(indentLine(writer, 2), "} catch (IOException e) {");
        writeLine(indentLine(writer, 3), String.format("throw new SerializationException" +
                "(e, %s.class);", createTypeSimpleName(klass)));
        writeLine(indentLine(writer, 2), "}");
        closeBracket(indentLine(writer, 1));
    }

    private static void writeDeserializerDeserializeField(final Class<?> klass, final FileWriter
            writer, final int indent, final ArrayList<Field> objectFieldsList, final
                                                          ArrayList<Field> primitivesFieldsList, final HashMap<Field, Class<?>> mapFieldsList, final
                                                          HashMap<Field, Class<?>> arraysFieldsMap) throws IOException {

        writeLine(indentLine(writer, indent), "JsonToken fieldToken;");
        StringBuilder objectFieldCondition = new StringBuilder("if (");
        boolean addElseField = false;
        boolean addOr = false;
        for (Field f : objectFieldsList) {
            addElseField = true;
            objectFieldCondition.append(String.format("%s\"%s\".equals(currentName)", addOr ?
                    "|| " : "", f.getName()));
            addOr = true;
        }
        for (Map.Entry<Field, Class<?>> entry : mapFieldsList.entrySet()) {
            addElseField = true;
            objectFieldCondition.append(String.format("%s\"%s\".equals(currentName)", addOr ?
                    " || " : "", entry.getKey().getName()));
            addOr = true;
        }
        for (Map.Entry<Field, Class<?>> entry : arraysFieldsMap.entrySet()) {
            addElseField = true;
            objectFieldCondition.append(String.format("%s\"%s\".equals(currentName)", addOr ?
                    " || " : "", entry.getKey().getName()));
            addOr = true;
        }
        if (addOr) {
            objectFieldCondition.append(") {");
            writeLine(indentLine(writer, indent), objectFieldCondition.toString());
            writeLine(indentLine(writer, indent + 1), "//This is either an object or and array, " +
                    "let the parser move to the next token");
            writeLine(indentLine(writer, indent + 1), "//in order to call the appropriate static " +
                    "deserializer");
        }

        for (Field f : primitivesFieldsList) {
            writeFieldDeserialization(writer, f, indent, klass, addElseField);
            addElseField = true;
        }

        if (addElseField) {
            writeLine(indentLine(writer, indent), "} else if (currentName != null) {");
            writeLine(indentLine(writer, indent + 1),
                    "skipUnexpectedChild(currentName, parser);");
            closeBracket(indentLine(writer, indent));
        }
    }

    private static boolean writeDeserializerDeserializeArrayOrMapItem(final boolean isArray,
                                                                      final Class<?> klass, final FileWriter writer, final int indent,
                                                                      final HashMap<Field, Class<?>> arraysFieldsMap, final boolean addElse,
                                                                      final boolean ignoreRootElement) throws IOException {
        boolean addElseArrays = addElse;
        final String itemVariableName = isArray ? "arrayItemToken" : "mapItemToken";
        writeLine(indentLine(writer, indent), String.format("JsonToken %s = advanceParser" +
                "(parser);", itemVariableName));
        writeLine(indentLine(writer, indent), String.format("if (%1$s != null && " +
                "%1$s.id() != JsonTokenId.ID_NULL && " +
                (isArray ? "%1$s.id() != JsonTokenId.ID_END_ARRAY)"
                        : "%1$s.id() != JsonTokenId.ID_END_OBJECT)")
                + " {", itemVariableName));
        for (Map.Entry<Field, Class<?>> entry : arraysFieldsMap.entrySet()) {
            Field f = entry.getKey();
            Class<?> genericKlass = entry.getValue();
            writeArrayOrMapFieldDeserialization(isArray, writer, f, indent + 1, klass, genericKlass,
                    itemVariableName, addElseArrays, ignoreRootElement);
            addElseArrays = true;
        }

        if (!ignoreRootElement) {

            closeBracket(indentLine(writer, indent + 1));    //if

            if (addElseArrays) {
                writeLine(indentLine(writer, indent + 1), "else if (currentName != null) {");
                writeLine(indentLine(writer, indent + 2), String.format("logUnrecognizedJsonToken" +
                        "(%s, currentName, parser.getValueAsString());", itemVariableName));
                writeLine(indentLine(writer, indent + 2), "skipUnexpectedChild(currentName, parser);");
                writeLine(indentLine(writer, indent + 1), "}");
            }
        }
        closeBracket(indentLine(writer, indent));
        return addElseArrays;
    }

    private static void writeDeserializerDeserializeObject(final Class<?> klass, final FileWriter
            writer, final int i,  final ArrayList<Field> objectFieldsList, final HashMap<Field,
            Class<?>> mapFieldsList) throws IOException {
        boolean addElse = false;
        for (Field f : objectFieldsList) {
            writeObjectFieldDeserialization(writer, f, i, klass, addElse);
            addElse = true;
        }

        final boolean ignoreRootElement = klass.getAnnotation(IsAMap.class) != null;
        final boolean addElseBecauseOfMapOrArrayItem
            = writeDeserializerDeserializeArrayOrMap(klass, writer,
                i, mapFieldsList, addElse,
                false /*isArray*/, ignoreRootElement,
                false  /*addSkipUnexpectedChildCondition*/ );

        if (!ignoreRootElement && (addElseBecauseOfMapOrArrayItem || addElse)) {
            writeLine(indentLine(writer, i), "} else if (currentName != null) {");
            writeLine(indentLine(writer, i + 1), "logUnrecognizedJsonToken" +
                    "(token, currentName, parser.getValueAsString());");
            writeLine(indentLine(writer, i + 1), "skipUnexpectedChild(currentName, parser);");
            writeLine(indentLine(writer, i), "}");
        }
    }

    private static boolean writeDeserializerDeserializeArrayOrMap(
            final Class<?> klass, final FileWriter writer, final int i,
            final HashMap<Field, Class<?>> arrayOrMapFieldsList, boolean addElse,
            final boolean isArray, final boolean ignoreRootElement,
            final boolean addSkipUnexpectedChildCondition) throws IOException {
        final StringBuilder isAMapFieldConditionBuilder = new StringBuilder();
        final boolean shouldCheckUnrecognizedTokenName = !ignoreRootElement;
        if (shouldCheckUnrecognizedTokenName) {
            for (Field field : arrayOrMapFieldsList.keySet()) {
                isAMapFieldConditionBuilder.append(String.format("\"%s\".equals(currentName) ||",
                        field.getName()));
            }
        }
        boolean addElseForArrayOrMap = false;
        if (arrayOrMapFieldsList.size() > 0) {
            final String isAMapFieldCondition = StringHelpers.trimEnd(isAMapFieldConditionBuilder
                    .toString(), " ||");
            int baseIndent = i;
            if (shouldCheckUnrecognizedTokenName) {
                writeLine(indentLine(writer, baseIndent), String.format("%sif (%s) {"
                        , addElse ? "} else " : StringHelpers.EMPTY, isAMapFieldCondition));
            } else {
                baseIndent -= 1;
                addElseForArrayOrMap = addElse;
            }
            addElseForArrayOrMap = writeDeserializerDeserializeArrayOrMapItem
                    (isArray, klass, writer, baseIndent + 1, arrayOrMapFieldsList, addElseForArrayOrMap,
                            ignoreRootElement);
        }
        if (shouldCheckUnrecognizedTokenName && addSkipUnexpectedChildCondition && addElseForArrayOrMap) {
            writeLine(indentLine(writer, i), "} else if (currentName != null) {");
            writeLine(indentLine(writer, i + 1), "logUnrecognizedJsonToken" +
                    "(token, currentName, parser.getValueAsString());");
            writeLine(indentLine(writer, i + 1), "skipUnexpectedChild(currentName, parser);");
            writeLine(indentLine(writer, i), "}");
        }
        return addElseForArrayOrMap;
    }

    private static void writeGenericDeserializerFieldDeclaration(FileWriter writer, Set<Class<?>>
            types) throws IOException {
        for (Class<?> klass : types) {
            String typeSimpleName = createTypeSimpleName(klass);
            writeLine(indentLine(writer, 1), String.format("private final %1$sStaticDeserializer " +
                    "m%1$sStaticDeserializer;", typeSimpleName));
        }
    }

    private static void writeGenericDeserializerConstructor(Set<Class<?>> types, FileWriter writer)
            throws IOException {
        writeLine(indentLine(writer, 1), "public GenericStaticDeserializer() {");
        for (Class<?> klass : types) {
            String typeSimpleNameForInitialization = createTypeSimpleName(klass);
            writeLine(indentLine(writer, 2), String.format("m%sStaticDeserializer = " +
                            "new %sStaticDeserializer();", typeSimpleNameForInitialization,
                    typeSimpleNameForInitialization));
        }
        closeBracket(indentLine(writer, 1));
    }

    private static void writeDeserializerConstructor(Class<?> klass, FileWriter writer) throws IOException {
        writeLine(indentLine(writer, 1), String.format("public %sStaticDeserializer() {",
                createTypeSimpleName(klass)));
        writeLine(indentLine(writer, 2), String.format("super(%s.class);" +
                "", createTypeSimpleName(klass)));
        closeBracket(indentLine(writer, 1));
    }

    private static void writeGenericDeserializerHeader(Set<Class<?>> types, FileWriter writer) throws IOException {
        writeGenerationDisclaimer(writer, "/*", String.format(" THIS CLASS WAS GENERATED BY THE " +
                "SUPPORT MODULE " +
                "ON %s, RUN THE SUPPORT MODULE TO RE-GENERATE THEM", new Date().toString()), "*/");

        jumpLine(writer);

        writeLine(writer, String.format("package %s;", sAppNameInPackageName +
                sGenericSerializersPackageNameSuffix));
        jumpLine(writer);

        writeLine(writer, createImportStatement("com.fasterxml.jackson.core.JsonParser"));
        jumpLine(writer);
        writeLine(writer, createImportStatement("java.io.InputStream"));
        writeLine(writer, createImportStatement("ca.appostrophe.types.DynamicType"));
        jumpLine(writer);
        for (Class<?> klass : types) {
            writeLine(writer, createImportStatement(klass.getName()));
            jumpLine(writer);
        }
        jumpLine(writer);
        for (Class<?> klass : types) {
            writeLine(writer, createImportStatement(String.format("%s.%sStaticDeserializer",
                    sAppNameInPackageName + sTypedSerializersPackageNameSuffix, createTypeSimpleName(klass))));
            jumpLine(writer);
        }
        jumpLine(writer);
        writeLine(writer, createImportStatement(String.format("%s.%s",
                sSerializerInterfacePackageName, sGenericSerializerInterface)));
        writeLine(writer, createImportStatement(String.format("%s.%s",
                sSerializerInterfacePackageName, sJsonParserGenericSerializerInterface)));
        writeLine(writer, createImportStatement(String.format("%s.%s",
                sSerializationExceptionTypePackageName, sSerializationExceptionTypeName)));
        writeLine(writer, createImportStatement(String.format("%s.%s",
                sDynamicTypeStaticDeserializerPackageName, sDynamicTypeStaticDeserializerType)));
        jumpLine(writer);
    }

    private static void writeDeserializerHeader(final Class<?> klass, final FileWriter writer,
                                                final ArrayList<Field> objectFieldsList,
                                                final HashMap<Field, Class<?>> arraysFieldsList,
                                                final HashMap<Field, Class<?>> mapFieldsList)
            throws  IOException {
        writeGenerationDisclaimer(writer, "/*", String.format(" THIS CLASS WAS GENERATED BY THE " +
                "SUPPORT MODULE " +
                "ON %s, RUN THE SUPPORT MODULE TO RE-GENERATE THEM", new Date().toString()), "*/");

        writeLine(writer, String.format("package %s;", sAppNameInPackageName +
                sTypedSerializersPackageNameSuffix));
        jumpLine(writer);

        writeLine(writer, createImportStatement("com.fasterxml.jackson.core.JsonParser"));
        writeLine(writer, createImportStatement("com.fasterxml.jackson.core.JsonToken"));
        writeLine(writer, createImportStatement("com.fasterxml.jackson.core.JsonTokenId"));
        jumpLine(writer);
        writeLine(writer, createImportStatement("java.io.IOException"));
        writeLine(writer, createImportStatement("java.text.ParseException"));
        writeLine(writer, createImportStatement("ca.appostrophe.types.DynamicType"));
        jumpLine(writer);
        writeLine(writer, createImportStatement(klass.getName()));
        writeLine(writer, createImportStatement(String.format("%s.%s",
                sStaticJsonDeserializerBasePackageName, sStaticJsonParserDeserializerBaseClassName)));
        writeLine(writer, createImportStatement(String.format("%s.%s",
                sSerializerInterfacePackageName, sGenericSerializerInterface)));
        writeLine(writer, createImportStatement(String.format("%s.%s",
                sSerializationExceptionTypePackageName, sSerializationExceptionTypeName)));
        jumpLine(writer);
        for (Field f : objectFieldsList) {
            writeLine(writer, createImportStatement(f.getType().getName()));
        }
        if (arraysFieldsList.size() > 0 || mapFieldsList.size() > 0) {
            StringBuilder consolidatedGenericTypeImportStatements = new StringBuilder();
            for (Map.Entry<Field, Class<?>> entry : arraysFieldsList.entrySet()) {
                Class<?> genericKlass = entry.getValue();
                if (consolidatedGenericTypeImportStatements.indexOf(genericKlass.getName()) < 0) {
                    consolidatedGenericTypeImportStatements.append(createImportStatement(genericKlass
                            .getName()) + "\r\n");
                }
            }
            for (Map.Entry<Field, Class<?>> entry : mapFieldsList.entrySet()) {
                Class<?> genericKlass = entry.getValue();
                if (consolidatedGenericTypeImportStatements.indexOf(genericKlass.getName()) < 0) {
                    consolidatedGenericTypeImportStatements.append(createImportStatement(genericKlass
                            .getName()) + "\r\n");
                }
            }
            writeLine(writer, consolidatedGenericTypeImportStatements.toString());
        }
    }

    private static void writeGenerationDisclaimer(FileWriter writer, String line, String line2, String line3) throws IOException {
        writeLine(writer, line);
        jumpLine(writer);
        writeLine(writer, line2);
        jumpLine(writer);
        writeLine(writer, line3);
    }

    private static void writeArrayOrMapFieldDeserialization(final boolean isArray,
                                                            final FileWriter writer, final Field f, final int i, final Class<?> klass,
                                                            final Class<?> genericKlass, final String itemVariableName, final boolean addElse,
                                                            final boolean ignoreRootElement) throws IOException {

        int indent = ignoreRootElement ? i - 1 : i;
        if (!ignoreRootElement || isArray) {
            writeLine(indentLine(writer, indent), String.format("%sif (\"%s\".equals(%s)) {",
                    addElse ? "} else " : "", f.getName(), "currentName"));
        }
        writeLine(indentLine(writer, indent + 1), "do {");
        final String insertionStatement = isArray ? "add(" : "put(parser.getCurrentName(), ";
        boolean isDate = Date.class.equals(genericKlass);
        // The type of the field is String, int, etc.., so we will simply do a getValueAsString(),
        // getValueAsInt(), etc..
        if (isASimpleType(genericKlass)) {
            writeLine(indentLine(writer, indent + 2), String.format("if (%s.id() != JsonTokenId" +
                    ".ID_NULL) {", itemVariableName));
            if (isDate) {
                writeLine(indentLine(writer, indent + 3), String.format("String stringValue = parser" +
                        ".get%s();", createParserGetSuffix(f.getType())));
                writeLine(indentLine(writer, indent + 3), "try {");
                writeLine(indentLine(writer, indent + 4), String.format("%s.get%s().%s" +
                                "getSimpleDateFormat().parse(parser.get%s()));",
                        createVariableName(klass), toPascalCase(f.getName()),
                        insertionStatement, createParserGetSuffix(genericKlass)));
                writeLine(indentLine(writer, indent + 3), "} catch (ParseException e){");
                writeLine(indentLine(writer, indent + 4), "logFailedToDeserializeDate(stringValue);");
                closeBracket(indentLine(writer, indent + 3));
            } else {
                writeLine(indentLine(writer, indent + 3), String.format("%s.get%s().%sparser.get%s());",
                        createVariableName(klass), toPascalCase(f.getName()),
                        insertionStatement, createParserGetSuffix(genericKlass)));
            }
            closeBracket(indentLine(writer, indent + 2));
        } else {
            //The type is an object, we will use the deserializer of the appropriate type
            writeLine(indentLine(writer, indent + 2), String.format("if (%s.id() == " +
                    "JsonTokenId.ID_START_OBJECT) {", itemVariableName));

            if (genericKlass.isInterface()) {
                //The type is an interface, we must getValue the $type in order to determine the
                // concrete type
                String deserializationInstruction = createVariableName(klass) + ".get" +
                        toPascalCase(f.getName()) + "()." + insertionStatement + genericKlass.getSimpleName() +
                        ".class.cast" + "(genericSerializer.deserialize(parser, concreteClass)));";
                writeInterfaceFieldDeserialization(writer, indent + 3, genericKlass,
                        deserializationInstruction, true);
            } else {
                writeLine(indentLine(writer, indent + 3), String.format("%s.get%s().%s" +
                                "genericSerializer.deserialize(parser, %s.class));",
                        createVariableName(klass), toPascalCase(f.getName()), insertionStatement,
                        genericKlass.getSimpleName()));
            }
            closeBracket(indentLine(writer, indent + 2));
        }
        writeLine(indentLine(writer, indent + 2), String.format("%s = advanceParser(parser);",
                itemVariableName));
        writeLine(indentLine(writer, indent + 1), String.format("} while (!parser.isClosed() && %1$s " +
                "!= null && (%1$s.id() == JsonTokenId.ID_START_OBJECT || %1$s.id() == JsonTokenId" +
                ".ID_FIELD_NAME));", itemVariableName));
    }

    private static boolean isASimpleType(final Class<?> klass) {
        return Integer.class.equals(klass) || Long.class.equals(klass) ||String.class
                .equals(klass) || Date.class.equals(klass) || klass.isPrimitive() ||
                Float.class.equals(klass) || Double.class.equals(klass) || Short.class.equals
                (klass) || Boolean.class.equals(klass);
    }

    private static void writeInterfaceFieldDeserialization(FileWriter writer, int i, Class<?>
            klass, String deserializationInstruction, @SuppressWarnings("unused") boolean inArray)
            throws IOException {
        writeLine(indentLine(writer, i), "JsonToken typeToken = advanceParser(parser);");
        writeLine(indentLine(writer, i), "if (typeToken != null && typeToken.id()" +
                " != JsonTokenId.ID_NULL) {");
        writeLine(indentLine(writer, i + 1), "String typeTokenName = parser" +
                ".getCurrentName();");
        writeLine(indentLine(writer, i + 1), String.format("if (\"%s\".equals" +
                "(typeTokenName)) {", sDtoInterfaceTypeJsonFieldName));
        writeLine(indentLine(writer, i + 2), "try {");
        writeLine(indentLine(writer, i + 3), "JsonToken typeTokenValue = advanceParser(parser);");
        writeLine(indentLine(writer, i + 3), "if (typeTokenValue != null && typeTokenValue.id() " +
                "== JsonTokenId.ID_STRING) {");
        writeLine(indentLine(writer, i + 4), "Class<?> concreteClass = Class.forName(parser" +
                ".getValueAsString().split(\",\")[0]);");
//        writeLine(indentLine(writer, i + 4), String.format("setParsingContext(parser, %s);", inArray
//                ? "concreteClass.getSimpleName()  + \"_Array\"" : "currentName"));
        writeLine(indentLine(writer, i + 4), deserializationInstruction);
        writeLine(indentLine(writer, i + 3), "} else");
        writeLine(indentLine(writer, i + 4), String.format("logInterfaceConcreteTypeNotDefined(%s" +
                ".class);", klass.getSimpleName()));
        writeLine(indentLine(writer, i + 2), "} catch (ClassNotFoundException e) {");
        writeLine(indentLine(writer, i + 3), "logInterfaceConcreteTypeNotFound(parser.getValueAsString());");
        writeLine(indentLine(writer, i + 2), "}");
        writeLine(indentLine(writer, i + 1), "} else {");
        writeLine(indentLine(writer, i + 2), String.format("throw new " +
                "SerializationException(\"Missing concrete type " +
                "definition for interface %s \", this.getClass());", klass
                .getSimpleName()));
        closeBracket(indentLine(writer, i + 1));
        writeLine(indentLine(writer, i), "} else {");
        writeLine(indentLine(writer, i + 1), String.format("throw new " +
                "SerializationException(\"Missing concrete type " +
                "definition for interface %s \", this.getClass());", klass
                .getSimpleName()));
        closeBracket(indentLine(writer, i));
    }

    private static void writeFieldDeserialization(FileWriter writer, Field f, int
            i, Class<?> klass, boolean addElse) throws  IOException {
        boolean isDate = f.getType().equals(Date.class);
        writeLine(indentLine(writer, i), String.format("%sif (\"%s\".equals(%s)) {",
                addElse ? "} else " : "", f.getName(), "currentName"));
        writeLine(indentLine(writer, i + 1), "fieldToken = advanceParser(parser);");
        writeLine(indentLine(writer, i + 1), "if (fieldToken != null && " +
                "fieldToken.id() != JsonTokenId.ID_NULL) {");
        if (isDate) {
            writeLine(indentLine(writer, i + 2), String.format("String stringValue = parser.get%s" +
                    "();", createParserGetSuffix(f.getType())));
            writeLine(indentLine(writer, i + 2), "try {");
            writeLine(indentLine(writer, i + 3), String.format("%s.set%s" +
                            "(getSimpleDateFormat().parse(stringValue));", createVariableName(klass),
                    toPascalCase(f.getName())));
            writeLine(indentLine(writer, i + 2), "} catch (ParseException e){");
            writeLine(indentLine(writer, i + 3), "logFailedToDeserializeDate(stringValue);");
            closeBracket(indentLine(writer, i + 2));
        } else {
            //Is our DTO an instance of MappedDto
            writeLine(indentLine(writer, i + 2), String.format("%s.set%s" +
                                "(parser.get%s());", createVariableName(klass), toPascalCase(f.getName())
                        , createParserGetSuffix(f.getType())));
        }
        closeBracket(indentLine(writer, i + 1));
    }

    private static String createParserGetSuffix(Class<?> type) {
        if (String.class.equals(type) || Date.class.equals(type)) {
            return "Text";
        } else if (Integer.class.equals(type) || Short.class.equals(type)) {
            return "IntValue";
        } else if (Long.class.equals(type)) {
            return "LongValue";
        } else if (Double.class.equals(type) || Float.class.equals(type)) {
            return "FloatValue";
        } else if (Boolean.class.equals(type)) {
            return "BooleanValue";
        } else {
            return String.format("%sValue", toPascalCase(type.getSimpleName()));
        }
    }

    private static void writeObjectFieldDeserialization(FileWriter writer, Field f, int
            i, Class<?> klass, boolean addElse) throws IOException {
        String typeName = f.getType().getSimpleName();
        writeLine(indentLine(writer, i), String.format("%sif (\"%s\".equals(%s)) {",
                addElse ? "} else " : "", f.getName(), "currentName"));
        if (f.getType().isInterface()) {
            writeInterfaceFieldDeserialization(writer, i + 1, klass, createVariableName(klass) +
                    ".set" + toPascalCase(f.getName()) +
                    "(" + typeName + ".class.cast(genericSerializer.deserialize" +
                    "(parser, concreteClass)));", false);
        } else {
            writeLine(indentLine(writer, i + 1), String.format(createVariableName(klass) + ".set" + toPascalCase(f.getName()) +
                    "(genericSerializer.deserialize(parser, %s.class));", typeName));
        }
    }

    private static String toPascalCase(String name) {
        if (name == null) {
            return null;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    private static String createTypeSimpleName(Class<?> klass) {
        return klass.getSimpleName();
    }

    private static String createVariableName(Class<?> klass) {
        final String typeSimpleName = createTypeSimpleName(klass);
        final String variableName = typeSimpleName.substring(0, 1).toLowerCase() + typeSimpleName
                .substring(1);
        if (ArrayUtils.contains(LANGUAGE_RESERVED_WORDS, variableName)) {
            return StringHelpers.formatWithDefaultLocale("local%s", typeSimpleName);
        } else {
            return variableName;
        }
    }

    private static FileWriter closeBracket(final FileWriter writer) throws IOException {
        writer.write("}\r\n");
        return writer;
    }

    private static FileWriter indentLine(final FileWriter writer, int i) throws IOException {
        for(int count = 0; count < i; ++count) {
            writer.write(generateTab());
        }
        return writer;
    }

    private static String createGenericClassDeclarationStatement() {
        return "public class " + sGenericStaticSerializerClassName + " implements " +
                sJsonParserGenericSerializerInterface + "{\r\n";
    }

    private static String createClassDeclarationStatement(Class<?> klass) {
        return "public class " + createSerializerClassName(klass) + " extends " +
                createBaseStaticDeserializerClassName(klass) + " {\r\n";
    }

    private static FileWriter jumpLine(FileWriter writer) throws IOException {
        writer.write("\r\n");
        return writer;
    }

    private static String createImportStatement(String objectPackageName) {
        return String.format("import %s;", objectPackageName);
    }

    private static FileWriter writeLine(FileWriter writer, String line) throws IOException {
        writer.write(line + "\r\n");
        return writer;
    }

    private static String getSerializersModuleName() {
        return sGenerateUsingTestPackage ?
                Generator.sSerializersModuleNameUsingTestPackage :
                Generator.sSerializersModuleName;
    }

    private static String getSourceFolder() {
        return sGenerateUsingTestPackage ? "test" : "main";
    }

    private static String createGenericSerializerFileName() {

        return String.format("%2$s%1$s%3$s%1$ssrc%1$s%6$s%1$sjava%1$s%4$s%1$s%5$s.java",
                File.separator,
                Generator.sCurrent,
                getSerializersModuleName(),
                (sAppNameInPackageName + sGenericSerializersPackageNameSuffix).replace(".",
                        File.separator),
                sGenericStaticSerializerClassName,
                getSourceFolder());
    }

    private static String createSerializerFileName(final Class<?> klass) {

        return String.format("%2$s%1$s%3$s%1$ssrc%1$s%6$s%1$sjava%1$s%4$s%1$s%5$s.java",
                File.separator,
                Generator.sCurrent,
                getSerializersModuleName(),
                (sAppNameInPackageName + sTypedSerializersPackageNameSuffix).replace(".",
                        File.separator),
                createSerializerClassName(klass),
                getSourceFolder());
    }

    private static String createSerializerClassName(final Class<?> klass) {
        return String.format("%sStaticDeserializer", createTypeSimpleName(klass));
    }

    private static String createBaseStaticDeserializerClassName(final Class<?> klass) {
        return String.format("%s<%s>", sStaticJsonParserDeserializerBaseClassName, createTypeSimpleName(klass));
    }

    private static String generateTab() {
        return "    ";
    }

    private static File createFile(String filePath, String klassName) {
        return createFile(filePath, klassName, false);
    }

    private static File createFile(String filePath, String klassName, boolean deleteIfExists) {
        final File file = new File(filePath);

        if (file.exists() && deleteIfExists) {
            if (!file.delete()) {
                throw new RuntimeException(String.format("Failed to delete the " +
                        "deserialization file for type '%1$s'", klassName));
            }
        }
        else {
            try {
                boolean result = file.createNewFile();
                if (!result) {
                    throw new RuntimeException(String.format("Failed to create the " +
                            "deserialization file for type '%1$s'", klassName));
                }
                return file;
            } catch (final IOException exception) {
                throw new RuntimeException(String.format(
                        "Failed to create the java file with the path '%1$s'",
                        filePath));
            }
        }
        return file;
    }

    private static List<Field> getFieldsUpTo(Class<?> startClass,
                                                Class<?> exclusiveParent) {

        List<Field> currentClassFields = Lists.newArrayList(startClass.getDeclaredFields());
        Class<?> parentClass = startClass.getSuperclass();

        if (parentClass != null &&
                (exclusiveParent == null || !(parentClass.equals(exclusiveParent)))) {
            List<Field> parentClassFields = getFieldsUpTo(parentClass, exclusiveParent);
            currentClassFields.addAll(parentClassFields);
        }

        return currentClassFields;
    }
}