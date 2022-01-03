package kr.co.thkc.interceptor;

import kr.co.thkc.service.ConfigService;
import kr.co.thkc.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public class WhiteListInterceptor extends HandlerInterceptorAdapter {
  @Autowired
  ConfigService configService;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    boolean isAllowed = false;

    List<Map<String,String>> whiteList = configService.selectWhiteList();

    String clientIp = CommonUtils.getClientIpAddr(request);

    for (Map<String, String> map : whiteList) {
      for (Map.Entry<String, String> entry : map.entrySet()) {
        if (clientIp.equals(entry.getValue())) {
          isAllowed = true;
        }
      }
    }

    if (!isAllowed) {
      throw new Exception("Your IP [" + clientIp + "] is not allowed to access this API");
    }

    return super.preHandle(request, response, handler);
  }
}
