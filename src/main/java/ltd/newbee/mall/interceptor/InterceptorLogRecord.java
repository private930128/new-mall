package ltd.newbee.mall.interceptor;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import ltd.newbee.mall.common.Constants;
import ltd.newbee.mall.util.ThreadLocalUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.UUID;

/**
 * 日志
 */
@Slf4j
public class InterceptorLogRecord {

    /**
     * 日志记录
     * @param request
     * @param response
     * @param returnMsg
     */
    public static void completionLog(HttpServletRequest request, HttpServletResponse response, String returnMsg) {
        // 最后拼成的日志字符串
        StringBuilder sb = new StringBuilder(15);
        sb.append( "\n-----" );
        sb.append( "\n| RequestUri==【" ).append( request.getRequestURI() ).append( "】" );

        // request 参数日志
        Map bodyParam = ThreadLocalUtil.getParameter();
        if (null != bodyParam) {
            StringBuilder requestLog = new StringBuilder("\n| RequestBody==【");
            int i=0;
            for (Object key : bodyParam.keySet()) {
                i++;
                Object values = bodyParam.get(key);
                requestLog.append(String.format("%s:[", key));
                requestLog.append( JSON.toJSONString( values ));
                requestLog.append("]");
                if (i<bodyParam.keySet().size()) {
                    requestLog.append( "," );
                }
            }
            sb.append(requestLog.append( "】" ).toString());
        }

        // response 日志
        String responseLog = returnMsg;
        if (null != ThreadLocalUtil.getResult()) {
            if (ThreadLocalUtil.getResult() instanceof String) {
                responseLog = ThreadLocalUtil.getResult().toString();
            } else {
                responseLog = JSON.toJSONString(ThreadLocalUtil.getResult());
            }
        }
        sb.append(String.format("\n| Response==【%s】", responseLog));

        //处理时间
        if (null != ThreadLocalUtil.getTimeStart()) {
            sb.append(String.format("\n| Process==【start:[%s],end:[%s],process:[%s]】", ThreadLocalUtil.getTimeStart(), System.currentTimeMillis(), System.currentTimeMillis() - ThreadLocalUtil.getTimeStart()));
        }

        sb.append( "\n-----" );
        if (log.isInfoEnabled()) {
            log.info(sb.toString());
        }

        ThreadLocalUtil.clearThreadLocal();
        MDC.clear();
    }

    /**
     * 缓存日志信息
     * @param request
     */
    public static void setMDC(HttpServletRequest request){
        // ip 地址
        String ipAddress = getRemortIP(request);
        if (ipAddress == null) {
            ipAddress = Constants.NULL_FLAG;
        }
        MDC.put( Constants.MDC_REQ_IP, ipAddress);

        String logId = UUID.randomUUID().toString().replace(Constants.NULL_FLAG, StringUtils.EMPTY);
        MDC.put(Constants.MDC_LOG_ID, logId);
    }

    /**
     * 获取请求IP
     * @param request
     * @return
     */
    public static String getRemortIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        //这里主要是获取本机的ip,可有可无
        if ("127.0.0.1".equals(ip) || ip.endsWith("0:0:0:0:0:0:1")) {
            // 根据网卡取本机配置的IP
            InetAddress inet = null;
            try {
                inet = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                log.error(e.getMessage(), e);
            }
            if(inet != null){
                ip = inet.getHostAddress();
            }
            return ip;
        }
        if(ip.length() > 0){
            String[] ipArray = ip.split(",");
            if (ipArray != null && ipArray.length > 1) {
                return ipArray[0];
            }
            return ip;
        }

        return "";
    }

}
