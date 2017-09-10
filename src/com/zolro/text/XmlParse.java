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
					Method m = cla.getMethod(str.toString(), String.class);
					m.invoke(t, value);
				}
			}
			list.add(t);		
		}
		return list;
	}

}
