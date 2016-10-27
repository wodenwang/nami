package com.riversoft.core.script.function;

import com.jayway.restassured.path.json.JsonPath;
import com.riversoft.core.script.annotation.ScriptSupport;
import com.riversoft.util.JsonMapper;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

/**
 * Created by exizhai on 2/17/2016.
 */
@ScriptSupport("json")
public class JsonUtil {

    /**
     * 对象格式化为JSON字符串
     * @param o 对象
     * @return JSON字符串
     */
    public static String to(Object o){
        if (o == null) {
            return "{}";
        }
        try {
            return JsonMapper.defaultMapper().toJson(o);
        } catch (Exception e) {
            return "{}";
        }
    }

    /**
     * 把字符串解析为JsonPath
     * @param json JSON字符串
     * @return JsonPath
     * @see com.jayway.restassured.path.json.JsonPath
     * http://static.javadoc.io/com.jayway.restassured/json-path/2.8.0/com/jayway/restassured/path/json/JsonPath.html
     */
    public static JsonPath from(String json) {
        return new JsonPath(json);
    }

    /**
     * 把URL指代的内容解析为JsonPath
     * @param url url
     * @return JsonPath
     * @see com.jayway.restassured.path.json.JsonPath
     * http://static.javadoc.io/com.jayway.restassured/json-path/2.8.0/com/jayway/restassured/path/json/JsonPath.html
     */
    public static JsonPath from(URL url) {
        return new JsonPath(url);
    }

    /**
     * 把stream指代的内容解析为JsonPath
     * @param stream stream
     * @return JsonPath
     * @see com.jayway.restassured.path.json.JsonPath
     * http://static.javadoc.io/com.jayway.restassured/json-path/2.8.0/com/jayway/restassured/path/json/JsonPath.html
     */
    public static JsonPath from(InputStream stream) {
        return new JsonPath(stream);
    }

    /**
     * 把文件指代的内容解析为JsonPath
     * @param file file
     * @return JsonPath
     * @see com.jayway.restassured.path.json.JsonPath
     * http://static.javadoc.io/com.jayway.restassured/json-path/2.8.0/com/jayway/restassured/path/json/JsonPath.html
     */
    public static JsonPath from(File file) {
        return new JsonPath(file);
    }

    /**
     * 把reader指代的内容解析为JsonPath
     * @param reader reader
     * @return JsonPath
     * @see com.jayway.restassured.path.json.JsonPath
     * http://static.javadoc.io/com.jayway.restassured/json-path/2.8.0/com/jayway/restassured/path/json/JsonPath.html
     */
    public static JsonPath from(Reader reader) {
        return new JsonPath(reader);
    }
}
