package ltd.newbee.mall.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.Feature;
import ltd.newbee.mall.filter.RepeatedlyReadRequestWrapper;
import ltd.newbee.mall.util.ThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class SecurityInterceptor extends HandlerInterceptorAdapter {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        //对来自后台的请求统一进行签名处理
        RepeatedlyReadRequestWrapper requestWrapper;
        if (request instanceof RepeatedlyReadRequestWrapper) {
            //日志记录
            InterceptorLogRecord.setMDC( request );
            requestWrapper = (RepeatedlyReadRequestWrapper) request;
            String str = RepeatedlyReadRequestWrapper.getBodyString(requestWrapper);
            //TODO 验签
            Map parameterStringMap;
            try {
                parameterStringMap =  JSON.parseObject(str,LinkedHashMap.class, Feature.OrderedField);
            }catch (Exception e){
//                RepeatedlyReadRequestWrapper.outputOneByOutputStream(request, response, JSON.toJSONString( ResponseUtils.fail( ResponseCodeEnum.PARAM_EXC)));
                return false;
            }
            //记录请求参数
            Map<String, Object> params = new HashMap<>(parameterStringMap.size() * 2);
            params.putAll( parameterStringMap );
            ThreadLocalUtil.setParameter( params );
            ThreadLocalUtil.setTimeStart( System.currentTimeMillis() );
        }

        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        InterceptorLogRecord.completionLog( request,  response, StringUtils.EMPTY);
    }

}
