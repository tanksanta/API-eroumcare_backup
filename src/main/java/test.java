import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class test {
    public static void main(String[] args) throws ParseException {
//        SDBCryptUtil sdbCryptUtil = new SDBCryptUtil();
//
//        System.out.println("010-8748-7796 / " + sdbCryptUtil.encrypt("010-8748-7796"));
//        System.out.println("010-6317-2402 / " + sdbCryptUtil.encrypt("010-6317-2402"));
//        System.out.println("010-0000-0000 / " + sdbCryptUtil.encrypt("010-0000-0000"));
//        System.out.println("010-1234-1324 / " + sdbCryptUtil.encrypt("010-1234-1324"));
//        System.out.println("010-1231-1231 / " + sdbCryptUtil.encrypt("010-1231-1231"));
//        System.out.println("010-000-0000 / " + sdbCryptUtil.encrypt("010-000-0000"));
//        System.out.println("010-7777-7777 / " + sdbCryptUtil.encrypt("010-7777-7777"));
//        System.out.println("010-3214-3215 / " + sdbCryptUtil.encrypt("010-3214-3215"));
//        System.out.println("010-0000-1111 / " + sdbCryptUtil.encrypt("010-0000-1111"));
//        System.out.println("010-000-1111 / " + sdbCryptUtil.encrypt("010-000-1111"));
//        System.out.println("010-2551-8080 / " + sdbCryptUtil.encrypt("010-2551-8080"));
//        System.out.println("010-6776-9303 / " + sdbCryptUtil.encrypt("010-6776-9303"));
//        System.out.println("010-4051-4887 / " + sdbCryptUtil.encrypt("010-4051-4887"));
        List<Map> option = new ArrayList<>();
        Map test = new HashMap() {{
            put("test", 1);
        }};
        option.add(test);
        System.out.println("list : " + option);

        for (Map item : option) {
            item.put("test", 2);
        }
        System.out.println("list : " + option);

    }
}
