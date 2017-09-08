package com.zolro.text;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class XmlParse {
	public static String creaObject(Field field, Node node) {
		if (node.getChildNodes().getLength() > 1) {
			NodeList nodes = node.getChildNodes();
			for (int i = 0; i < nodes.getLength(); i++) {
				String value = creaObject(field, nodes.item(i));
				if (value != null)
					return value;
			}
		} else if (node.getChildNodes().getLength() == 1) {
			if (field.getName().equals(node.getNodeName())) {
				return node.getFirstChild().getNodeValue();
			}
		}
		return null;
	}

	public static <T> List<T> getObject(String filePath, Class<T> cla)
			throws ParserConfigurationException, IOException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, SecurityException, IllegalArgumentException,
			InvocationTargetException, DOMException, SAXException {
		List<T> list = new ArrayList<T>();
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(filePath);// 换为Document对象
		NodeList placeList = document.getElementsByTagName(cla.getSimpleName());
		Field[] fields = cla.getDeclaredFields();
		for (int i = 0; i < placeList.getLength(); i++) {
			T t = cla.newInstance();
			for (Field field : fields) {
				String value = creaObject(field, placeList.item(i));
				if (value != null) {
					Node name = placeList.item(i);
					StringBuilder str = new StringBuilder();
					str.append("set");
					str.append(field.getName().substring(0, 1).toUpperCase());
					str.append(field.getName().substring(1));
					/*
					 * String method = "set" + (field.getName().substring(0, 1)
					 * .toUpperCase() + field.getName() .substring(1));
					 */
					Method m = cla.getMethod(str.toString(), String.class);
					m.invoke(t, value);
				}
			}
			list.add(t);
			/*
			 * Node place = placeList.item(i); NodeList placeNodes =
			 * place.getChildNodes();
			 */
			/*
			 * for (int j = 0; j < placeNodes.getLength(); j++) {
			 * if(placeNodes.item(j).getChildNodes().getLength()>1){
			 * System.out.println(placeNodes.item(j).getNodeName()); } String
			 * value=creaTObject(fileds[0],placeNodes.item(j));
			 * System.out.println(value); if (value != null) { Node name =
			 * placeNodes.item(j); String method = "set" +
			 * (fileds[0].getName().substring(0, 1) .toUpperCase() +
			 * fileds[0].getName() .substring(1)); Method m =
			 * cla.getMethod(method, String.class); m.invoke(t, value); } for
			 * (Field filed : fileds) { String
			 * value=creaTObject(filed,placeNodes.item(j));
			 * System.out.println("filedValue:"+value); if (value != null) {
			 * Node name = placeNodes.item(j); String method = "set" +
			 * (filed.getName().substring(0, 1) .toUpperCase() + filed.getName()
			 * .substring(1)); Method m = cla.getMethod(method, String.class);
			 * m.invoke(t, value); }
			 * 
			 * } }
			 */

		}
		return list;
	}

	public static void testExample(String txt) {
		try {
			// 1、打开流
			Writer w = new FileWriter("g:/Jinweidu.txt", true);
			// 2、写入内容
			w.write(txt);
			// 3、关闭流
			w.close();
		} catch (IOException e) {
			System.out.println("文件写入错误：" + e.getMessage());
		}
	}
}
