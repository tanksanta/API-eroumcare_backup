package kr.co.thkc.utils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.Period;

/**
 * 2020-07-03 HoonyB : 공통적인 유틸함수를 사용하기 위하여 생성.
 */
public class CommonUtils {
    

    /**
     * 클라이언트(접속자) IP 얻기위한 함수
     */
    public static String getClientIpAddr(HttpServletRequest request) {
		String clientIp = request.getHeader("X-Forwarded-For");
		if (StringUtil.isEmpty(clientIp)|| "unknown".equalsIgnoreCase(clientIp)) {
			//Proxy 서버인 경우
			clientIp = request.getHeader("Proxy-Client-IP");
		}
		if (StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
			//Weblogic 서버인 경우
			clientIp = request.getHeader("WL-Proxy-Client-IP");
		}
		if (StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getHeader("HTTP_CLIENT_IP");
		}
		if (StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (StringUtil.isEmpty(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
			clientIp = request.getRemoteAddr();
		}

        return clientIp;
    }


}





    