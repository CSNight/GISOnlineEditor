package com.supermap.iserverex.utils;

import net.sf.json.*;
import net.sf.json.xml.XMLSerializer;
import org.w3c.dom.Document;

import java.util.List;
import java.util.Map;

public class JSONUtil {

    /**
     * JSON工具类 <li>使用该类请导入json相关架包：
     * <p>
     * 1. commons-beanutils.jar
     * </p>
     * <p>
     * 2. commons-collections-3.2.1.jar
     * </p>
     * <p>
     * 3. commons-lang-*.jar
     * </p>
     * <p>
     * 4. xom-*.jar
     * </p>
     * <p>
     * 5. ezmorph-*.jar
     * </p>
     * <p>
     * 6. json-lib-*-jdk15.jar
     * </p>
     * </li> <li>JSON 即 JavaScript Object Natation，它是一种轻量级的数据交换格式，非常适合于服务器与
     * JavaScript 的交互</li> <li>将JSON写入servlet响应需要设置正确的 MIME
     * 类型（application/json）和字符编码如下：
     * <p>
     * response.setContentType("application/json;charset=UTF-8");
     * </p>
     * <p>
     * response.setCharacterEncoding("UTF-8");
     * </p>
     * <p>
     * PrintWriter pw = response.getWriter();
     * </p>
     * <p>
     * pw.write(JsonUtil.toJson(obj));
     * </p>
     * <p>
     * pw.flush();
     * </p>
     * </li> <li>在客户端使用 JSON.可以通过对 eval() 的简单调用实现，这个函数可以即时地解释包含 JavaScript
     * 表达式的字符串,最后使用 属性.值.值.... 的形式获得想要的数据</li> <li>Ajax 的 Java 对象序列化方式有：
     * <p>
     * 1、从 Java 类产生 XML，每个对象都编写自己的 XML 生成代码
     * </P>
     * <p>
     * 2、XML 绑定框架，简化 XML 文档到 Java 对象图的绑定过程，使用代码生成的框架包括XMLBeans、JAXB、Zeus 和
     * JBind，采用映射方式的框架包括 Castor 和 Apache Commons Betwixt
     * </P>
     * <p>
     * 3、页面模板系统，使用jstl等标记语言形成想要的模板
     * </P>
     * <p>
     * 4、不用 XML 的响应数据，除了 responseXML 之外，XMLHttpRequest 对象还提供了名为 responseText
     * 的属性，这个属性只是以字符串的方式提供服务器的响应体，在只需要从响应文档中提取单一值的场景中，“欺骗性”地把 XML
     * 当作文本字符串并合理使用正则表达式，而不把它当作结构化的文档对待，会更方便
     * </P>
     * <p>
     * 5、JavaScript 对象标注，即JSON
     * </P>
     * </li> <li>
     * 如果要处理数百种数据类型，这时想要的是可伸缩性，那么代码生成可能就是最好的选择。如果需要为同一数据模型生成多个不同视图，那么就应当使用页面模板
     * 。如果处理的是小规模项目，想降低需要编写的 JavaScript 代码数量，那么请考虑 JSON</li>
     *
     * @author yangYong
     *
     */

    /**
     * 将一个实体类对象转化成JSON数据格式,等效于object2json
     *
     * @param obj 实体类对象
     * @return JSON数据格式字符串
     */
    public static String pojo2json(Object obj) {
        JsonConfig jsonConfig = new JsonConfig();
        return JSONObject.fromObject(obj, jsonConfig).toString(1);
    }

    public static Object map2pojo(@SuppressWarnings("rawtypes") Map map,
                                  @SuppressWarnings("rawtypes") Class javaBean) {
        String str = JSONUtil.map2json(map);
        return JSONUtil.json2pojo(str, javaBean);
    }

    /**
     * 将数组集合等对象转换成JSON字符串
     *
     * @param list
     * @return
     */
    public static String object2json(Object list) {
        return JSONSerializer.toJSON(list).toString(1);
    }

    /**
     * 将Map准换为JSON字符串,等效于object2json()
     *
     * @param map map集合
     * @return JSON字符串
     */
    public static String map2json(Map<?, ?> map) {
        JSONObject object = JSONObject.fromObject(map);
        return object.toString(1);
    }

    /**
     * 将xml字符串转换为JSON字符串
     *
     * @param xmlString xml字符串
     * @return JSON对象
     */
    public static String xml2json(String xmlString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        JSON json = xmlSerializer.read(xmlString);
        return json.toString(1);
    }

    /**
     * 将xmlDocument转换为JSON对象
     *
     * @param xmlDocument XML Document
     * @return JSON对象
     */

    public static String xml2json(Document xmlDocument) {
        return xml2json(xmlDocument.toString());
    }

    /**
     * 读取XML文件准换为JSON字符串
     *
     * @param xmlFile XML文件
     * @return JSON字符串
     */
    public String xmlFile2json(String xmlFile) {

        return null;
    }

    /**
     * 将Json格式的字符串转换成指定的对象返回
     *
     * @param jsonStr  要转化的Json格式的字符串
     * @param javaBean 指定转化对象类型
     * @return 转化后的对象
     */
    public static Object json2pojo(String jsonStr,
                                   @SuppressWarnings("rawtypes") Class javaBean) {

        JSONObject jsonObj = JSONObject.fromObject(jsonStr);

        Object obj = JSONObject.toBean(jsonObj, javaBean);
        return obj;
    }

    /**
     * 将Json格式的字符串转换成Map对象
     *
     * @param jsonString JSON数据格式字符串
     * @return map集合
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> json2Map(String jsonString) {
        return (Map<String, Object>) json2pojo(jsonString, Map.class);
    }

    /**
     * 将Json格式的字符串转换成对象数组返回
     *
     * @param jsonString JSON数据格式字符串
     * @return 对象数组
     */
    public static Object[] json2ObjectArray(String jsonString) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        return jsonArray.toArray();
    }

    /**
     * 将Json格式的字符串转换成指定对象组成的List返回
     *
     * @param jsonString JSON数据格式字符串
     * @param pojoClass  指定转化对象类型
     * @return list集合
     */
    @SuppressWarnings("deprecation")
    public static List<?> jsonArray2List(String jsonString,
                                         @SuppressWarnings("rawtypes") Class pojoClass) {
        JSONArray jsonArray = JSONArray.fromObject(jsonString);
        return JSONArray.toList(jsonArray, pojoClass);
    }

    /**
     * pojo，集合，数组等对象转换成XML字符串
     *
     * @param obj
     * @return
     */
    public static String obj2xml(Object obj) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        return xmlSerializer.write(JSONSerializer.toJSON(obj)).replaceAll(
                "class=", "type=");

    }

    /**
     * JSON(数组)字符串转换成XML字符串
     *
     * @param jsonString
     * @return
     */
    public static String json2xml(String jsonString) {
        XMLSerializer xmlSerializer = new XMLSerializer();
        return xmlSerializer.write(JSONSerializer.toJSON(jsonString));
        // return
        // xmlSerializer.write(JSONArray.fromObject(jsonString));//这种方式只支持JSON数组
    }

    public static String ConvertToString(String resultType, Object obj) {
        if (resultType != null) {
            if (resultType.equals("xml") || resultType.equals("XML")) {
                return JSONUtil.obj2xml(obj);
            } else if (resultType.equals("json") || resultType.equals("JSON")) {
                return JSONUtil.pojo2json(obj);
            } else if (resultType.equals("fastjson")
                    || resultType.equals("FASTJSON")) {
                return JSONUtil.pojo2json(obj);
            }
        } else {
            return "转换信息错误!";
        }

        return "";
    }
}
