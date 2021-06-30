package kr.co.thkc.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Enumeration;


@Slf4j
@WebFilter(urlPatterns = {"/api/*"}, description = "필터")
public class DefaultFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ModifyRequestWrapper modifyReq = new ModifyRequestWrapper((HttpServletRequest) request);
        MutableHttpServletRequest req = new MutableHttpServletRequest(modifyReq);
        HttpServletResponse res = (HttpServletResponse) response;

        Enumeration<?> headerNames = req.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = (String) headerNames.nextElement();
            String value = req.getHeader(name);
            // log.info("[HEADER] {} = {}", name, value);
        }

        Enumeration<?> attributeNames = req.getAttributeNames();
        while (attributeNames.hasMoreElements()) {
            String name = (String) attributeNames.nextElement();
            String value = req.getParameter(name);
            // log.info("[ATTRIBUTE] {} = {}", name, value);
        }

        Enumeration<?> parameterNames = req.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = (String) parameterNames.nextElement();
            String value = req.getParameter(name);
            // log.info("[PARAMETER] {} = {}", name, value);
        }

        res.setStatus(HttpServletResponse.SC_OK);
        res.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE");
        res.addHeader("Access-Control-Allow-Headers",
                "Origin,Accept,X-Requested-With,Content-Type,Access-Control-Request-Method,Access-Control-Request-Headers,Authorization");
        res.addHeader("Access-Control-Allow-Origin", "*");
        res.addHeader("Access-Control-Max-Age", "3600");

        switch (req.getMethod()) {
            case "GET":
            case "POST":
            case "PUT":
                log.info("");
                log.info("=================================[NEW REQUEST]=================================");
                log.info("[URI] {} [METHOD] {}", req.getRequestURI(), req.getMethod());
                log.info("");

                break;
            // CORS 체크를 위해 예외 처리
            case "OPTIONS":
                break;
            default:
                res.sendError(HttpServletResponse.SC_BAD_REQUEST, "허용되지 않는 접근");
                return;
        }

        chain.doFilter(req, res);
    }

    @Override
    public void destroy() {

    }
}
