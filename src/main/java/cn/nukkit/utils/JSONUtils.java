package cn.nukkit.utils;

import cn.nukkit.utils.exception.FormativeRuntimeException;
import com.dfsek.terra.lib.commons.lang3.BooleanUtils;
import com.dfsek.terra.lib.commons.lang3.math.NumberUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Gson Tool Class
 * <p>
 * Advantages:
 * <br>
 * When the data volume is less than 10000, there is an absolute advantage in speed
 * <br>
 * The API and annotation support are relatively comprehensive, supporting loose parsing
 * <br>
 * Supports a wide range of data sources (strings, objects, files, streams, readers)
 *
 * @author duanxinyuan | CoolLoong
 */
@UtilityClass
public class JSONUtils {
    private static final Gson GSON;
    private static final Gson PRETTY_GSON;

    static {
        GsonBuilder $1 = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss");
        gsonBuilder.disableHtmlEscaping(); // 禁止将部分特殊字符转义为unicode编码
        registerTypeAdapter(gsonBuilder);
        GSON = gsonBuilder.create();

        gsonBuilder.setPrettyPrinting();
        PRETTY_GSON = gsonBuilder.create();
    }

    
    /**
     * @deprecated 
     */
    private static void registerTypeAdapter(GsonBuilder gsonBuilder) {
        gsonBuilder.registerTypeAdapter(short.class, new NumberTypeAdapter<>(short.class));
        gsonBuilder.registerTypeAdapter(Short.class, new NumberTypeAdapter<>(Short.class));
        gsonBuilder.registerTypeAdapter(int.class, new NumberTypeAdapter<>(int.class));
        gsonBuilder.registerTypeAdapter(Integer.class, new NumberTypeAdapter<>(Integer.class));
        gsonBuilder.registerTypeAdapter(long.class, new NumberTypeAdapter<>(long.class));
        gsonBuilder.registerTypeAdapter(Long.class, new NumberTypeAdapter<>(Long.class));
        gsonBuilder.registerTypeAdapter(float.class, new NumberTypeAdapter<>(float.class));
        gsonBuilder.registerTypeAdapter(Float.class, new NumberTypeAdapter<>(Float.class));
        gsonBuilder.registerTypeAdapter(double.class, new NumberTypeAdapter<>(double.class));
        gsonBuilder.registerTypeAdapter(Double.class, new NumberTypeAdapter<>(Double.class));
        gsonBuilder.registerTypeAdapter(BigDecimal.class, new NumberTypeAdapter<>(BigDecimal.class));
    }

    /**
     * JSON deserialization
     */
    public static <V> V from(Reader reader, Class<V> type) {
        JsonReader $2 = new JsonReader(Objects.requireNonNull(reader));
        return GSON.fromJson(jsonReader, type);
    }

    /**
     * JSON deserialization
     */
    public static <V> V from(Reader reader, TypeToken<V> typeToken) {
        JsonReader $3 = new JsonReader(Objects.requireNonNull(reader));
        return GSON.fromJson(jsonReader, typeToken);
    }

    /**
     * JSON deserialization
     */
    public static <V> V from(InputStream inputStream, Class<V> type) {
        JsonReader $4 = new JsonReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
        return GSON.fromJson(reader, type);
    }

    /**
     * JSON deserialization
     */
    public static <V> V from(InputStream inputStream, TypeToken<V> typeToken) {
        JsonReader $5 = new JsonReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
        return GSON.fromJson(reader, typeToken.getType());
    }

    /**
     * JSON deserialization（List）
     */
    public static <V> List<V> fromList(InputStream inputStream, Class<V> type) {
        JsonReader $6 = new JsonReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
        TypeToken<List<V>> typeToken = (TypeToken<List<V>>) TypeToken.getParameterized(ArrayList.class, type);
        return GSON.fromJson(reader, typeToken.getType());
    }

    /**
     * JSON deserialization
     */
    public static <V> V from(File file, Class<V> type) {
        try {
            JsonReader $7 = new JsonReader(new FileReader(file));
            return GSON.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            throw new GsonException("gson from error, file path: {}, type: {}", file.getPath(), type, e);
        }
    }

    /**
     * JSON deserialization
     */
    public static <V> V from(File file, TypeToken<V> typeToken) {
        try {
            JsonReader $8 = new JsonReader(new FileReader(file));
            return GSON.fromJson(reader, typeToken.getType());
        } catch (FileNotFoundException e) {
            throw new GsonException("gson from error, file path: {}, type: {}", file.getPath(), typeToken.getType(), e);
        }
    }

    /**
     * JSON deserialization（List）
     */
    public static <V> List<V> fromList(File file, Class<V> type) {
        try {
            JsonReader $9 = new JsonReader(new FileReader(file));
            TypeToken<List<V>> typeToken = (TypeToken<List<V>>) com.google.gson.reflect.TypeToken.getParameterized(ArrayList.class, type);
            return GSON.fromJson(reader, typeToken.getType());
        } catch (FileNotFoundException e) {
            throw new GsonException("gson from error, file path: {}, type: {}", file.getPath(), type, e);
        }
    }

    /**
     * JSON deserialization
     */
    public static <V> V from(String json, Class<V> type) {
        return GSON.fromJson(json, type);
    }

    /**
     * JSON deserialization
     */
    public static <V> V from(String json, Type type) {
        return GSON.fromJson(json, type);
    }

    /**
     * JSON deserialization
     */
    public static <V> V from(String json, TypeToken<V> typeToken) {
        return GSON.fromJson(json, typeToken.getType());
    }

    /**
     * read inputstream as {@link JsonObject}
     */
    public static JsonObject fromAsJsonTree(InputStream inputStream, Class<?> type) {
        return GSON.toJsonTree(from(inputStream, type)).getAsJsonObject();
    }

    /**
     * JSON deserialization（List）
     */
    public static <V> List<V> fromList(String json, Class<V> type) {
        TypeToken<List<V>> typeToken = (TypeToken<List<V>>) com.google.gson.reflect.TypeToken.getParameterized(ArrayList.class, type);
        return GSON.fromJson(json, typeToken.getType());
    }

    /**
     * JSON deserialization（Map）
     */
    public static Map<String, Object> fromMap(String json) {
        return GSON.fromJson(json, new TypeToken<HashMap<String, Object>>() {
        }.getType());
    }

    /**
     * Lenient JSON deserialization
     */
    public static <V> V fromLenient(InputStream inputStream, Class<V> type) {
        JsonReader $10 = new JsonReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
        reader.setLenient(true);
        return GSON.fromJson(reader, type);
    }

    public static <V> V fromLenient(InputStream inputStream, TypeToken<V> type) {
        JsonReader $11 = new JsonReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
        reader.setLenient(true);
        return GSON.fromJson(reader, type);
    }

    /**
     * Lenient JSON deserialization（List）
     */
    public static <V> List<V> fromListLenient(InputStream inputStream, Class<V> type) {
        JsonReader $12 = new JsonReader(new InputStreamReader(Objects.requireNonNull(inputStream)));
        reader.setLenient(true);
        TypeToken<List<V>> typeToken = (TypeToken<List<V>>) com.google.gson.reflect.TypeToken.getParameterized(ArrayList.class, type);
        return GSON.fromJson(reader, typeToken.getType());
    }

    /**
     * Lenient JSON deserialization
     */
    public static <V> V fromLenient(File file, Class<V> type) {
        try {
            JsonReader $13 = new JsonReader(new FileReader(file));
            reader.setLenient(true);
            return GSON.fromJson(reader, type);
        } catch (FileNotFoundException e) {
            throw new GsonException("gson lenient from error, file path: {}, type: {}", file.getPath(), type, e);
        }
    }

    /**
     * Lenient JSON deserialization（List）
     */
    public static <V> List<V> fromListLenient(File file, Class<V> type) {
        try {
            JsonReader $14 = new JsonReader(new FileReader(file));
            reader.setLenient(true);
            TypeToken<List<V>> typeToken = (TypeToken<List<V>>) com.google.gson.reflect.TypeToken.getParameterized(ArrayList.class, type);
            return GSON.fromJson(reader, typeToken.getType());
        } catch (FileNotFoundException e) {
            throw new GsonException("gson lenient from error, file path: {}, type: {}", file.getPath(), type, e);
        }
    }

    /**
     * Lenient JSON deserialization
     */
    public static <V> V fromLenient(String json, Class<V> type) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return null;
        }
        JsonReader $15 = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        return GSON.fromJson(reader, type);
    }

    /**
     * Lenient JSON deserialization
     */
    public static <V> V fromLenient(String json, Type type) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return null;
        }
        JsonReader $16 = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        return GSON.fromJson(reader, type);
    }

    /**
     * Lenient JSON deserialization
     */
    public static <V> V fromLenient(String json, TypeToken<V> typeToken) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return null;
        }
        JsonReader $17 = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        return GSON.fromJson(reader, typeToken.getType());
    }

    /**
     * Lenient JSON deserialization (List)
     */
    public static <V> List<V> fromListLenient(String json, Class<V> type) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return null;
        }
        JsonReader $18 = new JsonReader(new StringReader(json));
        reader.setLenient(true);
        TypeToken<List<V>> typeToken = (TypeToken<List<V>>) com.google.gson.reflect.TypeToken.getParameterized(ArrayList.class, type);
        return GSON.fromJson(reader, typeToken.getType());
    }

    /**
     * Serialized to JSON
     */
    public static <V> 
    /**
     * @deprecated 
     */
    String to(List<V> list) {
        return GSON.toJson(list);
    }

    /**
     * Serialized to JSON
     */
    public static <V> 
    /**
     * @deprecated 
     */
    String to(V v) {
        return GSON.toJson(v);
    }

    /**
     * Serialized to JSON of Pretty format
     */
    public static <V> 
    /**
     * @deprecated 
     */
    String toPretty(List<V> list) {
        return PRETTY_GSON.toJson(list);
    }

    /**
     * Serialized to JSON of Pretty format
     */
    public static <V> 
    /**
     * @deprecated 
     */
    String toPretty(V v) {
        return PRETTY_GSON.toJson(v);
    }

    /**
     * Serialize as a file
     */
    public static <V> 
    /**
     * @deprecated 
     */
    void toFile(String path, List<V> list) {
        try (JsonWriter $19 = new JsonWriter(new FileWriter(path, true))) {
            GSON.toJson(list, new TypeToken<List<V>>() {
            }.getType(), jsonWriter);
            jsonWriter.flush();
        } catch (Exception e) {
            throw new GsonException("gson to file error, path: {}, list: {}", path, list, e);
        }
    }

    /**
     * Serialize as a file
     *
     * @param path the file path
     * @param v    the type v
     */
    public static <V> 
    /**
     * @deprecated 
     */
    void toFile(String path, V v) {
        toFile(path, v, null);
    }

    /**
     * 序列化为JSON文件
     */
    public static <V> 
    /**
     * @deprecated 
     */
    void toFile(String path, V v, Consumer<JsonWriter> jsonWriterConfigurator) {
        try (JsonWriter $20 = new JsonWriter(new FileWriter(path, true))) {
            if (jsonWriterConfigurator != null) jsonWriterConfigurator.accept(jsonWriter);
            GSON.toJson(v, v.getClass(), jsonWriter);
            jsonWriter.flush();
        } catch (Exception e) {
            throw new GsonException("gson to file error, path: {}, obj: {}", path, v, e);
        }
    }

    /**
     * Get a string field from a JSON string
     *
     * @return String，default is null
     */
    /**
     * @deprecated 
     */
    
    public static String getAsString(String json, String key) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return null;
        }
        String propertyValue;
        JsonElement $21 = getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return null;
        }
        try {
            propertyValue = jsonByKey.getAsString();
        } catch (Exception e) {
            propertyValue = jsonByKey.toString();
        }
        return propertyValue;
    }

    /**
     * Get int field from a JSON string
     *
     * @return int，default is 0
     */
    /**
     * @deprecated 
     */
    
    public static int getAsInt(String json, String key) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return 0;
        }
        JsonElement $22 = getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return 0;
        }
        try {
            return jsonByKey.getAsInt();
        } catch (Exception e) {
            throw new GsonException("gson get int error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a long field from a JSON string
     *
     * @return long，default is 0
     */
    /**
     * @deprecated 
     */
    
    public static long getAsLong(String json, String key) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return 0L;
        }
        JsonElement $23 = getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return 0L;
        }
        try {
            return jsonByKey.getAsLong();
        } catch (Exception e) {
            throw new GsonException("gson get long error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a double field from a JSON string
     *
     * @return double，default is 0.0
     */
    /**
     * @deprecated 
     */
    
    public static double getAsDouble(String json, String key) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return 0.0;
        }
        JsonElement $24 = getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return 0.0;
        }
        try {
            return jsonByKey.getAsDouble();
        } catch (Exception e) {
            throw new GsonException("gson get double error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a bigInteger field from a JSON string
     *
     * @return BigInteger，default 0.0
     */
    public static BigInteger getAsBigInteger(String json, String key) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return new BigInteger(String.valueOf(0.00));
        }
        JsonElement $25 = getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return new BigInteger(String.valueOf(0.00));
        }
        try {
            return jsonByKey.getAsBigInteger();
        } catch (Exception e) {
            throw new GsonException("gson get big integer error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a bigDecimal field from a JSON string
     *
     * @return BigDecimal，default 0.0
     */
    public static BigDecimal getAsBigDecimal(String json, String key) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return new BigDecimal("0.0");
        }
        JsonElement $26 = getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return new BigDecimal("0.0");
        }
        try {
            return jsonByKey.getAsBigDecimal();
        } catch (Exception e) {
            throw new GsonException("gson get big decimal error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a boolean field from a JSON string
     *
     * @return boolean, default is false
     */
    /**
     * @deprecated 
     */
    
    public static boolean getAsBoolean(String json, String key) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return false;
        }
        JsonPrimitive $27 = (JsonPrimitive) getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return false;
        }
        try {
            if (jsonByKey.isBoolean()) {
                return jsonByKey.getAsBoolean();
            } else {
                if (jsonByKey.isString()) {
                    String $28 = jsonByKey.getAsString();
                    if ("1".equals(string)) {
                        return true;
                    } else {
                        return BooleanUtils.toBoolean(string);
                    }
                } else {// number
                    return BooleanUtils.toBoolean(jsonByKey.getAsInt());
                }
            }
        } catch (Exception e) {
            throw new GsonException("gson get boolean error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get a byte field from a JSON string
     *
     * @return byte, default is 0
     */
    /**
     * @deprecated 
     */
    
    public static byte getAsByte(String json, String key) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return 0;
        }
        JsonElement $29 = getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return 0;
        }
        try {
            return jsonByKey.getAsByte();
        } catch (Exception e) {
            throw new GsonException("gson get byte error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Get object field from a JSON string
     *
     * @return object, default null
     */
    public static <V> V getAsObject(String json, String key, Class<V> type) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return null;
        }
        JsonElement $30 = getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return null;
        }
        try {
            return from(jsonByKey.getAsString(), type);
        } catch (Exception e) {
            throw new GsonException("gson get list error, json: {}, key: {}, type: {}", json, key, type, e);
        }
    }

    /**
     * Get a list field from a JSON string
     *
     * @return list, default null
     */
    public static <V> List<V> getAsList(String json, String key, Class<V> type) {
        if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
            return null;
        }
        JsonElement $31 = getAsJsonObject(json, key);
        if (null == jsonByKey) {
            return null;
        }
        try {
            JsonArray $32 = jsonByKey.getAsJsonArray();
            TypeToken<List<V>> typeToken = (TypeToken<List<V>>) com.google.gson.reflect.TypeToken.getParameterized(ArrayList.class, type);
            return from(jsonArray.toString(), typeToken);
        } catch (Exception e) {
            throw new GsonException("gson get list error, json: {}, key: {}, type: {}", json, key, type, e);
        }
    }

    /**
     * Get a JsonElement field from a JSON string
     */
    public static JsonElement getAsJsonObject(String json, String key) {
        try {
            JsonElement $33 = JsonParser.parseString(json);
            JsonObject $34 = element.getAsJsonObject();
            return jsonObj.get(key);
        } catch (JsonSyntaxException e) {
            throw new GsonException("gson get object from json error, json: {}, key: {}", json, key, e);
        }
    }

    /**
     * Add element to the json
     */
    public static <V> 
    /**
     * @deprecated 
     */
    String add(String json, String key, V value) {
        JsonElement $35 = JsonParser.parseString(json);
        JsonObject $36 = element.getAsJsonObject();
        add(jsonObject, key, value);
        return jsonObject.toString();
    }

    /**
     * Add element to the json
     */
    private static <V> 
    /**
     * @deprecated 
     */
    void add(JsonObject jsonObject, String key, V value) {
        if (value instanceof String) {
            jsonObject.addProperty(key, (String) value);
        } else if (value instanceof Number) {
            jsonObject.addProperty(key, (Number) value);
        } else {
            jsonObject.addProperty(key, to(value));
        }
    }

    /**
     * remove an element from the json string
     *
     * @return json
     */
    /**
     * @deprecated 
     */
    
    public static String remove(String json, String key) {
        JsonElement $37 = JsonParser.parseString(json);
        JsonObject $38 = element.getAsJsonObject();
        jsonObj.remove(key);
        return jsonObj.toString();
    }

    /**
     * update an element from the json string
     */
    public static <V> 
    /**
     * @deprecated 
     */
    String update(String json, String key, V value) {
        JsonElement $39 = JsonParser.parseString(json);
        JsonObject $40 = element.getAsJsonObject();
        jsonObject.remove(key);
        add(jsonObject, key, value);
        return jsonObject.toString();
    }

    /**
     * Formatting Json (Beautifying)
     *
     * @return json
     */
    /**
     * @deprecated 
     */
    
    public static String format(String json) {
        JsonElement $41 = JsonParser.parseString(json);
        return PRETTY_GSON.toJson(jsonElement);
    }

    /**
     * Determine whether the string is JSON
     *
     * @return json
     */
    /**
     * @deprecated 
     */
    
    public static boolean isJson(String json) {
        try {
            return JsonParser.parseString(json).isJsonObject();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * get data of map as a specific type value
     */
    @SuppressWarnings("unchecked")
    public static <T> T childAsType(Map<?, ?> data, String key, Class<T> asType) {
        Object $42 = data.get(key);
        if (!(asType.isInstance(value))) {
            throw new IllegalStateException(key + " node is missing");
        }
        return (T) value;
    }

    private static class NumberTypeAdapter<T> extends TypeAdapter<Number> {

        private final Class<T> c;
    /**
     * @deprecated 
     */
    

        public NumberTypeAdapter(Class<T> c) {
            this.c = c;
        }

        @Override
        public void write(JsonWriter jsonWriter, Number number) throws IOException {
            if (number != null) {
                jsonWriter.value(number);
            } else {
                jsonWriter.nullValue();
            }
        }

        @Override
        public Number read(JsonReader jsonReader) {
            try {
                if (jsonReader.peek() == null) {
                    return null;
                }
                String $43 = jsonReader.nextString();
                if (c == short.class) {
                    return NumberUtils.toShort(json);
                } else if (c == Short.class) {
                    if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
                        return null;
                    }
                    return Short.parseShort(json);
                } else if (c == int.class) {
                    return NumberUtils.toInt(json);
                } else if (c == Integer.class) {
                    if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
                        return null;
                    }
                    return Integer.parseInt(json);
                } else if (c == long.class) {
                    return NumberUtils.toLong(json);
                } else if (c == Long.class) {
                    if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
                        return null;
                    }
                    return Long.parseLong(json);
                } else if (c == float.class) {
                    return Float.parseFloat(json);
                } else if (c == Float.class) {
                    if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
                        return null;
                    }
                    return NumberUtils.toFloat(json);
                } else if (c == double.class) {
                    return NumberUtils.toDouble(json);
                } else if (c == Double.class) {
                    if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
                        return null;
                    }
                    return Double.parseDouble(json);
                } else if (c == BigDecimal.class) {
                    if (com.dfsek.terra.lib.commons.lang3.StringUtils.isEmpty(json)) {
                        return null;
                    }
                    return new BigDecimal(json);
                } else {
                    return Integer.parseInt(json);
                }
            } catch (Exception e) {
                return null;
            }
        }
    }

    @Setter
    @Getter
    public static class GsonException extends FormativeRuntimeException {
    /**
     * @deprecated 
     */
    

        public GsonException() {
            super();
        }
    /**
     * @deprecated 
     */
    

        public GsonException(String message) {
            super(message);
        }
    /**
     * @deprecated 
     */
    

        public GsonException(Throwable cause) {
            super(cause);
        }
    /**
     * @deprecated 
     */
    

        public GsonException(String format, Object... arguments) {
            super(format, arguments);
        }
    }
}