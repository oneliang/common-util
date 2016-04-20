package com.oneliang.util.common;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.oneliang.Constant;
import com.oneliang.util.common.ClassUtil.ClassProcessor;

public final class JavaXmlUtil{

	private static final ClassProcessor DEFAULT_CLASS_PROCESSOR=ClassUtil.DEFAULT_CLASS_PROCESSOR;

	/**
	 * get document builder
	 * @return DocumentBuilder
	 */
	private static DocumentBuilder getDocumentBuilder(){
		DocumentBuilder documentBuilder=null;
		DocumentBuilderFactory documentBuilderFactory=DocumentBuilderFactory.newInstance();
		try {
			documentBuilder=documentBuilderFactory.newDocumentBuilder();
		} catch (Exception e) {
			throw new JavaXmlUtilException(e);
		}
		return documentBuilder;
	}

	public static Document getEmptyDocument(){
		Document document=null;
		try {
			DocumentBuilder documentBuilder=getDocumentBuilder();
			document=documentBuilder.newDocument();
			document.normalize();
		} catch (Exception e) {
			throw new JavaXmlUtilException(e);
		}
		return document;
	}

	/**
	 * parse
	 * @param filename
	 * @return Document
	 */
	public static Document parse(final String filename){
		Document document=null;
		try {
			DocumentBuilder documentBuilder=getDocumentBuilder();
			document = documentBuilder.parse(new File(filename));
			document.normalize();
		} catch (Exception e) {
			throw new JavaXmlUtilException(e);
		}
		return document;
	}

	/**
	 * parse
	 * @param inputStream
	 * @return Document
	 */
	public static Document parse(final InputStream inputStream){
		Document document=null;
		try {
			DocumentBuilder documentBuilder=getDocumentBuilder();
			document = documentBuilder.parse(inputStream);
			document.normalize();
		} catch (Exception e) {
			throw new JavaXmlUtilException(e);
		}
		return document;
	}

	/**
	 * save document
	 * @param document
	 * @param outputFullFilename
	 */
	public static void saveDocument(final Document document, final String outputFullFilename) {
		OutputStream outputStream=null;
		try{
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			transformer.setOutputProperty(OutputKeys.ENCODING, Constant.Encoding.UTF8);
			outputStream=new FileOutputStream(outputFullFilename);
			StreamResult result = new StreamResult(outputStream);
			transformer.transform(domSource, result);
		} catch (Exception e){
			throw new JavaXmlUtilException(e);
		} finally{
			if(outputStream!=null){
				try {
					outputStream.close();
				} catch (Exception e) {
					throw new JavaXmlUtilException(e);
				}
			}
		}
	}

	/**
	 * initialize from attribute map
	 * @param object
	 * @param namedNodeMap
	 */
	public static void initializeFromAttributeMap(final Object object,final NamedNodeMap namedNodeMap){
		initializeFromAttributeMap(object, namedNodeMap, DEFAULT_CLASS_PROCESSOR);
	}

	/**
	 * initialize from attribute map
	 * @param object
	 * @param namedNodeMap
	 * @param classProcessor
	 */
	public static void initializeFromAttributeMap(final Object object,final NamedNodeMap namedNodeMap,final ClassProcessor classProcessor){
		if(namedNodeMap!=null){
			Method[] methods=object.getClass().getMethods();
			for(Method method:methods){
                String methodName=method.getName();
                String fieldName=null;
                if(methodName.startsWith(Constant.Method.PREFIX_SET)){
                    fieldName=ObjectUtil.methodNameToFieldName(Constant.Method.PREFIX_SET, methodName);
                }
                if(fieldName!=null){
                	Node node=namedNodeMap.getNamedItem(fieldName);
                	if(node!=null){
                		Class<?>[] classes=method.getParameterTypes();
                		if(classes.length==1){
                        	Class<?> objectClass=classes[0];
                        	String attributeValue=node.getNodeValue();
                        	Object value=ClassUtil.changeType(objectClass, new String[]{attributeValue}, classProcessor);
                        	try {
								method.invoke(object, value);
                        	}catch(Exception e){
                        		throw new JavaXmlUtilException(e);
                        	}
                		}
                	}
                }
			}
		}
	}

	/**
	 * xml to list
	 * @param <T>
	 * @param xml
	 * @param xmlObjectTag
	 * @param clazz
	 * @param mapping
	 * @return List<T>
	 */
	public static <T extends Object> List<T> xmlToList(String xml, String xmlObjectTag, Class<T> clazz, Map<String,String> mapping) {
		List<T> list = new ArrayList<T>();
		try {
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes(Constant.Encoding.UTF8));
			Document document = JavaXmlUtil.parse(inputStream);
			Element root = document.getDocumentElement();
			NodeList nodeList=root.getElementsByTagName(xmlObjectTag);
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node=nodeList.item(i);
				list.add(xmlToObject(((Element)node), clazz, mapping));
			}
		} catch (Exception e) {
			throw new JavaXmlUtilException(e);
		}
		return list;
	}

	/**
	 * xml to object
	 * @param <T>
	 * @param element
	 * @param clazz
	 * @param mapping
	 * @return <T>
	 */
	private static <T extends Object> T xmlToObject(Element element, Class<T> clazz, Map<String,String> mapping) {
		T object=null;
		try {
			object = clazz.newInstance();
			Method[] methods=clazz.getMethods();
			for(Method method:methods){
                String methodName=method.getName();
                Class<?>[] classes=method.getParameterTypes();
                if(methodName.startsWith(Constant.Method.PREFIX_SET)){
                	String fieldName=ObjectUtil.methodNameToFieldName(Constant.Method.PREFIX_SET, methodName);
                    if(fieldName!=null){
                    	String xmlTagName=mapping.get(fieldName);
                    	if(xmlTagName!=null){
                    		NodeList nodeList=element.getElementsByTagName(xmlTagName);
                    		if(nodeList!=null&&nodeList.getLength()>0){
                    			Node node=nodeList.item(0);
                    			String xmlTagValue=StringUtil.nullToBlank(node.getTextContent()).trim();
                    			if (classes.length == 1) {
                    				Object value=ClassUtil.changeType(classes[0], new String[]{xmlTagValue}, DEFAULT_CLASS_PROCESSOR);
                        			method.invoke(object, value);
                    			}
                    		}
                    	}
                    }
                }
			}
		} catch (Exception e) {
			throw new JavaXmlUtilException(e);
		}
		return object;
	}

	/**
	 * xml to object
	 * @param <T>
	 * @param xml
	 * @param clazz
	 * @param mapping
	 * @return <T>
	 */
	public static <T extends Object> T xmlToObject(String xml, Class<T> clazz, Map<String,String> mapping) {
		T object=null;
		try {
			InputStream inputStream = new ByteArrayInputStream(xml.getBytes(Constant.Encoding.UTF8));
			Document document = JavaXmlUtil.parse(inputStream);
			Element root = document.getDocumentElement();
			object=xmlToObject(root, clazz, mapping);
		} catch (Exception e) {
			throw new JavaXmlUtilException(e);
		}
		return object;
	}

	public static class JavaXmlUtilException extends RuntimeException{
		private static final long serialVersionUID = 4669527982017700891L;
		public JavaXmlUtilException(Throwable cause) {
			super(cause);
		}
	} 
}
