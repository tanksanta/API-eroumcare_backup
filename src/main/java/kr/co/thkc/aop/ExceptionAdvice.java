package kr.co.thkc.aop;

import kr.co.thkc.dispatch.BaseResponse;
import kr.co.thkc.dispatch.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionAdvice {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> except(Exception ex){
        BaseResponse response = new BaseResponse();

        if(ex.getMessage() == null){
            if(ex instanceof NullPointerException){
                String str = "";
                for (StackTraceElement element : ex.getStackTrace()) {
                    str += element + "\n";
                }
                response.setResult(ResultCode.RC_OK, str);
                log.error("EXCEPTION: {}", str);
            } else {
                response.setResult(ResultCode.RC_OK, ex.getCause().getMessage());
                log.error("EXCEPTION: {}", ex.getCause().getMessage());
            }
        }else {
            response.setResult(ResultCode.RC_FAIL, ex.getMessage());
            log.error("EXCEPTION: {}",ex.getMessage());
        }


        return new ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
