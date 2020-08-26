package ltd.newbee.mall.util.wxpay;

import java.beans.PropertyDescriptor;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
//import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.cglib.beans.BeanMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;

/**
 * ClassName: beanToMap <br/>
 * Function: TODO <br/>
 * date 2017年12月19日 <br/>
 *
 * @author lft
 * @version 1.0
 * @since JDK1.8
 * @see
 *
 */

public class BeanToMap {

	/*public static Map<String, Object> beanToMap(Object obj) {
		Map<String, Object> params = new HashMap<String, Object>(0);
		try {
			PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
			PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
			for (int i = 0; i < descriptors.length; i++) {
				String name = descriptors[i].getName();
				if (!"class".equals(name)) {
					params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return params;
	}

	public static List<Map<String, Object>> beanListToMap(List<?> objs) {
		List<Map<String, Object>> results = new ArrayList<>();
		try {
			for (Object obj : objs) {
				Map<String, Object> params = new HashMap<String, Object>(0);
				PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
				PropertyDescriptor[] descriptors = propertyUtilsBean.getPropertyDescriptors(obj);
				for (int i = 0; i < descriptors.length; i++) {
					String name = descriptors[i].getName();
					if (!"class".equals(name)) {
						params.put(name, propertyUtilsBean.getNestedProperty(obj, name));
					}
				}
				results.add(params);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}*/
	
	/**
	 * 将List<T>转换为List<Map<String, Object>>
	 * @param objList
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	/*public static <T> List<Map<String, Object>> objectsToMaps(List<T> objList) {
		List<Map<String, Object>> list = new ArrayList<>();
		if (objList != null && objList.size() > 0) {
			Map<String, Object> map = null;
			T bean = null;
			for (int i = 0,size = objList.size(); i < size; i++) {
				bean = objList.get(i);
				map = beanToMap(bean);
				list.add(map);
			}
		}
		return list;
	}*/

	/**
	 * 将List<Map<String,Object>>转换为List<T>
	 * @param maps
	 * @param clazz
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public static <T> List<T> mapsToObjects(List<Map<String, Object>> maps,Class<T> clazz) throws InstantiationException, IllegalAccessException {
		List<T> list = new ArrayList<>();
		if (maps != null && maps.size() > 0) {
			Map<String, Object> map = null;
			T bean = null;
			for (int i = 0,size = maps.size(); i < size; i++) {
				map = maps.get(i);
				bean = clazz.newInstance();
				mapToBean(map, bean);
				list.add(bean);
			}
		}
		return list;
	}
	
	/**
	 * 将map装换为javabean对象
	 * @param map
	 * @param bean
	 * @return
	 */
	public static <T> T mapToBean(Map<String, Object> map,T bean) {
		BeanMap beanMap = BeanMap.create(bean);
		beanMap.putAll(map);
		return bean;
	}
	
	/**
	 * XML转换为Map
	 * 
	 * @param strXML
	 * @return Map
	 * @throws Exception
	 */
	public static Map<String, Object> getMapFromXML(String strXML) throws Exception {
        try {
            Map<String, Object> data = new HashMap<String, Object>();
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            //防止XXE攻击
            documentBuilderFactory.setXIncludeAware(false);
            documentBuilderFactory.setExpandEntityReferences(false);
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            InputStream stream = new ByteArrayInputStream(strXML.getBytes("UTF-8"));
            org.w3c.dom.Document doc = documentBuilder.parse(stream);
            doc.getDocumentElement().normalize();
            NodeList nodeList = doc.getDocumentElement().getChildNodes();
            for (int idx = 0; idx < nodeList.getLength(); ++idx) {
                Node node = nodeList.item(idx);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    org.w3c.dom.Element element = (org.w3c.dom.Element) node;
                    data.put(element.getNodeName(), element.getTextContent());
                }
            }
            try {
                stream.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return data;
        } catch (Exception ex) {
            throw ex;
        }
	}
	
}
