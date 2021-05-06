package kr.co.thkc.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SHA512Util {
    
	public static String SHA512(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("SHA-512");
		byte[] sha1hash = new byte[40];
		md.update(text.getBytes("UTF-8"), 0, text.length());
		sha1hash = md.digest();
		return convertToHex(sha1hash);
	}

	public static String EncryptPassword(String userId, String pass) {
		String returnValue = "";

		try {
			returnValue = SHA512Util.SHA512(userId + SHA512Util.SHA512(pass));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
		}

		return returnValue;
	}

	private static String convertToHex(byte[] data) {
	    byte temp;
	    String out = "";
	    String s = "";
		
		for (int i = 0; i < data.length; i++) {
            temp = data[i];
            s = Integer.toHexString(new Byte(temp));
            while (s.length() < 2) {
                s = "0" + s;
            }
            s = s.substring(s.length() - 2);
            out += s;
        }
		
		return out;
	}
	
	public static void main(String agrs[]) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		/*
		admin        cc11622a6adb47a759a2ec223628e43401b05820  1
		dgkim        49cf21a3d49d49bb19d38ec00510cd6a60b463fc  1
		monitor      90953249d19120c2b2473198b5bc7c36f12eb561  1
		*/
		String userId = "admin";
		String userPw = "1";
		String encryptPw = SHA512(userId + SHA512(userPw));
		System.out.println("admin encryptPw=========>"+encryptPw);
		
		userId = "dgkim";
		userPw = "1";
		encryptPw = SHA512(userId + SHA512(userPw));
		System.out.println("dgkim encryptPw=========>"+encryptPw);
		
		userId = "monitor";
		userPw = "1";
		encryptPw = SHA512(userId + SHA512(userPw));
		System.out.println("monitor encryptPw=========>"+encryptPw);
	}
}
