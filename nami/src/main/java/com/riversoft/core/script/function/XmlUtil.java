package com.riversoft.core.script.function;

import com.jayway.restassured.path.xml.XmlPath;
import com.riversoft.core.script.annotation.ScriptSupport;
import org.xml.sax.InputSource;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;

/**
 * @borball on 2/17/2016.
 */
@ScriptSupport("xml")
public class XmlUtil {

    /**
     * 把xml文本解析为XmlPath
     * @param xml xml
     * @return XmlPath
     * @see com.jayway.restassured.path.xml.XmlPath
     * http://static.javadoc.io/com.jayway.restassured/xml-path/2.8.0/com/jayway/restassured/path/xml/XmlPath.html
     */
    public static XmlPath from(String xml) {
        return new XmlPath(xml);
    }

    /**
     * 把stream指代的文本解析为XmlPath
     * @param stream stream
     * @return XmlPath
     * @see com.jayway.restassured.path.xml.XmlPath
     * http://static.javadoc.io/com.jayway.restassured/xml-path/2.8.0/com/jayway/restassured/path/xml/XmlPath.html
     */
    public static XmlPath from(InputStream stream) {
        return new XmlPath(stream);
    }

    /**
     * 把source指代的文本解析为XmlPath
     * @param source source
     * @return XmlPath
     * @see com.jayway.restassured.path.xml.XmlPath
     * http://static.javadoc.io/com.jayway.restassured/xml-path/2.8.0/com/jayway/restassured/path/xml/XmlPath.html
     */
    public static XmlPath from(InputSource source) {
        return new XmlPath(source);
    }

    /**
     * 把file指代的文本解析为XmlPath
     * @param file file
     * @return XmlPath
     * @see com.jayway.restassured.path.xml.XmlPath
     * http://static.javadoc.io/com.jayway.restassured/xml-path/2.8.0/com/jayway/restassured/path/xml/XmlPath.html
     */
    public static XmlPath from(File file) {
        return new XmlPath(file);
    }

    /**
     * 把reader指代的文本解析为XmlPath
     * @param reader reader
     * @return XmlPath
     * @see com.jayway.restassured.path.xml.XmlPath
     * http://static.javadoc.io/com.jayway.restassured/xml-path/2.8.0/com/jayway/restassured/path/xml/XmlPath.html
     */
    public static XmlPath from(Reader reader) {
        return new XmlPath(reader);
    }

}
