package com.riversoft.core.script.function;

import com.jayway.restassured.specification.RequestSpecification;
import com.riversoft.core.script.annotation.ScriptSupport;

/**
 * Created by exizhai on 2/16/2016.
 */
@ScriptSupport("http")
public class HttpUtil {

    /**
     * 初始化一个http client
     * @return RequestSpecification
     * @see com.jayway.restassured.specification.RequestSpecification
     * @see com.jayway.restassured.path.json.JsonPath
     * @see com.jayway.restassured.path.xml.XmlPath
     */
    public static RequestSpecification given(){
        return com.jayway.restassured.RestAssured.given();
    }
}
