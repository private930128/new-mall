package ltd.newbee.mall.util;

import javax.validation.constraints.NotEmpty;
import java.util.Map;

public final class ThreadLocalUtil {

    /**
     * 请求入参序列号
     */
    private static final ThreadLocal<String> SERIAL_NUM = new ThreadLocal<>();

    /**
     * 请求入参
     */
    private static final ThreadLocal<Map<String, Object>> PARAMETER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 请求返回结果
     */
    private static final ThreadLocal<Object> RESULT_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 请求开始时间节点
     */
    private static final ThreadLocal<Long> TIME_START_THREAD_LOCAL = new ThreadLocal<>();


    public static String getMarketSerialNum() {
        return SERIAL_NUM.get();
    }

    public static void setMarketSerialNum(@NotEmpty String serialNum) {
        SERIAL_NUM.set( serialNum );
    }

    public static void setParameter(Map<String, Object> parameter) {
        PARAMETER_THREAD_LOCAL.set(parameter);
    }

    public static Map<String, Object> getParameter() {
        return PARAMETER_THREAD_LOCAL.get();
    }

    public static void setResult(Object result) {
        RESULT_THREAD_LOCAL.set(result);
    }

    public static Object getResult() {
        return RESULT_THREAD_LOCAL.get();
    }

    public static void setTimeStart(Long start) {
        TIME_START_THREAD_LOCAL.set( start );
    }

    public static Long getTimeStart() {
        return TIME_START_THREAD_LOCAL.get();
    }

    public static void clearThreadLocal() {
        SERIAL_NUM.remove();
        PARAMETER_THREAD_LOCAL.remove();
        RESULT_THREAD_LOCAL.remove();
        TIME_START_THREAD_LOCAL.remove();
    }

}
