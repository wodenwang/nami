package com.riversoft.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Borball on 4/14/2015.
 */
public class JsonMapper {

    private static Logger logger = LoggerFactory.getLogger(JsonMapper.class);

    private static JsonMapper defaultJsonMapper = null;
    private static JsonMapper nonDefaultJsonMapper = null;
    private static JsonMapper nonEmptyJsonMapper = null;

    private ObjectMapper mapper;

    public JsonMapper() {
        this(null);
    }

    public JsonMapper(JsonInclude.Include include) {
        mapper = new ObjectMapper();

        if (include != null) {
            mapper.setSerializationInclusion(include);
        }

        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
    }

    public synchronized static JsonMapper nonEmptyMapper() {
        if (nonEmptyJsonMapper == null) {
            nonEmptyJsonMapper = new JsonMapper(JsonInclude.Include.NON_EMPTY);
        }
        return nonEmptyJsonMapper;
    }

    public synchronized static JsonMapper nonDefaultMapper() {
        if (nonDefaultJsonMapper == null) {
            nonDefaultJsonMapper = new JsonMapper(JsonInclude.Include.NON_DEFAULT);
        }
        return nonDefaultJsonMapper;
    }

    public synchronized static JsonMapper defaultMapper() {
        if (defaultJsonMapper == null) {
            defaultJsonMapper = new JsonMapper();
        }
        return defaultJsonMapper;
    }

    public String toJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.warn("toJson出错:" + object, e);
            return null;
        }
    }

    public <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().length() == 0) {
            return null;
        }

        try {
            return mapper.readValue(json, clazz);
        } catch (IOException e) {
            logger.warn("fromJson出错:" + json, e);
            return null;
        }
    }

    public HashMap<String, Object> json2Map(String json) {
        return fromJson(json, HashMap.class);
    }

    public <T> T convert(Object object, Class<T> clazz) {
        if (object == null) {
            return null;
        }

        return mapper.convertValue(object, clazz);
    }

    public <T> List<T> fromJsons(String jsons, Class<T> clazz) throws IOException {
        if (jsons == null || jsons.trim().length() == 0) {
            return Collections.EMPTY_LIST;
        }

        List<T> list = new ArrayList<>();

        JsonNode jsonNode = mapper.readTree(jsons);
        if (jsonNode.isArray()) {//是数组
            for (JsonNode child : jsonNode) {
                list.add(mapper.treeToValue(child, clazz));
            }
        } else {//不是数组
            list.add(fromJson(jsons, clazz));
        }

        return list;

    }

    public ObjectMapper getMapper() {
        return mapper;
    }

}
