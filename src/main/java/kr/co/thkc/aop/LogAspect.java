package kr.co.thkc.aop;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;

import java.util.Map;


@Aspect
@Component
@EnableAspectJAutoProxy
@Slf4j
public class LogAspect {

    //서비스 로그
    @Around("execution(kr.co.thkc.dispatch.BaseResponse kr.co.thkc.service..*.*(..)) " +
            "&& !@annotation(kr.co.thkc.aop.NoAspect)")
    public BaseResponse serviceLog(ProceedingJoinPoint pjp) throws Throwable {
        BaseResponse result = new BaseResponse();

        Map<String,Object> params = (Map<String,Object>)pjp.getArgs()[0]; // params 값 가져오기
        log.info("REQUEST: {} => {}", pjp.getSignature().getName(),
                StringUtil.convertMapToJSONstring(params));

        result = (BaseResponse)pjp.proceed(); // reponse 값 가져오기
        String resultData = result.getData() != null ? result.getData().toString() : "null";
        int resLen = resultData.length();
        log.info("RESPONSE: {} => errorYN:{} | message:{}", pjp.getSignature().getName(),
                result.getErrorYN(), result.getMessage());
        log.trace("RESPONSE: resultData={}", resultData.substring(1,resLen>2000?2000:resLen)+"...");

        return result;
    }

}
