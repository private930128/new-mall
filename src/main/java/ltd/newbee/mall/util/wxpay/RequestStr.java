package ltd.newbee.mall.util.wxpay;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import javax.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSONObject;


public class RequestStr {

	/**
	 * 请求体数据流获取
	 *
	 * @author jiangguqiang
	 * @param request
	 * @return 请求流字符串
	 */
	public static String getRequestStr(HttpServletRequest request) {
		BufferedReader in;
		try {
			Object requestStr = request.getAttribute("RequestStr");
			if (!StringUtils.isEmpty(requestStr)) {
				return requestStr.toString();
			}
			in = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			/*千万不可去除！！*/
			request.setAttribute("RequestStr", sb.toString());
			return sb.toString();
		} catch (IOException e) {
			return "";
		}
	}

	/**
	 * 从Parameter中获取参数
	 *
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getParamJson(HttpServletRequest request) {
		Object requestStr = request.getAttribute("RequestStr");
		if (!StringUtils.isEmpty(requestStr)) {
			return requestStr.toString();
		}
	    // 参数Map
	    Map<String, String[]> properties = request.getParameterMap();
	    // 返回值Map
	    //Map<String,String> returnMap = new HashMap<String,String>();
	    Iterator entries = properties.entrySet().iterator();
	    Entry entry;
	    String name = "";
	    //String value = "";
	    //String results = "";
	    while (entries.hasNext()) {
	        entry = (Entry) entries.next();
	        name = (String) entry.getKey();
	        request.setAttribute("RequestStr", name);
	    }
	    return name;
	}

	/**
	 * 从Parameter中获取参数
	 *
	 * @param request
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static String getParameterMap(HttpServletRequest request) {
		Object requestStr = request.getAttribute("RequestParm");
		if (!StringUtils.isEmpty(requestStr)) {
			return requestStr.toString();
		}
	    // 参数Map
	    Map<String, String[]> properties = request.getParameterMap();
	    // 返回值Map
	    Map<String,String> returnMap = new HashMap<String,String>();
		Iterator entries = properties.entrySet().iterator();
		Entry entry;
	    String name = "";
	    String value = "";
	    String results = "";
	    while (entries.hasNext()) {
	        entry = (Entry) entries.next();
	        name = (String) entry.getKey();
	        Object valueObj = entry.getValue();
	        if(null == valueObj){
	            value = "";
	        }else if(valueObj instanceof String[]){
	            String[] values = (String[])valueObj;
	            for(int i=0;i<values.length;i++){
	                value = values[i] + ",";
	            }
	            value = value.substring(0, value.length()-1);
	        }else{
	            value = valueObj.toString();
	        }
	        returnMap.put(name, value);
	        results = returnMap.toString().replace("{{","{").replace("}}", "}");
	        request.setAttribute("RequestParm", results);
	        System.out.println("jsonStr = "+results);
	    }
	    return results;
	}

	/**
	 * 从request中获取参数,遍历,并且转成json格式返回
	 * @param request
	 * @return
	 */
	public static String getParamJsonString(HttpServletRequest request) {
		Map<String, String[]> map = request.getParameterMap();
		Map<String, String> results = new HashMap<>();
		Set<Entry<String, String[]>> entrySet = map.entrySet();
		for (Entry<String, String[]> stringEntry : entrySet) {
			results.put(stringEntry.getKey(),stringEntry.getValue()[0]);
		}
		return JSONObject.toJSONString (results);
	}
}