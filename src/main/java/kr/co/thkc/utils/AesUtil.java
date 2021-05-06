package kr.co.thkc.utils;

import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Slf4j
public class AesUtil {

	private SecretKeySpec secretKeySpec;


	
	public AesUtil initAes(String key, int keyBits) {
		int keySize = getKeySize(keyBits);
		byte[] keyBytes = new byte[keySize];
		for (int i=0; i<key.getBytes().length && i<keySize; i++) {
			keyBytes[i] = key.getBytes()[i];
		}
		secretKeySpec = new SecretKeySpec(keyBytes, "AES");
		return this;
	}

	

	public String encrypt(String content) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
			return Base64.getEncoder().encodeToString(cipher.doFinal(content.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}

	public String decrypt(String content) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
			byte[] bytes = Base64.getDecoder().decode(content.getBytes(StandardCharsets.UTF_8));
			return new String(cipher.doFinal(bytes));
		} catch (Exception e) {
			log.error(e.getLocalizedMessage());
			e.printStackTrace();
		}
		return null;
	}

	private int getKeySize(int keyBits) {
		switch (keyBits) {
			default:		// 기본 128 bit 방식 사용
			case 128:
				return 16;
			case 192:
				return 24;
			case 256:
				return 32;
		}
	}

	
}
