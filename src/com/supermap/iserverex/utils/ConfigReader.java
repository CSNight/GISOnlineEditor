package com.supermap.iserverex.utils;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigReader {
    private static Element root = null;
    private static String path = "";
    public static boolean loaded = false;

    static {
        try {
            String url = ConfigReader.class.getResource("").getPath();
            path = url + "configration.xml";
            SAXReader sax = new SAXReader();// 创建一个SAXReader对象
            File xmlFile = new File(path);// 根据指定的路径创建file对象
            Document doc = sax.read(xmlFile);// 获取document对象,如果文档无节点，则会抛出Exception提前结束
            root = doc.getRootElement();
        } catch (Exception e) {
            e.printStackTrace();
            ConfigReader.loaded = true;
        }
    }

    public static Map<String, String> readerXml() {
        try {
            Map<String, String> configMap = new HashMap<String, String>();
            Element nList = root.element("iserver-metadata");
            @SuppressWarnings("unchecked")
            List<Element> cList = nList.elements();
            for (int j = 0; j < cList.size(); j++) {
                Element cnode = cList.get(j);
                configMap.put(cnode.getName(), cnode.getTextTrim());
            }
            ConfigReader.loaded = true;
            return configMap;
        } catch (Exception e) {
            e.printStackTrace();
            ConfigReader.loaded = false;
            return null;
        }
    }

    @SuppressWarnings({"unchecked", "deprecation"})
    public static boolean XmlWorksapceInfoSaver(
            Map<String, String> Workspaceinfo) {
        try {
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("utf-8");
            File file = new File(path);
            XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
            writer.setEscapeText(false);
            Element workspaces = root.element("workspaces");
            List<Element> nList = workspaces.elements();
            for (int i = 0; i < nList.size(); i++) {
                if (nList.get(i).getTextTrim()
                        .equals(Workspaceinfo.get("workspace"))) {
                    nList.get(i).setAttributeValue("info",
                            Workspaceinfo.get("info"));
                    ConfigReader.loaded = true;
                    writer.write(root.getDocument());
                    writer.close();
                    return ConfigReader.loaded;
                }
            }
            Element worknode = workspaces.addElement("workspace");
            worknode.setText(Workspaceinfo.get("workspace"));
            worknode.addAttribute("info", Workspaceinfo.get("info"));

            writer.write(root.getDocument());
            writer.close();
            ConfigReader.loaded = true;
            return ConfigReader.loaded;
        } catch (Exception e) {
            e.printStackTrace();
            ConfigReader.loaded = false;
            return ConfigReader.loaded;
        }
    }

    @SuppressWarnings("unchecked")
    public static String WorkspaceInfoGet(String ServerName) {
        String info = "";
        try {
            Element nList = root.element("workspaces");
            List<Element> cList = nList.elements();
            if (cList.size() == 0) {
                return info;
            }
            for (int i = 0; i < cList.size(); i++) {
                if (cList.get(i).getTextTrim().equals(ServerName)) {
                    info = cList.get(i).attributeValue("info");
                }
            }
            return info;
        } catch (Exception e) {
            e.printStackTrace();
            return info;
        }
    }

    public static Map<String, String> getDBmeta() {
        try {
            Map<String, String> configMap = new HashMap<String, String>();
            Element nList = root.element("datasource");
            @SuppressWarnings("unchecked")
            List<Element> cList = nList.elements();
            for (int j = 0; j < cList.size(); j++) {
                Element cnode = cList.get(j);
                configMap.put(cnode.getName(), cnode.getTextTrim());
            }
            ConfigReader.loaded = true;
            return configMap;
        } catch (Exception e) {
            e.printStackTrace();
            ConfigReader.loaded = false;
            return null;
        }
    }
}
