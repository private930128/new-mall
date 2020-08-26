package ltd.newbee.mall.aspect;

import ltd.newbee.mall.util.ThreadLocalUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class RequestLogAspect {

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void postMapping() {
    }

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void requestMapping() {
    }

    @AfterReturning(pointcut = "(postMapping() || requestMapping())", returning = "result")
    public void afterReturning(JoinPoint joinPoint, Object result) {
        //返回结果
        ThreadLocalUtil.setResult( result );
    }
}
