package kr.co.thkc.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
final public class MutableHttpServletRequest extends HttpServletRequestWrapper {

    private final Map<String, String> headers;
    public final static String EROUM_USER_ID = "eroum_user_id";

    private final Charset encoding;
    private byte[] rawData;
    private Map<String, String[]> params = new HashMap<>();
    public Map<String, Object> requestBody = new HashMap<>();

    private List<MultipartFile> files;

    public MutableHttpServletRequest(HttpServletRequest request) {
        super(request);
        headers = new HashMap<>();


        /**
         * 2021-01-26 MH : ResponseBody 로 받은 데이터를 Parameter로 변환한다.
         */
        this.params.putAll(request.getParameterMap()); // 원래의 파라미터를 저장
        String charEncoding = request.getCharacterEncoding(); // 인코딩 설정
        this.encoding = charEncoding == null || charEncoding.isEmpty() ? StandardCharsets.UTF_8 : Charset.forName(charEncoding);
        try {
            InputStream is = request.getInputStream();
            this.rawData = IOUtils.toByteArray(is); // InputStream 을 별도로 저장한 다음 getReader() 에서 새 스트림으로 생성

            // body 파싱
            String collect = this.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

            if (collect.isEmpty()) { // body 가 없을경우 로깅 제외
                return;
            }
            // if (request.getContentType() != null && request.getContentType().contains(
            // 	ContentType.MULTIPART_FORM_DATA.getMimeType())) { // 파일 업로드시 로깅제외
            // 	return;
            // }
            JSONParser jsonParser = new JSONParser();
            Object parse = jsonParser.parse(collect);
            if (parse instanceof JSONArray) {
                JSONArray jsonArray = (JSONArray) jsonParser.parse(collect);
                setParameter("requestBody", jsonArray.toJSONString());
            } else {
                JSONObject jsonObject = (JSONObject) jsonParser.parse(collect);
                Iterator iterator = jsonObject.keySet().iterator();
                while (iterator.hasNext()) {
                    String key = (String) iterator.next();
                    setParameter(key, jsonObject.get(key).toString().replace("\"", "\\\""));
                }
            }

        } catch (Exception e) {
            log.error("MutableHttpServletRequest init error", e);
        }
    }

    public void putHeader(String name, String value) {
        headers.put(name, value);
    }

    public String getHeader(String name) {
        String value = headers.get(name);
        if (value != null) {
            return value;
        }
        return ((HttpServletRequest) getRequest()).getHeader(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        Set<String> values = new HashSet<>();
        values.add(this.headers.get(name));

        Enumeration<String> underlyingValues = ((HttpServletRequest) getRequest()).getHeaders(name);
        while (underlyingValues.hasMoreElements()) {
            values.add(underlyingValues.nextElement());
        }
        return Collections.enumeration(values);
    }

    public Enumeration<String> getHeaderNames() {
        Set<String> headers = new HashSet<String>(this.headers.keySet());

        Enumeration<String> underlyingHeaders = ((HttpServletRequest) getRequest()).getHeaderNames();
        while (underlyingHeaders.hasMoreElements()) {
            headers.add(underlyingHeaders.nextElement());
        }
        return Collections.enumeration(headers);
    }


    //
    @Override
    public String getParameter(String name) {
        String[] paramArray = getParameterValues(name);
        if (paramArray != null && paramArray.length > 0) {
            return paramArray[0];
        } else {
            return null;
        }
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return Collections.unmodifiableMap(params);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(params.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] result = null;
        String[] dummyParamValue = params.get(name);

        if (dummyParamValue != null) {
            result = new String[dummyParamValue.length];
            System.arraycopy(dummyParamValue, 0, result, 0, dummyParamValue.length);
        }
        return result;
    }

    public void setParameter(String name, String value) {
        String[] param = {value};
        setParameter(name, param);
    }

    public void setParameter(String name, String[] values) {
        params.put(name, values);
    }

    @Override
    public ServletInputStream getInputStream() {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(this.rawData);

        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                // Do nothing
            }

            public int read() {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), this.encoding));
    }
}
