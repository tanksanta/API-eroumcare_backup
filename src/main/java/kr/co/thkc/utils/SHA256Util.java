package kr.co.thkc.utils;

import org.apache.tomcat.util.codec.binary.Base64;

import java.security.MessageDigest;

public class SHA256Util {
    /**
     * 비밀번호를 암호화하는 기능(복호화가 되면 안되므로 SHA-256 인코딩 방식 적용)
     *
     * @param password 암호화될 패스워드
     * @param id       salt로 사용될 사용자 ID 지정
     * @return
     * @throws Exception
     */
    public static String encryptPassword(String password, String id) throws Exception {
        if (password == null) {
            return "";
        }

        byte[] hashValue = null; // 해쉬값

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        md.reset();
        md.update(id.getBytes());

        hashValue = md.digest(password.getBytes());

        return new String(Base64.encodeBase64(hashValue));
    }


    public static void main(String agrs[]) throws Exception {
        String userId = "123456789";
        String userPw = "1q2w3e";
        String encryptPw = encryptPassword(userPw, userId);
        System.out.println("admin encryptPw=========>" + encryptPw);
    }
}
