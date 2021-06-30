package kr.co.thkc.mybatis;

import kr.co.thkc.utils.SDBCryptUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * mybatis에서 map으로 return 받을 때 Camel Case로 사용
 *
 * @author Jinwoo
 * @reference https://chois9105.github.io/spring/2017/12/31/configuring-mybatis-underscore-to-camel-case.html
 */
public class CamelListMap extends ListOrderedMap<Object, Object> {
    /**
     *
     */
    private final String decryptCode = "SDB_";
    private static final long serialVersionUID = 1L;

    private String toProperCase(String s, boolean isCapital) {

        String rtnValue = "";

        if (isCapital) {
            rtnValue = s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        } else {
            rtnValue = s.toLowerCase();
        }
        return rtnValue;
    }

    private String toCamelCase(String s) {
        String[] parts = s.split("_");
        StringBuilder camelCaseString = new StringBuilder();

        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            camelCaseString.append(toProperCase(part, (i != 0 ? true : false)));
        }

        return camelCaseString.toString();
    }

    @SneakyThrows
    @Override
    public Object put(Object key, Object value) {
        if (key.toString().startsWith(decryptCode) && value != null) {
            try {
                SDBCryptUtil sdb = new SDBCryptUtil();
                value = sdb.decrypt(value.toString());
                key = key.toString().replaceFirst(decryptCode, "");
                sdb = null;
            } catch (Exception e) {
                throw new Exception(value + " can't decrypt");
            }
        }
        if (value instanceof Date) {
            value = new SimpleDateFormat("yyyyMMddHHmmss").format(value);
        }
        return super.put(toCamelCase((String) key), (value == null || value.equals(" ") ? null : value));
    }

}
