package ltd.newbee.mall.interceptor;

import ltd.newbee.mall.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * newbee-mall系统身份验证拦截器
 *
 * @author 13
 * @qq交流群 796794009
 * @email 2449207463@qq.com
 * @link https://github.com/newbee-ltd
 */
@Component
public class NewBeeMallLoginInterceptor implements HandlerInterceptor {

    private static Logger LOGGER = LoggerFactory.getLogger(AdminLoginInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        LOGGER.info("2222----" + request.getContextPath());
        if (null == request.getSession().getAttribute(Constants.MALL_USER_SESSION_KEY)) {
            LOGGER.info("2222----here" + request.getContextPath());
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
