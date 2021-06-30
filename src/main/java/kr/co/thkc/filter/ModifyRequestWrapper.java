package kr.co.thkc.filter;

import kr.co.thkc.utils.CommonUtils;
import kr.co.thkc.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 2020-07-03 HoonyB : 최초 요청의 requestBody를 변조하기 위하여 생성
 * - RequestBody 에 accessIp(접속IP) 를 추가
 */
@Slf4j
public class ModifyRequestWrapper extends HttpServletRequestWrapper {

    private byte[] body;

    //매핑할 파라미터 변수명
    public final static String EROUM_PARAMS_NAME = "params";
    public final static String EROUM_FILES_NAME = "files";

    public ModifyRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);

        String requestStringBody = "";
        try {
            //Body의 값을 가져온다
            if (request.getContentType() != null && request.getContentType().contains("multipart/form-data")) {
                //multipart일 경우
                StandardServletMultipartResolver multipartResolver = new StandardServletMultipartResolver();
                MultipartHttpServletRequest multipartRequest = multipartResolver.resolveMultipart(request);

                //파라미터들을 묶어서 새로운 파라미터바디 생성
                Enumeration<String> enumeration = multipartRequest.getParameterNames();
                Map<String, Object> paramMap = new HashMap<>();
                while (enumeration.hasMoreElements()) {
                    String key = enumeration.nextElement();
                    paramMap.put(key, multipartRequest.getParameter(key));
                }
                requestStringBody = StringUtil.convertMapToJSONstring(paramMap);
                //파일을 묶어서 새로운 파일 리스트 생성

            } else {
                //그 외 json body
                InputStream is = super.getInputStream();
                body = IOUtils.toByteArray(is);
                requestStringBody = new String(body, "UTF-8");
            }
            requestStringBody = StringUtil.convertStringToEscape(requestStringBody);

            // request body 를 변경 하는 부분
            // request body 에 해당 하는 byte[] 을 바꿔주고 getInputStream 2번이 안되는 문제를 해결
            // ==> Body(JsonString) 을 map 으로 변환 후, accessIp를 put 후에 다시 JsonString 으로 변환하여 body를 대체한다.
            Map<String, Object> reqBody = new HashMap<>();
            Map<String, Object> params = new HashMap<String, Object>();
            if (requestStringBody.length() == 0) {
                params = new HashMap<>();
            } else {
                params = StringUtil.convertJSONstringToMap(requestStringBody);
            }
            params.put("accessIp", CommonUtils.getClientIpAddr(request));
            reqBody.put(EROUM_PARAMS_NAME, params);
            requestStringBody = StringUtil.convertMapToJSONstring(reqBody);

            body = new String(requestStringBody).getBytes("UTF-8");

        } catch (Exception e) {
            log.warn("requestStringBody : ", requestStringBody);
            log.warn("RequestBody Change Fail : ", e.getMessage());
        }
    }

    public ServletInputStream getInputStream() throws IOException {
        final ByteArrayInputStream bis = new ByteArrayInputStream(body);
        return new ServletInputStreamImpl(bis);
    }

    class ServletInputStreamImpl extends ServletInputStream {
        private InputStream is;

        public ServletInputStreamImpl(InputStream bis) {
            is = bis;
        }

        public int read() throws IOException {
            return is.read();
        }

        public int read(byte[] b) throws IOException {
            return is.read(b);
        }

        @Override
        public boolean isFinished() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean isReady() {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public void setReadListener(ReadListener listener) {
            // TODO Auto-generated method stub

        }
    }
}


