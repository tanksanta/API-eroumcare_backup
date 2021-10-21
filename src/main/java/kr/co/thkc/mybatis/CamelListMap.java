package kr.co.thkc.mybatis;

import kr.co.thkc.utils.SDBCryptUtil;
import lombok.SneakyThrows;
import org.apache.commons.collections4.map.ListOrderedMap;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    // 수급자 정보 마스킹
    private final String[] NAME = {"SDB_PEN_NM", "SDB_PEN_PRO_NM"};
    private final String[] LTM_NUM = {"SDB_PEN_LTM_NUM"};
    private final String[] PHONE = {"SDB_PEN_CON_NUM", "SDB_PEN_CON_PNUM", "SDB_PEN_PRO_CON_NUM", "SDB_PEN_PRO_CON_PNUM"};

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

                // 이름
                if (Arrays.asList(NAME).contains(key.toString()) && value.toString().length() > 1) {
                    value = nameMasking(value.toString());
                }
                // 핸드폰
                if (Arrays.asList(LTM_NUM).contains(key.toString()) && value.toString().length() > 5) {
                    value = ltmNumMasking(value.toString());
                }
                // 연락처
                if (Arrays.asList(PHONE).contains(key.toString()) && value.toString().length() > 5) {
                    value = phoneMasking(formatPhone(value.toString()));
                }

                // 키 원래대로 수정
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

    public static String nameMasking(String name) throws Exception {
        String result;

        if (name.length() <= 2) {
            result = name.substring(0, 1) + new String(new char[name.length() - 1]).replace("\0", "*");;
        } else {
            result = name.substring(0, 2) + new String(new char[name.length() - 2]).replace("\0", "*");;
        }

        return result;
    }

    public static String ltmNumMasking(String num) throws Exception {
        return num.substring(0, 6) + "*****";
    }

    public static String phoneMasking(String phoneNo) throws Exception {
//        String regex = "(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";
//        Matcher matcher = Pattern.compile(regex).matcher(phoneNo);
//        if (matcher.find()) {
//            String target = matcher.group(2);
//            int length = target.length();
//            char[] c = new char[length];
//            Arrays.fill(c, '*');
//            return phoneNo.replace(target, String.valueOf(c));
//        }

        String head = phoneNo.substring(0, 5);
        String tail = phoneNo.substring(phoneNo.length() - 1, phoneNo.length());
        String maskedPhoneNo;

        if (phoneNo.length() == 13) {
            maskedPhoneNo = head + "***-***" + tail;
        } else {
            maskedPhoneNo = head + "**-***" + tail;
        }

        return maskedPhoneNo;
    }

    public static String formatPhone(String src) {
        if (src == null) {
            return "";
        }
        if (src.length() == 8) {
            return src.replaceFirst("^([0-9]{4})([0-9]{4})$", "$1-$2");
        } else if (src.length() == 12) {
            return src.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$", "$1-$2-$3");
        }
        return src.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$", "$1-$2-$3");
    }
}
