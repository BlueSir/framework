package com.sohu.smc.core.json;

import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.*;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.type.TypeReference;

import java.io.*;
import java.lang.reflect.Type;

/**
 * A basic class for JSON parsing and generating.
 *
 * <p>By default, {@link com.sohu.smc.core.json.Json} is configured to:</p>
 * <ul>
 *     <li>Automatically close JSON content, if possible.</li>
 *     <li>Automatically close input and output streams.</li>
 *     <li>Quote field names.</li>
 *     <li>Allow both C-style line and block comments.</li>
 *     <li>Not fail when encountering unknown properties.</li>
 *     <li>Read and write enums using {@code toString()}.</li>
 *     <li>Use {@code snake_case} for property names when encoding and decoding
 *         classes annotated with {@link com.sohu.smc.core.json.JsonSnakeCase}.</li>
 * </ul>
 */
public class Json {
    protected JsonFactory factory;
    protected ObjectMapper mapper;
    protected TypeFactory typeFactory;

    /**
     * Creates a new {@link com.sohu.smc.core.json.Json} instance.
     */
    public Json() {
        this.factory = new MappingJsonFactory();
        factory.enable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT);
        factory.enable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
        factory.enable(JsonGenerator.Feature.QUOTE_FIELD_NAMES);
        factory.enable(JsonParser.Feature.ALLOW_COMMENTS);
        factory.enable(JsonParser.Feature.AUTO_CLOSE_SOURCE);

        this.mapper = (ObjectMapper) factory.getCodec();
        mapper.setPropertyNamingStrategy(AnnotationSensitivePropertyNamingStrategy.INSTANCE);
        mapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(SerializationConfig.Feature.WRITE_ENUMS_USING_TO_STRING);
        mapper.disable(DeserializationConfig.Feature.READ_ENUMS_USING_TO_STRING);
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new Log4jModule());

        this.typeFactory = mapper.getTypeFactory();
    }

    /**
     * Registers a module that can extend functionality provided by this class; for example, by
     * adding providers for custom serializers and deserializers.
     *
     * @param module Module to register
     * @see org.codehaus.jackson.map.ObjectMapper#registerModule(org.codehaus.jackson.map.Module)
     */
    public void registerModule(Module module) {
        mapper.registerModule(module);
    }

    /**
     * Returns true if the given {@link org.codehaus.jackson.map.DeserializationConfig.Feature} is enabled.
     *
     * @param feature    a given feature
     * @return {@code true} if {@code feature} is enabled
     * @see org.codehaus.jackson.map.ObjectMapper#isEnabled(org.codehaus.jackson.map.DeserializationConfig.Feature)
     */
    public boolean isEnabled(DeserializationConfig.Feature feature) {
        return mapper.isEnabled(feature);
    }

    /**
     * Enables the given {@link org.codehaus.jackson.map.DeserializationConfig.Feature}s.
     *
     * @param features    a set of features to enable
     * @see org.codehaus.jackson.map.ObjectMapper#enable(org.codehaus.jackson.map.DeserializationConfig.Feature...)
     */
    public void enable(DeserializationConfig.Feature... features) {
        mapper.enable(features);
    }

    /**
     * Disables the given {@link org.codehaus.jackson.map.DeserializationConfig.Feature}s.
     *
     * @param features    a set of features to disable
     * @see org.codehaus.jackson.map.ObjectMapper#disable(org.codehaus.jackson.map.DeserializationConfig.Feature...)
     */
    public void disable(DeserializationConfig.Feature... features) {
        mapper.disable(features);
    }

    /**
     * Returns true if the given {@link org.codehaus.jackson.map.SerializationConfig.Feature} is enabled.
     *
     * @param feature    a given feature
     * @return {@code true} if {@code feature} is enabled
     * @see org.codehaus.jackson.map.ObjectMapper#isEnabled(org.codehaus.jackson.map.SerializationConfig.Feature)
     */
    public boolean isEnabled(SerializationConfig.Feature feature) {
        return mapper.isEnabled(feature);
    }

    /**
     * Enables the given {@link org.codehaus.jackson.map.SerializationConfig.Feature}s.
     *
     * @param features    a set of features to enable
     * @see org.codehaus.jackson.map.ObjectMapper#enable(org.codehaus.jackson.map.SerializationConfig.Feature...)
     */
    public void enable(SerializationConfig.Feature... features) {
        mapper.enable(features);
    }

    /**
     * Disables the given {@link org.codehaus.jackson.map.SerializationConfig.Feature}s.
     *
     * @param features    a set of features to disable
     * @see org.codehaus.jackson.map.ObjectMapper#disable(org.codehaus.jackson.map.SerializationConfig.Feature...)
     */
    public void disable(SerializationConfig.Feature... features) {
        mapper.disable(features);
    }

    /**
     * Returns true if the given {@link org.codehaus.jackson.JsonGenerator.Feature} is enabled.
     *
     * @param feature    a given feature
     * @return {@code true} if {@code feature} is enabled
     * @see org.codehaus.jackson.map.ObjectMapper#isEnabled(org.codehaus.jackson.JsonGenerator.Feature)
     */
    public boolean isEnabled(JsonGenerator.Feature feature) {
        return mapper.isEnabled(feature);
    }

    /**
     * Enables the given {@link org.codehaus.jackson.JsonGenerator.Feature}s.
     *
     * @param features    a set of features to enable
     * @see org.codehaus.jackson.JsonFactory#enable(org.codehaus.jackson.JsonGenerator.Feature)
     */
    public void enable(JsonGenerator.Feature... features) {
        for (JsonGenerator.Feature feature : features) {
            factory.enable(feature);
        }
    }

    /**
     * Disables the given {@link org.codehaus.jackson.JsonGenerator.Feature}s.
     *
     * @param features    a set of features to disable
     * @see org.codehaus.jackson.JsonFactory#disable(org.codehaus.jackson.JsonGenerator.Feature)
     */
    public void disable(JsonGenerator.Feature... features) {
        for (JsonGenerator.Feature feature : features) {
            factory.disable(feature);
        }
    }

    /**
     * Returns true if the given {@link org.codehaus.jackson.JsonParser.Feature} is enabled.
     *
     * @param feature    a given feature
     * @return {@code true} if {@code feature} is enabled
     * @see org.codehaus.jackson.map.ObjectMapper#isEnabled(org.codehaus.jackson.JsonParser.Feature)
     */
    public boolean isEnabled(JsonParser.Feature feature) {
        return mapper.isEnabled(feature);
    }

    /**
     * Enables the given {@link org.codehaus.jackson.JsonParser.Feature}s.
     *
     * @param features    a set of features to enable
     * @see org.codehaus.jackson.JsonFactory#enable(org.codehaus.jackson.JsonParser.Feature)
     */
    public void enable(JsonParser.Feature... features) {
        for (JsonParser.Feature feature : features) {
            factory.enable(feature);
        }
    }

    /**
     * Disables the given {@link org.codehaus.jackson.JsonParser.Feature}s.
     *
     * @param features    a set of features to disable
     * @see org.codehaus.jackson.JsonFactory#disable(org.codehaus.jackson.JsonParser.Feature)
     */
    public void disable(JsonParser.Feature... features) {
        for (JsonParser.Feature feature : features) {
            factory.disable(feature);
        }
    }

    /**
     * Returns {@code true} if the mapper can find a serializer for instances of given class
     * (potentially serializable), {@code false} otherwise (not serializable).
     *
     * @param type    the type of object to serialize
     * @return {@code true} if instances of {@code type} are potentially serializable
     */
    public boolean canSerialize(Class<?> type) {
        return mapper.canSerialize(type);
    }

    /**
     * Returns {@code true} if the mapper can find a deserializer for instances of given class
     * (potentially deserializable), {@code false} otherwise (not deserializable).
     *
     * @param type    the type of object to deserialize
     * @return {@code true} if instances of {@code type} are potentially deserializable
     */
    public boolean canDeserialize(Class<?> type) {
        return mapper.canDeserialize(constructType(type));
    }

    /**
     * Deserializes the given {@link java.io.File} as an instance of the given type.
     *
     * @param src          a JSON {@link java.io.File}
     * @param valueType    the {@link Class} to deserialize {@code src} as
     * @param <T>          the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readValue(File src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    /**
     * Deserializes the given {@link java.io.File} as an instance of the given type.
     *
     * @param src             a JSON {@link java.io.File}
     * @param valueTypeRef    a {@link org.codehaus.jackson.type.TypeReference} of the type to deserialize {@code src} as
     * @param <T>             the type of {@code valueTypeRef}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readValue(File src, TypeReference<T> valueTypeRef) throws IOException {
        return(T) mapper.readValue(src, valueTypeRef);
    }

    /**
     * Deserializes the given {@link String} as an instance of the given type.
     *
     * @param content      a JSON {@link String}
     * @param valueType    the {@link Class} to deserialize {@code content} as
     * @param <T>          the type of {@code valueType}
     * @return {@code content} as an instance of {@code T}
     * @throws java.io.IOException if there is an error parsing {@code content}
     */
    public <T> T readValue(String content, Class<T> valueType) throws IOException {
        return mapper.readValue(content, valueType);
    }

    /**
     * Deserializes the given {@link String} as an instance of the given type.
     *
     * @param content         a JSON {@link String}
     * @param valueTypeRef    a {@link org.codehaus.jackson.type.TypeReference} of the type to deserialize {@code content} as
     * @param <T>             the type of {@code valueTypeRef}
     * @return {@code content} as an instance of {@code T}
     * @throws java.io.IOException if there is an error parsing {@code content}
     */
    public <T> T readValue(String content, TypeReference<T> valueTypeRef) throws IOException {
        return(T) mapper.readValue(content, valueTypeRef);
    }

    /**
     * Deserializes the given {@link java.io.Reader} as an instance of the given type.
     *
     * @param src          a JSON {@link java.io.Reader}
     * @param valueType    the {@link Class} to deserialize {@code src} as
     * @param <T>          the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readValue(Reader src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    /**
     * Deserializes the given {@link java.io.Reader} as an instance of the given type.
     *
     * @param src             a JSON {@link java.io.Reader}
     * @param valueTypeRef    a {@link org.codehaus.jackson.type.TypeReference} of the type to deserialize {@code src} as
     * @param <T>             the type of {@code valueTypeRef}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readValue(Reader src, TypeReference<T> valueTypeRef) throws IOException {
        return(T) mapper.readValue(src, valueTypeRef);
    }

    /**
     * Deserializes the given {@link java.io.InputStream} as an instance of the given type.
     *
     * @param src          a JSON {@link java.io.InputStream}
     * @param valueType    the {@link Class} to deserialize {@code src} as
     * @param <T>          the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readValue(InputStream src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    /**
     * Deserializes the given {@link java.io.InputStream} as an instance of the given type.
     *
     * @param src             a JSON {@link java.io.InputStream}
     * @param valueTypeRef    a {@link org.codehaus.jackson.type.TypeReference} of the type to deserialize {@code src} as
     * @param <T>             the type of {@code valueTypeRef}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readValue(InputStream src, TypeReference<T> valueTypeRef) throws IOException {
        return(T) mapper.readValue(src, valueTypeRef);
    }

    /**
     * Deserializes the given {@link java.io.InputStream} as an instance of the given type.
     *
     * @param src          a JSON {@link java.io.InputStream}
     * @param valueType    the {@link java.lang.reflect.Type} to deserialize {@code src} as
     * @param <T>          the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readValue(InputStream src, Type valueType) throws IOException {
        return(T) mapper.readValue(src, constructType(valueType));
    }

    /**
     * Deserializes the given byte array as an instance of the given type.
     *
     * @param src          a JSON byte array
     * @param valueType    the {@link Class} to deserialize {@code src} as
     * @param <T>          the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error parsing {@code src}
     */
    public <T> T readValue(byte[] src, Class<T> valueType) throws IOException {
        return mapper.readValue(src, valueType);
    }

    /**
     * Deserializes a subset of the given byte array as an instance of the given type.
     *
     * @param src          a JSON byte array
     * @param offset       the offset into {@code src} of the subset
     * @param len          the length of the subset of {@code src}
     * @param valueType    the {@link Class} to deserialize {@code src} as
     * @param <T>          the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error parsing {@code src}
     */
    public <T> T readValue(byte[] src, int offset, int len, Class<T> valueType) throws IOException {
        return mapper.readValue(src, offset, len, valueType);
    }

    /**
     * Deserializes the given byte array as an instance of the given type.
     *
     * @param src             a JSON byte array
     * @param valueTypeRef    a {@link org.codehaus.jackson.type.TypeReference} of the type to deserialize {@code src} as
     * @param <T>             the type of {@code valueTypeRef}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error parsing {@code src}
     */
    public <T> T readValue(byte[] src, TypeReference<T> valueTypeRef) throws IOException {
        return(T) mapper.readValue(src, valueTypeRef);
    }

    /**
     * Deserializes a subset of the given byte array as an instance of the given type.
     *
     * @param src             a JSON byte array
     * @param offset       the offset into {@code src} of the subset
     * @param len          the length of the subset of {@code src}
     * @param valueTypeRef    a {@link org.codehaus.jackson.type.TypeReference} of the type to deserialize {@code src} as
     * @param <T>             the type of {@code valueTypeRef}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error parsing {@code src}
     */
    public <T> T readValue(byte[] src, int offset, int len, TypeReference<T> valueTypeRef) throws IOException {
        return(T) mapper.readValue(src, offset, len, valueTypeRef);
    }

    /**
     * Deserializes the given {@link org.codehaus.jackson.JsonNode} as an instance of the given type.
     *
     * @param root         a {@link org.codehaus.jackson.JsonNode}
     * @param valueType    the {@link Class} to deserialize {@code src} as
     * @param <T>          the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error mapping {@code src} to {@code T}
     */
    public <T> T readValue(JsonNode root, Class<T> valueType) throws IOException {
        return mapper.readValue(root, valueType);
    }

    /**
     * Deserializes the given {@link org.codehaus.jackson.JsonNode} as an instance of the given type.
     *
     * @param root            a {@link org.codehaus.jackson.JsonNode}
     * @param valueTypeRef    a {@link org.codehaus.jackson.type.TypeReference} of the type to deserialize {@code src} as
     * @param <T>             the type of {@code valueTypeRef}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error mapping {@code src} to {@code T}
     */
    public <T> T readValue(JsonNode root, TypeReference<T> valueTypeRef) throws IOException {
        return(T) mapper.readValue(root, valueTypeRef);
    }

    /**
     * Serializes the given object to the given {@link java.io.File}.
     *
     * @param output the {@link java.io.File} to which the JSON will be written
     * @param value  the object to serialize into {@code output}
     * @throws java.io.IOException if there is an error writing to {@code output} or serializing {@code
     *                     value}
     */
    public void writeValue(File output, Object value) throws IOException {
        mapper.writeValue(output, value);
    }

    /**
     * Serializes the given object to the given {@link java.io.OutputStream}.
     *
     * @param output the {@link java.io.OutputStream} to which the JSON will be written
     * @param value  the object to serialize into {@code output}
     * @throws java.io.IOException if there is an error writing to {@code output} or serializing {@code
     *                     value}
     */
    public void writeValue(OutputStream output, Object value) throws IOException {
        mapper.writeValue(output, value);
    }

    /**
     * Serializes the given object to the given {@link java.io.Writer}.
     *
     * @param output the {@link java.io.Writer} to which the JSON will be written
     * @param value  the object to serialize into {@code output}
     * @throws java.io.IOException if there is an error writing to {@code output} or serializing {@code
     *                     value}
     */
    public void writeValue(Writer output, Object value) throws IOException {
        mapper.writeValue(output, value);
    }

    /**
     * Returns the given object as a JSON string.
     *
     * @param value    an object
     * @return {@code value} as a JSON string
     * @throws IllegalArgumentException if there is an error encoding {@code value}
     */
    public String writeValueAsString(Object value) throws IllegalArgumentException {
        try {
            return mapper.writeValueAsString(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns the given object as a JSON byte array.
     *
     * @param value    an object
     * @return {@code value} as a JSON byte array
     * @throws IllegalArgumentException if there is an error encoding {@code value}
     */
    public byte[] writeValueAsBytes(Object value) throws IllegalArgumentException {
        try {
            return mapper.writeValueAsBytes(value);
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }

    /**
     * Returns the given object as a {@link org.codehaus.jackson.JsonNode}.
     *
     * @param value    an object
     * @return {@code value} as a {@link org.codehaus.jackson.JsonNode}
     * @throws IllegalArgumentException if there is an error encoding {@code value}
     */
    public JsonNode writeValueAsTree(Object value) throws IllegalArgumentException {
        return mapper.valueToTree(value);
    }

    /**
     * Deserializes the given YAML {@link java.io.File} as an instance of the given type.
     * <p><b>N.B.:</b> All tags, comments, and non-JSON elements of the YAML file will be elided.</p>
     *
     * @param src          a YAML {@link java.io.File}
     * @param valueType    the {@link Class} to deserialize {@code src} as
     * @param <T>          the type of {@code valueType}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readYamlValue(File src, Class<T> valueType) throws IOException {
        final YamlConverter converter = new YamlConverter(this, factory);
        return mapper.readValue(converter.convert(src), valueType);
    }

    public <T> T readYamlValue(InputStream inputStream, Class<T> valueType) throws IOException {
        final YamlConverter converter = new YamlConverter(this, factory);
        return mapper.readValue(converter.convert(inputStream), valueType);
    }

    /**
     * Deserializes the given YAML {@link java.io.File} as an instance of the given type.
     * <p><b>N.B.:</b> All tags, comments, and non-JSON elements of the YAML file will be elided.</p>
     *
     * @param src             a YAML {@link java.io.File}
     * @param valueTypeRef    a {@link org.codehaus.jackson.type.TypeReference} of the type to deserialize {@code src} as
     * @param <T>             the type of {@code valueTypeRef}
     * @return the contents of {@code src} as an instance of {@code T}
     * @throws java.io.IOException if there is an error reading from {@code src} or parsing its contents
     */
    public <T> T readYamlValue(File src, TypeReference<T> valueTypeRef) throws IOException {
        final YamlConverter converter = new YamlConverter(this, factory);
        return(T) mapper.readValue(converter.convert(src), valueTypeRef);
    }

    private JavaType constructType(Type type) {
        return typeFactory.constructType(type);
    }
}
