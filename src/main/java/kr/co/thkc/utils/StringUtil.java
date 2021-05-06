package kr.co.thkc.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;

@Slf4j
public class StringUtil {


    public static String toCamelCase(String s) {
        String[] parts = s.split("_");
        StringBuilder camelCaseString = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            camelCaseString.append(toProperCase(part, (i != 0 ? true : false)));
        }
        return camelCaseString.toString();
    }

    
    public static String toProperCase(String s, boolean isCapital) {
        String returnValue = "";
        if (isCapital) {  returnValue = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        } else {
            returnValue = s.toLowerCase();
        }
        return returnValue;
    }


    // CamelCase(helloWorld) to PotholeCase(HELLO_WORLD)
    public static String toPotholeCase(String str) {
        String returnValue = "";
        for (int i = 0; i < str.length(); i++) {
            if (str.substring(i, i + 1).matches("[A-Z0-9]")) {
                returnValue += "_" + str.substring(i, i + 1).toUpperCase();
            } else {
                returnValue += str.substring(i, i + 1).toUpperCase();
            }
        }
        
        return returnValue;
    }

    // QUERY SELECT 한 결과값을 CamelCase 형태의 Map 으로 변환한다.
    public static ArrayList<Map<String, Object>> ResultSetToCamleCase(ResultSet resultSet) throws SQLException {
        ArrayList<Map<String, Object>> data = new ArrayList<>();
		ResultSetMetaData metaData = resultSet.getMetaData();
		while(resultSet.next()) {
			HashMap<String, Object> row = new HashMap<>();
			for (int index = 1; index <= metaData.getColumnCount() ; index++) {
				row.put(StringUtil.toCamelCase(metaData.getColumnName(index)), resultSet.getObject(index));
			}
			data.add(row);
        }
        resultSet.close();
        
        return data;
    }

    public static boolean isEmpty(String str) {
        boolean returnValue = false;

        if( str == null || str.isEmpty() ) {
            returnValue = true;
        }

        return returnValue;
    }

    
    /**
     * Json형식의 String을 Map 으로 변환한다.
     */
    public static Map<String,Object> convertJSONstringToMap(String json) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();
        final String fromRex = "((19|20)\\d{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])";
        final String toRex = "$1\\.$3\\.$4";
        try {
            json = json.replaceAll(fromRex,toRex);
            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(json, Map.class);
        } catch (Exception e) {
            log.error("convertJSONstringToMap fail : " +e.getMessage());
        }
        
        return map;
    }

    /**
     * 이스케이프 문자를 치환
     */
    public static String convertStringToEscape(String string) throws Exception {
        try {
            if(string.contains("\\/")) string = string.replace("\\/", "/");
            if(string.contains("&")) string = string.replaceAll("&", "&amp;");
            if(string.contains("<")) string = string.replaceAll("<", "&lt;");
            if(string.contains(">")) string = string.replaceAll(">", "&gt;");
            if(string.contains("'")) string = string.replaceAll("'", "&#x27;");
            if(string.contains("/")) string = string.replaceAll("/", "&#x2F;");
            if(string.contains("\\\\\\\"")||string.contains("\\\"")) string = string.replaceAll("((?<!\\\\)(\\\\\\\\)*)(\\\\\\\")", "$1&quot;");
        } catch (Exception e) {
            log.error("convertStringToEscape fail : " +e.getMessage());
        }

        return string;
    }

    /**
     * Map 데이터를 Json형식의 String으로 변환한다.
     * @param map
     * @return
     * @throws Exception
     */
    public static String convertMapToJSONstring( Map<String,Object> map) throws Exception {
        String json = null;
        try {
            ObjectMapper mapper = new ObjectMapper();
            json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
        } catch (Exception e) {
            log.error("convertMapToJSONstring fail : " +e.getMessage());
        }
        
        return json;
    }


    /**
     * Date 타입 포맷을 yyyy-MM-DD 형식의 String으로 변환한다.
     * @param dateStr
     * @return
     * @throws Exception
     */
    public static String dateFormmatConvert(String dateStr) throws Exception {
        final String fromRex = "^(19|20)\\d{2}-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[0-1])$";
        final String toRex = "$1\\.$2\\.$3";
        try {
            if(dateStr.matches(fromRex)){
                dateStr.replaceAll(fromRex,toRex);
            }
        } catch (Exception e) {
            log.error("dateFormmatConvert fail : " +e.getMessage());
        }

        return dateStr;
    }



    public static String setLPad( String strContext, int iLen, String strChar ) {
        String strResult = "";
        StringBuilder sbAddChar = new StringBuilder();
        for( int i = strContext.length(); i < iLen; i++ ) {
            // iLen길이 만큼 strChar문자로 채운다.
            sbAddChar.append( strChar );
        }
        strResult = sbAddChar + strContext;
        // LPAD이므로, 채울문자열 + 원래문자열로 Concate한다.
        return strResult;
    }


}