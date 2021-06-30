package kr.co.thkc.controller;

import org.springframework.stereotype.Controller;


@Controller
public class BaseController {
    public final static String baseUrl = "api";

    /**
     * 실행중인 클래스(컨트롤러) 이름
     *
     * @param obj - 해당 클래스 this
     */
    public static String getClassName(Object obj) {
        String[] className = obj.getClass().getName().split("\\.");
        return className[className.length - 1];
    }

    /**
     * 실행중인 메소드 이름
     */
    public static String getMethodName() {
        return Thread.currentThread().getStackTrace()[3].getMethodName();
    }

}
