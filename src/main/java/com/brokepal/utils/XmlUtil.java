package com.brokepal.utils;

import com.brokepal.constant.Const;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.*;

/**
 * Created by Administrator on 2016/11/8.
 */
public class XmlUtil {
    public static String getNodeText(String language, String nodeId) throws DocumentException{
        //确定是哪种语言
        String xmlFileName = "";
        File dir = new File(Const.Language.stringXmlFilePath);
        File[] files=dir.listFiles();
        if(files!=null && files.length>0)
            for (File file : files)
                if (file.getName().contains(language))
                    xmlFileName = file.getName();

        String nodeText;
        SAXReader saxReader = new SAXReader();

        Document document = saxReader.read(new File(Const.Language.stringXmlFilePath + xmlFileName));

        org.dom4j.Element root = document.getRootElement();// 获取根元素

        //获取目标字符串的命名空间
        String[] nodePackage = nodeId.split("\\.");
        org.dom4j.Element namespace = root;
        for (int i = 0; i < nodePackage.length - 1; i++){
            List<org.dom4j.Element> packages = namespace.elements("package");
            for (org.dom4j.Element pack :packages)
                if (nodePackage[i].equals(pack.attributeValue("id")))
                    namespace = pack;
        }

        List<org.dom4j.Element> strings = namespace.elements("string");// 获取特定名称的子元素

        for (org.dom4j.Element string :strings)
            if (nodePackage[nodePackage.length-1].equals(string.attributeValue("id"))){
                nodeText = string.getData().toString();
                return nodeText;
            }

        throw new NoSuchElementException("There is no such a node whose id is " + nodeId + ". Make sure that you give the correct package path.");
    }

    public static String[] getArrayText(String language,String nodeId) throws DocumentException {
        //确定是哪种语言
        String xmlFileName = "";
        File dir = new File(Const.Language.stringXmlFilePath);
        File[] files=dir.listFiles();
        if(files!=null && files.length>0)
            for (File file : files)
                if (file.getName().contains(language))
                    xmlFileName = file.getName();

        List<String> arrayText = new ArrayList<String>();
        SAXReader saxReader = new SAXReader();
        Document document = saxReader.read(new File(Const.Language.stringXmlFilePath + xmlFileName));

        org.dom4j.Element root = document.getRootElement();
        String[] nodePackage = nodeId.split("\\.");
        org.dom4j.Element namespace = root;
        for (int i = 0; i < nodePackage.length - 1; i++){
            List<org.dom4j.Element> packages = namespace.elements("package");
            for (org.dom4j.Element pack :packages)
                if (nodePackage[i].equals(pack.attributeValue("id")))
                    namespace = pack;
        }

        // 获取特定名称的子元素
        List<org.dom4j.Element> arrays = namespace.elements("array");

        for (org.dom4j.Element array :arrays) {
            if (nodePackage[nodePackage.length-1].equals(array.attributeValue("id"))){
                List<org.dom4j.Element> strings = array.elements();// 获取所有子元素
                for (org.dom4j.Element string :strings)
                    arrayText.add(string.getData().toString());
                return (String[])arrayText.toArray(new String[arrayText.size()]);
            }
        }
        throw new NoSuchElementException("There is no such a node whose id is " + nodeId + ". Make sure that you give the correct package path.");
    }

    public static String getCommonNodeText(String path, String nodeId){
        SAXReader saxReader = new SAXReader();

        Document document = null;
        try {
            document = saxReader.read(new File(path));
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        org.dom4j.Element root = document.getRootElement();// 获取根元素

        //获取目标字符串的命名空间
        String[] nodePackage = nodeId.split("\\.");
        org.dom4j.Element node = root;
        for (int i = 0; i < nodePackage.length; i++){
            List<org.dom4j.Element> packages = node.elements();
            for (org.dom4j.Element pack :packages)
                if (nodePackage[i].equals(pack.getName()))
                    node = pack;

        }
        if (node != root)
            return node.getData().toString();

        throw new NoSuchElementException("There is no such a node whose id is " + nodeId + ". Make sure that you give the correct package path.");
    }
}
