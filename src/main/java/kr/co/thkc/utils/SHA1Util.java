package kr.co.thkc.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA1Util {
    public static String SHA1(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        MessageDigest md;
        md = MessageDigest.getInstance("SHA1");
        byte[] sha1hash = new byte[40];
        md.update(text.getBytes("UTF-8"), 0, text.length());
        sha1hash = md.digest();
        return convertToHex(sha1hash);
    }

    private static String convertToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while (two_halfs++ < 1);
        }
        return buf.toString();
    }

    public static void main(String agrs[]) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		/*
		admin        cc11622a6adb47a759a2ec223628e43401b05820  1
		dgkim        49cf21a3d49d49bb19d38ec00510cd6a60b463fc  1
		monitor      90953249d19120c2b2473198b5bc7c36f12eb561  1
		*/
        String userId = "admin";
        String userPw = "1";
        String encryptPw = SHA1(userId + SHA1(userPw));
        System.out.println("admin encryptPw=========>" + encryptPw);

        userId = "dgkim";
        userPw = "1";
        encryptPw = SHA1(userId + SHA1(userPw));
        System.out.println("dgkim encryptPw=========>" + encryptPw);

        userId = "monitor";
        userPw = "1";
        encryptPw = SHA1(userId + SHA1(userPw));
        System.out.println("monitor encryptPw=========>" + encryptPw);
    }
}
