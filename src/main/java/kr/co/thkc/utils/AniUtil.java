package kr.co.thkc.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AniUtil {
    
    private static final String cryptKey = "ipron-swat";
    private static final int cryptBits = 128;
    private static final AesUtil aesUtil = new AesUtil().initAes(cryptKey, cryptBits);
    

    /**
     * ani AES 암호화 여부 확인
     */
    public static boolean encValidation(String ani) {
		Boolean result = false;
		try {
			for (char digit : ani.toCharArray()) {
				if ((digit >= 97 && digit <= 122) || (digit >= 65 && digit <= 90)) {
					result = true;
					break;
				}
			}
		} catch(Exception e ) {
			log.error(e.getMessage());
		}
		return result;
    }
    
    /**
     * AES 암호화
     * @param ani
     * @return
     * @throws Exception
     */
    public static String encryptAni(String ani) {
		String returnValue = ani;

		if (returnValue != null) {
			// - 포함되어 있으면 삭제
			//returnValue = returnValue.replaceAll("-", "");
			if (encValidation(ani) == false) {
				returnValue = aesUtil.encrypt(ani);
			}
		}
		return returnValue;
    }
    
    /**
     * 복호화
     */
    public static String decryptAni(String ani) {
		String returnValue = ani;
		try {
			if (ani != null ) {
				returnValue = aesUtil.decrypt(ani);
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return returnValue;
	}
}