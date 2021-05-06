package kr.co.thkc.utils;


import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class AuthKey {
	public static void main(String[] args) {
		//AuthKey authKey = new AuthKey();
		String sAuthKey = generate(6000, 12345678);
		isValid(sAuthKey);
	}

	public static boolean isValid(String authKey) {
		String plain = getPlainString(authKey);
		if (plain != null && plain.length()>0) {
			log.info("AuthKey = " + plain);
			long expired = Long.parseLong(plain.substring(0, 14));
			long now = Long.parseLong(getDateString(new Date()));
			return expired >= now;
		}
		return false;
	}

	public static String getPlainString(String authKey) {
		return aesUtil.decrypt(authKey);
	}

	public static Integer getUserId(String authKey) {
		String plain = getPlainString(authKey);
		if (plain != null && plain.length()>0) {
			log.info("AuthKey = " + plain);
			long expired = Long.parseLong(plain.substring(0, 14));
			long now = Long.parseLong(getDateString(new Date()));
			if(expired >= now) {
				return Integer.parseInt(plain.substring(14));
			}
		}
		return null;
	}

	public static String getExpireDate(String authKey) {
		String plain = getPlainString(authKey);
		if (plain != null && plain.length()>0) {
			return plain.substring(0, 14);
		}
		return null;
	}

	public static String generate(int minutes, int userId) {
		Date expireDate = getExpiredDate(minutes);
		String plain = getDateString(expireDate) + userId;
		String authKey = aesUtil.encrypt(plain);
		log.debug("AuthKey = " + authKey);
		return authKey;
	}

    private static String getDateString(Date date) {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(date);
    }

    private static Date getExpiredDate(int minutes) {
    	Date date = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MINUTE, minutes);
		return calendar.getTime();
	}

    private static final String cryptKey = "swat-webservice";
    private static final int cryptBits = 128;
    private static final AesUtil aesUtil = new AesUtil().initAes(cryptKey, cryptBits);
}
